package wang.bannong.gk5.ntm.rpc.service.api.directorys;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import wang.bannong.gk5.ntm.common.exception.NtmException;
import wang.bannong.gk5.ntm.common.model.NtmInnerRequest;
import wang.bannong.gk5.ntm.common.model.NtmResult;
import wang.bannong.gk5.ntm.rpc.biz.NtmDirectoryMgr;
import wang.bannong.gk5.ntm.rpc.service.BaseInnerService;

@Slf4j
@Service
public class InnerDirectoryList implements BaseInnerService {

    @Autowired
    private NtmDirectoryMgr ntmDirectoryMgr;

    @Override
    public NtmResult api(NtmInnerRequest innerRequest) {
        String pid = innerRequest.get("pid");
        try {
            return ntmDirectoryMgr.list(StringUtils.isNotBlank(pid) ? Long.valueOf(pid) : 0L);
        } catch (Exception e) {
            log.error("获取目录结构报错，pid={}，异常信息：", pid, e);
            throw new NtmException(e);
        }
    }
}