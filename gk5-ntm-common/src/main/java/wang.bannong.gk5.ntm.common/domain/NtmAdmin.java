package wang.bannong.gk5.ntm.common.domain;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;

@Data
public class NtmAdmin implements Serializable {
    private static final long serialVersionUID = -13682442371305001L;

    private Long   id;
    private String name;
    private String mobile;
    private String password;
    private Byte   status;
    private Date   createTime;
}
