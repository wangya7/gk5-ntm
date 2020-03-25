package wang.bannong.gk5.ntm.iam.common.domain;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;

@Data
public class SysRole implements Serializable {
    private static final long serialVersionUID = 1471042287957112162L;

    private Long   id;
    private Long   pid;
    private String name;
    private Byte   status;
    private Date   createTime;
    private Date   modifyTime;

}