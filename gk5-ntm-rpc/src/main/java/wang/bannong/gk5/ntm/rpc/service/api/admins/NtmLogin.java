package wang.bannong.gk5.ntm.rpc.service.api.admins;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import wang.bannong.gk5.ntm.common.domain.NtmAdmin;
import wang.bannong.gk5.ntm.common.exception.NtmException;
import wang.bannong.gk5.ntm.common.model.AuthToken;
import wang.bannong.gk5.ntm.common.model.NtmInnerRequest;
import wang.bannong.gk5.ntm.common.model.NtmResult;
import wang.bannong.gk5.ntm.common.model.Subject;
import wang.bannong.gk5.ntm.rpc.biz.NtmAdminMgr;
import wang.bannong.gk5.ntm.rpc.handler.AuthTokenHandler;
import wang.bannong.gk5.ntm.rpc.service.BaseInnerService;

@Slf4j
@Service
public class NtmLogin implements BaseInnerService {
    @Autowired
    private NtmAdminMgr ntmAdminMgr;

    @Override
    public NtmResult api(NtmInnerRequest innerRequest) {
        String name = innerRequest.get("name");
        if (StringUtils.isBlank(name)) {
            return NtmResult.fail("请属于用户名");
        }

        String password = innerRequest.get("password");
        if (StringUtils.isBlank(password)) {
            return NtmResult.fail("请属于密码");
        }

        try {
            NtmResult result = ntmAdminMgr.login(name, password);
            if (result.isSuccess()) {
                NtmAdmin admin = result.getData();
                Subject subject = new Subject();
                subject.setId(admin.getId());
                subject.setMobile(admin.getMobile());
                subject.setName(admin.getName());
                AuthTokenHandler.creteAuthToken(subject, AuthToken.Role.admin, null, innerRequest.getRequest());
            }
            return result;
        } catch (Exception e) {
            log.error("登录失败，name={}, password={}, 异常信息：", name, password, e);
            throw new NtmException(e);
        }
    }
}
