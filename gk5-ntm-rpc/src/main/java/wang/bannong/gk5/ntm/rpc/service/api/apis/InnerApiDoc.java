package wang.bannong.gk5.ntm.rpc.service.api.apis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import wang.bannong.gk5.ntm.common.dto.ApiDto;
import wang.bannong.gk5.ntm.common.dto.DynamicDto;
import wang.bannong.gk5.ntm.common.exception.NtmException;
import wang.bannong.gk5.ntm.common.model.NtmInnerRequest;
import wang.bannong.gk5.ntm.common.model.NtmResult;
import wang.bannong.gk5.ntm.rpc.biz.NtmApiMgr;
import wang.bannong.gk5.ntm.rpc.service.BaseInnerService;

@Slf4j
@Service
public class InnerApiDoc implements BaseInnerService {

    @Autowired
    private NtmApiMgr ntmApiMgr;

    @Override
    public NtmResult api(NtmInnerRequest innerRequest) {
        try {
            return ntmApiMgr.apiDoc(Long.valueOf(innerRequest.get("apiId")));
        } catch (Exception e) {
            log.error("查询接口文档报错，innerRequest={}，异常信息：", innerRequest, e);
            throw new NtmException(e);
        }
    }
}