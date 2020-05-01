package wang.bannong.gk5.ntm.iam.common.dto;

import lombok.Data;
import lombok.ToString;

@Data
@ToString(callSuper = true)
public class SysRoleDto extends SysBaseDto {
    private static final long serialVersionUID = -247789316686250629L;

    private Long   pid;
    private String name;
    private Byte   status;
}
