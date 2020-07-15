package wang.bannong.gk5.ntm.rpc.service.api.apis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import wang.bannong.gk5.ntm.common.model.NtmInnerRequest;
import wang.bannong.gk5.ntm.common.model.NtmResult;
import wang.bannong.gk5.ntm.rpc.biz.NtmApiCache;
import wang.bannong.gk5.ntm.rpc.service.BaseInnerService;

@Slf4j
@Service
public class RefreshApiCache implements BaseInnerService {

    @Autowired
    private NtmApiCache ntmApiCache;

    @Override
    public NtmResult api(NtmInnerRequest innerRequest) {
        return ntmApiCache.refresh();
    }
}