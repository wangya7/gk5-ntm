package wang.bannong.gk5.ntm.core.handler;

import com.google.common.base.Splitter;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import wang.bannong.gk5.ntm.core.inner.ConfigTools;
import wang.bannong.gk5.ntm.common.constant.ApiConfig;
import wang.bannong.gk5.ntm.common.constant.HttpMethod;
import wang.bannong.gk5.ntm.common.constant.NtmConfigSetting;
import wang.bannong.gk5.ntm.common.constant.NtmConstant;
import wang.bannong.gk5.ntm.common.model.NtmCookie;
import wang.bannong.gk5.ntm.common.model.NtmRequest;
import wang.bannong.gk5.ntm.common.model.NtmResult;
import wang.bannong.gk5.ntm.common.model.ResultCode;
import wang.bannong.gk5.util.Constant;
import wang.bannong.gk5.util.RSAUtils;

public class RequestHandler {

    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);

    public static NtmResult checkAndConvert2NtmRequest(HttpServletRequest servletRequest) {
        // 0. 首先构造【原因是及时入参报错也可以看见模型】
        String _ts = format(servletRequest.getHeader(ApiConfig.TS));
        String _lng = servletRequest.getHeader(ApiConfig.LNG);
        String _lat = servletRequest.getHeader(ApiConfig.LAT);
        String _ttid = servletRequest.getHeader(ApiConfig.TTID);
        String _appid = servletRequest.getHeader(ApiConfig.APPID);
        String _channel = servletRequest.getHeader(ApiConfig.CHANNEL);
        String _ia = format(servletRequest.getHeader(ApiConfig.IA));
        String _oia = format(servletRequest.getHeader(ApiConfig.OIA));
        String _api = servletRequest.getParameter(ApiConfig.API);
        String _v = servletRequest.getParameter(ApiConfig.V);
        String _sign = servletRequest.getParameter(ApiConfig.SIGN);
        String _data = format(servletRequest.getParameter(ApiConfig.DATA));
        log.info(NtmConstant.LOG_IN + "request[api={}, v={}, ts={}, lng={}, lat={}, ttid={}, appid={}, " +
                "channel={}, ia={}, oia={}, data={}, sign={}]", _api, _v, _ts, _lng, _lat, _ttid, _appid,
                _channel, _ia, _oia, _data, _sign);

        // 1. 校验非空
        if (StringUtils.isBlank(_ts)) return NtmResult.of(ResultCode.header_miss_arg, ApiConfig.TS);
        if (StringUtils.isBlank(_lng)) return NtmResult.of(ResultCode.header_miss_arg, ApiConfig.LNG);
        if (StringUtils.isBlank(_lat)) return NtmResult.of(ResultCode.header_miss_arg, ApiConfig.LAT);
        if (StringUtils.isBlank(_ttid)) return NtmResult.of(ResultCode.header_miss_arg, ApiConfig.TTID);
        if (StringUtils.isBlank(_appid)) return NtmResult.of(ResultCode.header_miss_arg, ApiConfig.APPID);
        if (StringUtils.isBlank(_channel)) return NtmResult.of(ResultCode.body_miss_arg, ApiConfig.CHANNEL);
        if (StringUtils.isBlank(_api)) return NtmResult.of(ResultCode.body_miss_arg, ApiConfig.API);
        if (StringUtils.isBlank(_v)) return NtmResult.of(ResultCode.body_miss_arg, ApiConfig.V);
        if (StringUtils.isBlank(_sign)) return NtmResult.of(ResultCode.body_miss_arg, ApiConfig.SIGN);

        // 2. 校验参数类型
        int version = 0;
        try {
            version = Integer.parseInt(_v);
        } catch (NumberFormatException e) {
            log.error(NtmConstant.LOG_IN + "request v={}", _v);
            return NtmResult.of(ResultCode.arg_format_illegal, ApiConfig.V);
        }

        long timeMs = 0;
        try {
            timeMs = Long.parseLong(_ts);
        } catch (NumberFormatException e) {
            log.error(NtmConstant.LOG_IN + "request ts={}", _ts);
            return NtmResult.of(ResultCode.arg_format_illegal, ApiConfig.TS);
        }

        List<String> arr = null;
        try {
            arr = Splitter.on(Constant.AT).trimResults().splitToList(_ttid);
        } catch (Exception e) {
            log.error(NtmConstant.LOG_IN + "request ttid={}", _ttid);
            return NtmResult.of(ResultCode.arg_format_illegal, ApiConfig.TTID);
        }
        if (CollectionUtils.isEmpty(arr) || arr.size() != 4) {
            log.error(NtmConstant.LOG_IN + "request ttid={}", _ttid);
            return NtmResult.of(ResultCode.arg_format_illegal, ApiConfig.TTID);
        }

        String datas = null;
        try {
            datas = URLDecoder.decode(_data, Constant.UTF8);
        } catch (UnsupportedEncodingException e) {
            log.error(NtmConstant.LOG_IN + "request data decode fail, data={}", _data);
            return NtmResult.of(ResultCode.arg_format_illegal, ApiConfig.DATA);
        }
        Map<String, String> bizData = null;
        if (StringUtils.isBlank(datas)) {
            bizData = Collections.EMPTY_MAP;
        } else {
            try {
                bizData = JSON.parseObject(datas, new TypeReference<Map<String, String>>() {});
            } catch (Exception e) {
                log.error(NtmConstant.LOG_IN + "request data parsed fail, data={}", _data);
                return NtmResult.of(ResultCode.arg_format_illegal, ApiConfig.DATA);
            }
        }

        // 3. 处理cookie
        NtmCookie _ntmCookie = NtmCookie.of(servletRequest);
        if (StringUtils.isNotBlank(_ia)) {
            log.info(NtmConstant.LOG_IN + "ia(header)={}, api={}, ttid={}", _ia, _api, _ttid);
        } else {
            _ia = format(_ntmCookie.getCookieValue(ApiConfig.IA));
            log.info(NtmConstant.LOG_IN + "ia(cookie)={}, api={}, ttid={}", _ia, _api, _ttid);
        }

        // 4. 时间校验
        if (timeMs != 20190801) {
            long now = System.currentTimeMillis();
            log.info("校验时间戳，当前时间【{}】，客户端时间【{}】", now, timeMs);
            long tsThreshold = ConfigTools.getLong(NtmConfigSetting.TS_THRESHOLD_CONFIG, NtmConfigSetting.TS_THRESHOLD);
            if (now - timeMs > tsThreshold) {
                return NtmResult.of(ResultCode.request_timeout);
            }
        }

        boolean checkSign = ConfigTools.getBoolean(NtmConfigSetting.CHECK_SIGN_CONFIG, NtmConfigSetting.sign);
        // 5. 签名校验
        if (checkSign) {
            String signArgs = String.format(ApiConfig.SIGN_FORMAT, _api, _appid, _channel, _data, _ia, _ts, _ttid, _v);
            log.info(NtmConstant.LOG_IN + "sign 加密的text={}", signArgs);
            String text = RSAUtils.decrypt(RSAUtils.base64Decode(_sign), RSAUtils.getPrivateKey(NtmConfigSetting.privateKey));
            log.info(NtmConstant.LOG_IN + "sign 解密的text={}", text);
            if (!text.equals(signArgs)) {
                return NtmResult.of(ResultCode.sign_invalid);
            }
        }

        NtmRequest ntmRequest = NtmRequest.builder()
                .api(_api).v(version).ts(timeMs).lng(_lng).lat(_lat)
                .ttid(new NtmRequest.Ttid(arr.get(0), arr.get(1), arr.get(2), arr.get(3)))
                .appid(_appid).channel(_channel).sign(_sign).ia(_ia).oia(_oia).data(bizData)
                .ip(getIp(servletRequest))
                .method(HttpMethod.map.get(StringUtils.upperCase(servletRequest.getMethod())))
                .build();
        log.info(NtmConstant.LOG_IN + "request bo={}", ntmRequest);
        return NtmResult.success(ntmRequest);
    }


    // TODO 经过NGINX代理获得的IP错误
    private static String getIp(HttpServletRequest servletRequest) {
        String ip = null;
        //X-Forwarded-For：Squid 服务代理
        String ipAddresses = servletRequest.getHeader("X-Forwarded-For");
        if (ipAddresses == null || ipAddresses.length() == 0 || "unknown".equalsIgnoreCase(ipAddresses)) {
            //Proxy-Client-IP：apache 服务代理
            ipAddresses = servletRequest.getHeader("Proxy-Client-IP");
        }

        if (ipAddresses == null || ipAddresses.length() == 0 || "unknown".equalsIgnoreCase(ipAddresses)) {
            //WL-Proxy-Client-IP：weblogic 服务代理
            ipAddresses = servletRequest.getHeader("WL-Proxy-Client-IP");
        }

        if (ipAddresses == null || ipAddresses.length() == 0 || "unknown".equalsIgnoreCase(ipAddresses)) {
            //HTTP_CLIENT_IP：有些代理服务器
            ipAddresses = servletRequest.getHeader("HTTP_CLIENT_IP");
        }

        if (ipAddresses == null || ipAddresses.length() == 0 || "unknown".equalsIgnoreCase(ipAddresses)) {
            //X-Real-IP：nginx服务代理
            ipAddresses = servletRequest.getHeader("X-Real-IP");
        }

        //有些网络通过多层代理，那么获取到的ip就会有多个，一般都是通过逗号（,）分割开来，并且第一个ip为客户端的真实IP
        if (ipAddresses != null && ipAddresses.length() != 0) {
            ip = ipAddresses.split(",")[0];
        }

        //还是不能获取到，最后再通过request.getRemoteAddr();获取
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ipAddresses)) {
            ip = servletRequest.getRemoteAddr();
        }
        return ip;
    }


    public static String format(String value) {
        return StringUtils.trim(StringUtils.defaultString(value, Constant.BLANK));
    }


    public static void main(String... args) throws UnsupportedEncodingException {
        // DEMO_1
        String _api = "chech.test.demo";
        String _appid = "chech";
        String _channel = "hz";
        String _data = "";
        String _ia = "";
        String _ts = "20190801";
        String _ttid = "HUAWEI P20@Android@Android 7.0@1.0.2";
        String _v = "1";
        String signArgs = String.format(ApiConfig.SIGN_FORMAT, _api, _appid, _channel, _data, _ia, _ts, _ttid, _v);
        byte[] bytes = RSAUtils.encrypt(signArgs, RSAUtils.getPublicKey(NtmConfigSetting.publicKey));
        System.out.println("明文:\t" + signArgs);
        System.out.println("加密:\t" + RSAUtils.base64Encode(bytes));


        // DEMO_2
        String content = "Hello Bannong, 欢迎您入住我们酒店:)";
        byte[] bytes_ = RSAUtils.encrypt(content, RSAUtils.getPublicKey(NtmConfigSetting.publicKey));
        System.out.println("加密:\t" + new String(bytes_, Constant.UTF8));
        System.out.println("加密:\t" + RSAUtils.base64Encode(bytes_));  // 客户端给的
        String puk = RSAUtils.decrypt(bytes_, RSAUtils.getPrivateKey(NtmConfigSetting.privateKey));
        System.out.println("解密:" + puk);

        // DEMO_3
        // 加密后的并进过BASE64编码
        String check = "OzNehpVkUzWLVZxjG0l2M3l2CicaRBsp5mNfipokH3UxPi1TFTYb3YbzbDZUzU6C1louGyXs+bSu/+zD+7Qam0q25Q+ZQCxvz6tL0tv5tO3cvn0SgD0CpZ+g9/S0dR/NxHMHpcO7qCC2ZMrc6sKOIVIJJi4rjvK4IxP4cxB+ZkFzADNmYl1902NzT4xJ/DvGCNdxil3JNZcm7iw8b1r2vJ+OlJa9l2EqNvhk5Glp9SnhrzL5P0HIbVulYu1IgYJ0o1mX32hjmkX419Xhf4cphe/oEi4XSqaLzukDPAw3vzrrveq9eSeAcFeWoMFZipSUrI58idFd7j/dq+wermQ1NA==";
        System.out.println("解密:" + RSAUtils.decrypt(RSAUtils.base64Decode(check), RSAUtils.getPrivateKey(NtmConfigSetting.privateKey)));
    }

}
