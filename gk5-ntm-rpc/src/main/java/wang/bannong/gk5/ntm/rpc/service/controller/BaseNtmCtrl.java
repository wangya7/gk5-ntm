package wang.bannong.gk5.ntm.rpc.service.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import lombok.extern.slf4j.Slf4j;
import wang.bannong.gk5.ntm.common.constant.ApiConfig;
import wang.bannong.gk5.ntm.common.constant.NtmConstant;
import wang.bannong.gk5.ntm.common.domain.NtmApi;
import wang.bannong.gk5.ntm.common.dto.DynamicDto;
import wang.bannong.gk5.ntm.common.model.NtmInnerRequest;
import wang.bannong.gk5.ntm.common.model.NtmRequest;
import wang.bannong.gk5.ntm.common.model.NtmResponse;
import wang.bannong.gk5.ntm.common.model.NtmResult;
import wang.bannong.gk5.ntm.common.model.ResultCode;
import wang.bannong.gk5.ntm.rpc.handler.ApiHandler;
import wang.bannong.gk5.ntm.rpc.handler.AuthTokenHandler;
import wang.bannong.gk5.ntm.rpc.handler.IAuthorityAccess;
import wang.bannong.gk5.ntm.rpc.handler.RequestHandler;
import wang.bannong.gk5.ntm.rpc.rpc.NtmRpcClient;
import wang.bannong.gk5.ntm.rpc.service.BaseInnerService;
import wang.bannong.gk5.ntm.rpc.service.LoginService;
import wang.bannong.gk5.ntm.rpc.service.LogoutService;
import wang.bannong.gk5.ntm.rpc.trace.NtmTracesManager;
import wang.bannong.gk5.util.SpringBeanUtils;
import wang.bannong.gk5.util.TaskExecutor;

/**
 * Created by bn. on 2019/7/4 3:00 PM
 */
@Slf4j
@RestController
public class BaseNtmCtrl {

    @Autowired
    private        NtmTracesManager ntmTracesManager;
    private static ApiHandler       apiHandler;
    private static IAuthorityAccess authorityAccess;

    @RequestMapping(value = "/ntmx", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public NtmResponse apix(HttpServletRequest servletRequest, HttpServletResponse servletResponse) {
        return api(servletRequest, servletResponse);
    }

    @RequestMapping(value = "/ntm", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public NtmResponse api(HttpServletRequest servletRequest, HttpServletResponse servletResponse) {
        NtmResult domain = RequestHandler.checkAndConvert2NtmRequest(servletRequest);
        if (!domain.isSuccess())
            return log(NtmResponse.builder(domain).builder());

        // before ip rate limiter


        return apix(domain.getData());
    }

    public NtmResponse apix(NtmRequest request) {
        NtmInnerRequest innerRequest = NtmInnerRequest.of(request);
        // 1. check ntmApi
        if (null == apiHandler) {
            apiHandler = SpringBeanUtils.getBean("apiHandler", ApiHandler.class);
        }
        NtmResult domain = apiHandler.checkApi(request.getApi(), request.getV(), request.getAppid());
        if (!domain.isSuccess())
            return persistence(request, NtmResponse.builder(domain).builder(), null);
        NtmApi ntmApi = domain.getData();

        // 2. check token & update token
        domain = AuthTokenHandler.checkAuthToken(ntmApi, innerRequest);
        if (!domain.isSuccess())
            return persistence(request, NtmResponse.builder(domain).builder(), ntmApi);

        // 3. check ntmApi params
        domain = apiHandler.checkApiParams(innerRequest, ntmApi);
        if (!domain.isSuccess())
            return persistence(request, NtmResponse.builder(domain).builder(), ntmApi);

        NtmResponse response = NtmResponse.builder(innerApi(innerRequest, ntmApi))
                                          .api(ntmApi)
                                          .request(request)
                                          .builder();
        return persistence(request, response, ntmApi);
    }


    public static NtmResult innerApi(NtmInnerRequest innerRequest, NtmApi ntmApiInfo) {
        // 特殊场景的API
        NtmRequest request = innerRequest.getRequest();
        String appid = request.getAppid();
        String apiUnique = ntmApiInfo.getUnique();
        if (apiUnique.startsWith(ApiConfig.LOGIN_API)) {
            return LoginService.api(innerRequest);
        } else if (apiUnique.startsWith(ApiConfig.LOGOUT_API)) {
            return LogoutService.api(innerRequest);
        }

        // NTM内部的接口，比如自身接口配置，基础信息（比如：行政归属）
        if (appid.equalsIgnoreCase(NtmConstant.PROJECT)) {
            final String beanName = ntmApiInfo.getInnerInterface();
            BaseInnerService baseInnerService = SpringBeanUtils.getBean(beanName, BaseInnerService.class);
            if (null == baseInnerService) {
                return NtmResult.of(ResultCode.no_bean, beanName);
            }
            return baseInnerService.api(innerRequest);
        }

        // PRC-ReferenceConfig方式
        String innerInterface = ntmApiInfo.getInnerInterface();
        String innerMethod = ntmApiInfo.getInnerMethod();
        NtmResult result = NtmResult.SUCCESS;
        if (ntmApiInfo.getIsAsync()) {
            NtmRpcClient.invokeAsync(innerInterface, innerMethod, DynamicDto.of(innerRequest));
        } else {
            result = NtmRpcClient.invoke(innerInterface, innerMethod, DynamicDto.of(innerRequest));
        }

        return result;
    }

    public NtmResponse log(NtmResponse response) {
        log.info(NtmConstant.LOG_OUT + "response={}", response);
        return response;
    }

    public NtmResponse persistence(NtmRequest request, NtmResponse response, NtmApi ntmApi) {
        TaskExecutor.excute(() -> {
            try {
                ntmTracesManager.persistence(request, response, ntmApi);
            } catch (Exception e) {
                log.error("\n========  ntmApi persistence fail  ========\n");
                log.error("\t\trequest={}\n\t\tresponse={}\n\t\tntmApi={}\n\t\texception detail:\n", request, request, ntmApi, e);
            }
        });
        return log(response);
    }

}
