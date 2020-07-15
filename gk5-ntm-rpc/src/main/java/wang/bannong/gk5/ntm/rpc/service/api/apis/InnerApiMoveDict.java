package wang.bannong.gk5.ntm.rpc.service.api.apis;

import com.google.common.base.Splitter;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;
import wang.bannong.gk5.ntm.common.exception.NtmException;
import wang.bannong.gk5.ntm.common.model.NtmInnerRequest;
import wang.bannong.gk5.ntm.common.model.NtmResult;
import wang.bannong.gk5.ntm.rpc.biz.NtmApiMgr;
import wang.bannong.gk5.ntm.rpc.service.BaseInnerService;
import wang.bannong.gk5.util.Constant;

@Slf4j
@Service
public class InnerApiMoveDict implements BaseInnerService {

    @Autowired
    private NtmApiMgr ntmApiMgr;

    @Override
    public NtmResult api(NtmInnerRequest innerRequest) {
        String apiIds = innerRequest.get("apiIds");
        if (StringUtils.isBlank(apiIds)) {
            return NtmResult.fail("请先选择接口API");
        }

        String dictId = innerRequest.get("dictId");
        if (StringUtils.isBlank(dictId)) {
            return NtmResult.fail("请选择目的目录");
        }
        try {
            return ntmApiMgr.moveDict(Splitter.on(Constant.COMMA)
                                              .trimResults()
                                              .splitToList(apiIds)
                                              .stream()
                                              .map(i -> Long.valueOf(i))
                                              .collect(Collectors.toList()), Long.valueOf(dictId));
        } catch (Exception e) {
            log.error("新增接口定义报错，apiIds={}，dictId={}，异常信息：", apiIds, dictId, e);
            throw new NtmException(e);
        }
    }
}