package wang.bannong.gk5.ntm.iam.common.dto;

import lombok.Data;
import lombok.ToString;

@Data
@ToString(callSuper = true)
public class SysMenuDto extends SysBaseDto {
    private static final long serialVersionUID = 4263245684749115165L;

    private Long    pid;
    private String  name;
    private String  directory;
    private Byte    type;
    private Byte    visible;
    private Byte    status;
    private Integer sort;

}


