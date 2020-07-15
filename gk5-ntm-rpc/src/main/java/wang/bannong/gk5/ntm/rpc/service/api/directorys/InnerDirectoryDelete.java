package wang.bannong.gk5.ntm.rpc.service.api.directorys;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import wang.bannong.gk5.ntm.common.dto.DynamicDto;
import wang.bannong.gk5.ntm.common.dto.NtmDirectoryDto;
import wang.bannong.gk5.ntm.common.exception.NtmException;
import wang.bannong.gk5.ntm.common.model.NtmInnerRequest;
import wang.bannong.gk5.ntm.common.model.NtmResult;
import wang.bannong.gk5.ntm.rpc.biz.NtmDirectoryMgr;
import wang.bannong.gk5.ntm.rpc.service.BaseInnerService;

@Slf4j
@Service
public class InnerDirectoryDelete implements BaseInnerService {

    @Autowired
    private NtmDirectoryMgr ntmDirectoryMgr;

    @Override
    public NtmResult api(NtmInnerRequest innerRequest) {
        NtmDirectoryDto dto = DynamicDto.of(innerRequest).get(NtmDirectoryDto.class);
        try {
            return ntmDirectoryMgr.del(dto);
        } catch (Exception e) {
            log.error("删除目录报错，dto={}，异常信息：", dto, e);
            throw new NtmException(e);
        }
    }
}