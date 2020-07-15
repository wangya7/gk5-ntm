package wang.bannong.gk5.ntm.common.vo;

import java.io.Serializable;
import java.util.List;

import lombok.Data;
import lombok.ToString;

@Data
@ToString(callSuper = true)
public class NtmApiDocVo extends NtmApiVo {
    private static final long serialVersionUID = -6383594652692197668L;

    private List<NtmApiParamVo> parms;

}
