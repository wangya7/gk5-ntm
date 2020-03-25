package wang.bannong.gk5.ntm.iam.common.domain;

import java.io.Serializable;

import lombok.Data;

@Data
public class SysRoleMenu implements Serializable {
    private static final long serialVersionUID = -3883608818571566591L;

    private Long id;
    private Long roleId;
    private Long menuId;
}