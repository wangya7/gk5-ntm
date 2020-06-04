package wang.bannong.gk5.ntm.sample.api;

import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import wang.bannong.gk5.ntm.common.model.NtmInnerRequest;
import wang.bannong.gk5.ntm.common.model.NtmResult;
import wang.bannong.gk5.ntm.standalone.BaseInnerService;

@Slf4j
@Service
public class StandaloneDemoService implements BaseInnerService {

    @Override
    public NtmResult api(NtmInnerRequest request) {
        log.info("StandaloneDemoService-:::::::::::::::::::::::::");
        return NtmResult.SUCCESS;
    }

}
