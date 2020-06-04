package wang.bannong.gk5.ntm.rpc.handler;

import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import lombok.extern.slf4j.Slf4j;
import wang.bannong.gk5.ntm.common.constant.NtmConfigSetting;
import wang.bannong.gk5.ntm.common.constant.NtmConstant;
import wang.bannong.gk5.ntm.common.model.NtmRequest;
import wang.bannong.gk5.util.URLUtils;

@Slf4j
public final class CORSHandler {

    public static void setCORSSettingIfNeed(HttpServletRequest request, HttpServletResponse response) {
        if (NtmConfigSetting.corsOriginDefaultSetting) {
            log.info("CORS ALL-PARAMS Allowed");
            response.setHeader("Access-Control-Allow-Origin", "*");
            response.setHeader("Access-Control-Allow-Credentials", "true");
            response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, PATCH");
            response.setHeader("Access-Control-Allow-Headers", "Authorization,Content-Type,Accept,Origin,User-Agent,DNT,Cache-Control,X-Mx-ReqToken,X-Data-Type,X-Requested-With,X-Data-Type,X-Auth-Token,channel,ia,oia,v,ts,lng,lat,ttid,appid");
            response.setHeader("Access-Control-Max-Age", "3600");
            return;
        }
        String host = request.getHeader("Host");
        String ua = request.getHeader("User-Agent");
        String refer = request.getHeader("Referer");
        String accessOriginUrl = URLUtils.getUrl(refer);
        int accessOriginUrlPort = URLUtils.getPort(refer);
        if (accessOriginUrlPort != 80) {
            accessOriginUrl += ":" + accessOriginUrlPort;
        }

        log.info(NtmConstant.LOG_IN + "host={}, refer={}, origin={}, ua={}", host, refer, accessOriginUrl, ua);

        if (StringUtils.isBlank(accessOriginUrl)) {
            response.setHeader("Access-Control-Allow-Origin", getDefaultOriginHost());
        } else {
            if (contains(accessOriginUrl)) {
                response.setHeader("Access-Control-Allow-Origin", accessOriginUrl);
            }
        }
        response.setHeader("Access-Control-Allow-Credentials", "true");
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, PATCH");
    }

    private static boolean contains(String accessOriginUrl) {
        if (StringUtils.isBlank(accessOriginUrl)) return false;
        String cro = NtmConfigSetting.corsOriginHosts;
        if (cro.indexOf(accessOriginUrl) != -1) {
            return true;
        }
        return false;
    }

    private static String getDefaultOriginHost() {
        return NtmConfigSetting.corsOriginHostsDef;
    }

}
