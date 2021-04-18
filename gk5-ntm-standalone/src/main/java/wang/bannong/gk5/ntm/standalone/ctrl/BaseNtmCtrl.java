package wang.bannong.gk5.ntm.standalone.ctrl;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import lombok.extern.slf4j.Slf4j;
import wang.bannong.gk5.ntm.common.constant.NtmConstant;
import wang.bannong.gk5.ntm.common.domain.NtmApi;
import wang.bannong.gk5.ntm.common.model.NtmInnerRequest;
import wang.bannong.gk5.ntm.common.model.NtmRequest;
import wang.bannong.gk5.ntm.common.model.NtmResponse;
import wang.bannong.gk5.ntm.common.model.NtmResult;
import wang.bannong.gk5.ntm.common.model.ResultCode;
import wang.bannong.gk5.ntm.standalone.BaseInnerService;
import wang.bannong.gk5.ntm.standalone.IAuthorityAccess;
import wang.bannong.gk5.ntm.standalone.config.ApiYmlUtil;
import wang.bannong.gk5.ntm.standalone.config.NtmApiChannel;
import wang.bannong.gk5.ntm.standalone.handler.AuthTokenHandler;
import wang.bannong.gk5.ntm.standalone.handler.RequestHandler;
import wang.bannong.gk5.util.SpringBeanUtils;

@Slf4j
@RestController
public class BaseNtmCtrl {

    private static IAuthorityAccess authorityAccess;

    @RequestMapping(value = "/ntm", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public NtmResponse api(HttpServletRequest servletRequest, HttpServletResponse servletResponse) {
        // 1. 获取request对象
        NtmResult domain = RequestHandler.checkAndConvert2NtmRequest(servletRequest);
        if (!domain.isSuccess())
            return log(NtmResponse.builder(domain).builder());
        return api(domain.getData());
    }


    public NtmResponse api(NtmRequest request) {
        NtmApi api = ApiYmlUtil.getApiInfo(request.getApi(), request.getV());
        log.info(NtmConstant.LOG_IN + "api={}", api);
        if (api == null)
            return log(NtmResponse.builder(NtmResult.of(ResultCode.api_not_exist)).builder());
        NtmInnerRequest innerRequest = NtmInnerRequest.of(request);
        innerRequest.setNtmApi(api);

        NtmResult domain = AuthTokenHandler.checkAuthToken(api, innerRequest);
        if (!domain.isSuccess())
            return log(NtmResponse.builder(domain).builder());

        NtmResponse response = NtmResponse.builder(innerApi(innerRequest, api))
                                          .api(api)
                                          .request(request)
                                          .builder();
        return log(response);
    }

    public NtmResult innerApi(NtmInnerRequest request, NtmApi apiInfo) {
        boolean isIa = apiInfo.getIsIa();

        // 后台需要登录api 需要校验是否可以操作
        if (isIa && ApiYmlUtil.getApiChannel(apiInfo.getUnique(), apiInfo.getVersion()) == NtmApiChannel.admin) {
            if (null == authorityAccess) {
                authorityAccess = SpringBeanUtils.getBean("authorityAccess", IAuthorityAccess.class);
            }
            NtmResult result = authorityAccess.access(request);
            if (!result.isSuccess()) {
                return result;
            }

        }
        final String beanName = apiInfo.getInnerInterface();
        BaseInnerService service = SpringBeanUtils.getBean(beanName, BaseInnerService.class);
        if (null == service) {
            return NtmResult.of(ResultCode.no_bean, beanName);
        }

        return service.api(request);
    }

    public NtmResponse log(NtmResponse response) {
        log.info(NtmConstant.LOG_OUT + "response={}", response);
        return response;
    }

}
