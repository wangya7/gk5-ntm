package wang.bannong.gk5.ntm.core.service.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import wang.bannong.gk5.ntm.common.exception.NtmException;
import wang.bannong.gk5.ntm.common.model.NtmInnerRequest;
import wang.bannong.gk5.ntm.common.model.NtmResult;
import wang.bannong.gk5.ntm.core.inner.NtmApiMgr;
import wang.bannong.gk5.ntm.core.service.BaseInnerService;

@Slf4j
@Service
public class InnerApiTest implements BaseInnerService {
    @Autowired
    private NtmApiMgr ntmApiMgr;
    @Override
    public NtmResult api(NtmInnerRequest innerRequest) {
        try {
            return ntmApiMgr.testApi(innerRequest);
        } catch (Exception e) {
            log.error("测试接口参数报错，innerRequest={}，异常信息：", innerRequest, e);
            throw new NtmException(e);
        }
    }
}