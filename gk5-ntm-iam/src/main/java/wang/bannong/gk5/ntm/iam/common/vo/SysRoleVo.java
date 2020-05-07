package wang.bannong.gk5.ntm.iam.common.vo;

import java.io.Serializable;
import java.util.List;

import lombok.Data;

@Data
public class SysRoleVo implements Serializable {
    private static final long serialVersionUID = 7453525536386968339L;

    private String          id;
    private String          pid;
    private String          name;
    private Byte            status;
    private String          createTime;
    private Boolean         hasChildren;
    private List<SysRoleVo> children;
}
