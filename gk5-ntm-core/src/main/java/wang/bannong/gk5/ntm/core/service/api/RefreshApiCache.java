package wang.bannong.gk5.ntm.core.service.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import wang.bannong.gk5.ntm.common.model.NtmInnerRequest;
import wang.bannong.gk5.ntm.common.model.NtmResult;
import wang.bannong.gk5.ntm.core.inner.NtmApiCache;
import wang.bannong.gk5.ntm.core.service.BaseInnerService;

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