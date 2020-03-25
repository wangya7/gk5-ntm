package wang.bannong.gk5.ntm.iam.common.domain;

import java.io.Serializable;

import lombok.Data;

@Data
public class SysRoleTopic implements Serializable {
    private static final long serialVersionUID = 6658211459844440543L;

    private Long id;
    private Long roleId;
    private Long topicId;

}