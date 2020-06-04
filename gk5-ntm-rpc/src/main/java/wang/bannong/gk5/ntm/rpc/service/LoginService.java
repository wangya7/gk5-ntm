package wang.bannong.gk5.ntm.rpc.service;

import java.util.HashMap;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;
import wang.bannong.gk5.ntm.common.constant.ApiConfig;
import wang.bannong.gk5.ntm.common.domain.NtmApi;
import wang.bannong.gk5.ntm.common.dto.DynamicDto;
import wang.bannong.gk5.ntm.common.model.AuthToken;
import wang.bannong.gk5.ntm.common.model.NtmInnerRequest;
import wang.bannong.gk5.ntm.common.model.NtmRequest;
import wang.bannong.gk5.ntm.common.model.NtmResult;
import wang.bannong.gk5.ntm.common.model.Subject;
import wang.bannong.gk5.ntm.common.model.SubjectExtend;
import wang.bannong.gk5.ntm.rpc.handler.AuthTokenHandler;
import wang.bannong.gk5.ntm.rpc.rpc.NtmRpcClient;

/**
 * Created by bn. on 2019/10/18 4:29 PM
 */
@Slf4j
public class LoginService {
    public static NtmResult api(NtmInnerRequest innerRequest) {
        NtmApi ntmApiInfo = innerRequest.getNtmApi();
        NtmRequest ntmRequest = innerRequest.getRequest();
        String apiUnique = ntmApiInfo.getUnique();
        NtmResult result = NtmRpcClient.invoke(ntmApiInfo.getInnerInterface(), ntmApiInfo.getInnerMethod(),
                DynamicDto.of(innerRequest));
        if (!result.isSuccess()) {
            return result;
        }
        HashMap<String, Object> data = result.getData();
        log.info(">>>>> login[{}], data[{}]", ntmApiInfo.getUnique(), data);
        Subject subject = new Subject();
        Long subjectId = (Long) data.get(ApiConfig.ID);
        subject.setId(subjectId);
        subject.setMobile((String) data.get(ApiConfig.MOBILE));
        subject.setName((String) data.get(ApiConfig.NAME));
        AuthTokenHandler.creteAuthToken(subject,
                apiUnique.equals(ApiConfig.LOGIN_API) ? AuthToken.Role.user : AuthToken.Role.admin,
                null, ntmRequest);

        // 重新构造登录的返回信息
        SubjectExtend extend = new SubjectExtend();
        extend.setId(null);
        extend.setIcon((String) data.get(ApiConfig.ICON));
        extend.setMobile(subject.getMobile());
        extend.setName(subject.getName());
        if (data.get(SubjectExtend.EXTEND) != null) {
            extend.setExtend((Map<String, String>) data.get(SubjectExtend.EXTEND));
        }
        return NtmResult.success(extend);
    }
}
