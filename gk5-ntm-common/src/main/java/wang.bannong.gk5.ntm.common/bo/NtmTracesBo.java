package wang.bannong.gk5.ntm.common.bo;

import java.util.Date;

import lombok.Data;
import lombok.ToString;
import wang.bannong.gk5.ntm.common.model.NtmTraces;

/**
 * Created by bn. on 2019/10/11 6:31 PM
 */
@Data
@ToString(callSuper = true)
public class NtmTracesBo extends NtmTraces {
    private static final long serialVersionUID = 656417389156482196L;
    
    private Date requestBeginTime;
    private Date requestEndTime;

    private Date createBeginTime;
    private Date createEndTime;

    private Boolean resultCode; // 返回结果是否是正确 或者 错误
    private int     pageNum  = 1;
    private int     pageSize = 20;
}
