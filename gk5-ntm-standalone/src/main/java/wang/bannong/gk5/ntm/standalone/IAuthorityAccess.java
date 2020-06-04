package wang.bannong.gk5.ntm.standalone;

import wang.bannong.gk5.ntm.common.model.NtmInnerRequest;
import wang.bannong.gk5.ntm.common.model.NtmResult;

/**
 * Created by bn. on 2018/10/27 9:44 AM
 */
public interface IAuthorityAccess {
    NtmResult access(NtmInnerRequest request);
}
