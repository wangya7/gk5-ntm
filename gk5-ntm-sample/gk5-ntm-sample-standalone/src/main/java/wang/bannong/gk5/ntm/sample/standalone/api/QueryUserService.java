package wang.bannong.gk5.ntm.sample.standalone.api;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import wang.bannong.gk5.ntm.common.model.NtmInnerRequest;
import wang.bannong.gk5.ntm.common.model.NtmResult;
import wang.bannong.gk5.ntm.common.model.ResultCode;
import wang.bannong.gk5.ntm.sample.standalone.common.domain.ChatGroupConsult;
import wang.bannong.gk5.ntm.sample.standalone.mgr.ChatGroupConsultMgr;
import wang.bannong.gk5.ntm.standalone.BaseInnerService;

@Slf4j
@Service
public class QueryUserService implements BaseInnerService {

    @Autowired
    private ChatGroupConsultMgr chatGroupConsultMgr;

    @Override
    public NtmResult api(NtmInnerRequest request) {
        log.info("request={}", request);
        String id  = request.get("id");
        if (StringUtils.isBlank(id)) {
            return NtmResult.of(ResultCode.api_param_missing, "ID");
        }
        ChatGroupConsult consult = chatGroupConsultMgr.queryById(Long.valueOf(id));
        return NtmResult.success(consult);
    }
}
