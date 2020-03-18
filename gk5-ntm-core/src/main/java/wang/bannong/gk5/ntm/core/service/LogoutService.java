package wang.bannong.gk5.ntm.core.service;

import wang.bannong.gk5.ntm.core.handler.AuthTokenHandler;
import wang.bannong.gk5.ntm.common.model.NtmInnerRequest;
import wang.bannong.gk5.ntm.common.model.NtmRequest;
import wang.bannong.gk5.ntm.common.model.NtmResult;

/**
 * Created by bn. on 2019/10/18 4:27 PM
 */
public class LogoutService {
    public static NtmResult api(NtmInnerRequest innerRequest) {
        NtmRequest request = innerRequest.getRequest();
        AuthTokenHandler.clearIa(request.getAppid(), innerRequest.getAuthToken().getIa());
        request.setIa(null);
        return NtmResult.SUCCESS;
    }
}
