package wang.bannong.gk5.ntm.rpc.service;

import com.alibaba.fastjson.JSONObject;

import wang.bannong.gk5.ntm.common.constant.NtmConstant;
import wang.bannong.gk5.ntm.rpc.handler.AuthTokenHandler;
import wang.bannong.gk5.ntm.common.model.NtmInnerRequest;
import wang.bannong.gk5.ntm.common.model.NtmRequest;
import wang.bannong.gk5.ntm.common.model.NtmResult;
import wang.bannong.gk5.util.MD5Util;

/**
 * Created by bn. on 2019/10/18 4:27 PM
 */
public class LogoutService {
    public static NtmResult api(NtmInnerRequest innerRequest) {
        NtmRequest request = innerRequest.getRequest();
        AuthTokenHandler.clearIa(request.getAppid(), innerRequest.getAuthToken().getIa());
        request.setIa(null);
        JSONObject result = new JSONObject();
        result.put(NtmConstant.MD5_SUBJECT_ID, MD5Util.md5LowerCase(String.valueOf(innerRequest.getSubjectId())));
        return NtmResult.success(result);
    }
}
