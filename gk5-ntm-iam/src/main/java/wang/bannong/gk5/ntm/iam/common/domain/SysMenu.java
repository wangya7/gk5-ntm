package wang.bannong.gk5.ntm.iam.common.domain;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;

@Data
public class SysMenu implements Serializable {
    private static final long serialVersionUID = 3451381915643654767L;

    private Long    id;
    private Long    pid;
    private String  name;
    private String  directory;
    private Byte    type;      // 1-一级菜单 2-二级菜单 3-菜单中的按钮
    private Integer sort;
    private Date    createTime;
}