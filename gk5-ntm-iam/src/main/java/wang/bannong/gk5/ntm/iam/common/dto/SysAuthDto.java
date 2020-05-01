package wang.bannong.gk5.ntm.iam.common.dto;

import java.util.List;

import lombok.Data;
import lombok.ToString;

@Data
@ToString(callSuper = true)
public class SysAuthDto extends SysBaseDto {
    private static final long serialVersionUID = -4935384908868557631L;

    private Long       roldId;
    private List<Long> menuIds;
}
