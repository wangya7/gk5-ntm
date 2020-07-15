package wang.bannong.gk5.ntm.common.domain;

import java.io.Serializable;

import lombok.Data;

@Data
public class NtmDirectory implements Serializable {
    private static final long serialVersionUID = -3049752460085470016L;

    private Long    id;
    private Long    pid;
    private String  name;
    private Integer sort;
}
