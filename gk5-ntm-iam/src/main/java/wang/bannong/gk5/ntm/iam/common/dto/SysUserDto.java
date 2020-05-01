package wang.bannong.gk5.ntm.iam.common.dto;

import java.util.Date;
import java.util.List;

import lombok.Data;
import lombok.ToString;

@Data
@ToString(callSuper = true)
public class SysUserDto extends SysBaseDto {
    private static final long serialVersionUID = -987835963719428474L;

    private Long       orgId;
    private String     mobile;
    private String     identity;
    private String     password;
    private String     passwordOld;
    private String     workNumber;
    private String     icon;
    private String     email;
    private String     name;
    private Byte       status;
    private Date       lastLoginTime;
    private Long       creatorId;
    private List<Long> roleIds;
}
