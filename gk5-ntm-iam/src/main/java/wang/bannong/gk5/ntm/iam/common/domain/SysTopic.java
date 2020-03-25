package wang.bannong.gk5.ntm.iam.common.domain;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;

@Data
public class SysTopic implements Serializable {
    private static final long serialVersionUID = 7691733475486996663L;

    private Long   id;
    private String name;
    private String unique;
    private Long   menu2Id;    // 二级菜单
    private Long   menu3Id;    // 二级菜单下面的按钮
    private Date   createTime;
    private Date   modifyTime;

}