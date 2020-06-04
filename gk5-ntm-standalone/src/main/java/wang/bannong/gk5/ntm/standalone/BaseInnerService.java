package wang.bannong.gk5.ntm.standalone;

import wang.bannong.gk5.ntm.common.model.NtmInnerRequest;
import wang.bannong.gk5.ntm.common.model.NtmResult;

/**
 * Created by bn. on 2019/10/17 4:49 PM
 */
public interface BaseInnerService {
    NtmResult api(NtmInnerRequest request);
}
