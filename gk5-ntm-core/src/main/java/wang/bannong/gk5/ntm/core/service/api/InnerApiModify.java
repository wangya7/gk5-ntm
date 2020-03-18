package wang.bannong.gk5.ntm.core.service.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import wang.bannong.gk5.ntm.core.inner.NtmApiMgr;
import wang.bannong.gk5.ntm.core.service.BaseInnerService;
import wang.bannong.gk5.ntm.common.dto.ApiDto;
import wang.bannong.gk5.ntm.common.dto.DynamicDto;
import wang.bannong.gk5.ntm.common.exception.NtmException;
import wang.bannong.gk5.ntm.common.model.NtmInnerRequest;
import wang.bannong.gk5.ntm.common.model.NtmResult;

@Slf4j
@Service
public class InnerApiModify implements BaseInnerService {
    @Autowired
    private NtmApiMgr ntmApiMgr;

    @Override
    public NtmResult api(NtmInnerRequest innerRequest) {
        ApiDto dto = DynamicDto.of(innerRequest).get(ApiDto.class);
        try {
            return ntmApiMgr.modifyApi(dto);
        } catch (Exception e) {
            log.error("修改接口定义报错，dto={}，异常信息：", dto, e);
            throw new NtmException(e);
        }
    }
}