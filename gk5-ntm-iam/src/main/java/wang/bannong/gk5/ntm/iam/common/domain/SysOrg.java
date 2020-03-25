package wang.bannong.gk5.ntm.iam.common.domain;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;

@Data
public class SysOrg implements Serializable {
    private static final long serialVersionUID = 7208427523240509136L;

    private Long    id;
    private Long    pid;
    private String  name;
    private String  number;
    private String  address;
    private Integer street;
    private Integer area;
    private Integer city;
    private Integer province;
    private Byte    type;
    private Date    createTime;
    private Date    modifyTime;

}