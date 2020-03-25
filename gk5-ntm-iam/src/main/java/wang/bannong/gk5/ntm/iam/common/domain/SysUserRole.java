package wang.bannong.gk5.ntm.iam.common.domain;

import java.io.Serializable;

import lombok.Data;

@Data
public class SysUserRole implements Serializable {
    private static final long serialVersionUID = 8255072041395714815L;

    private Long id;
    private Long userId;
    private Long roleId;
}