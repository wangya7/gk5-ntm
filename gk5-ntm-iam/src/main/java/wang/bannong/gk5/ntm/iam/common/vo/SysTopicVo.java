package wang.bannong.gk5.ntm.iam.common.vo;

import java.io.Serializable;

import lombok.Data;

@Data
public class SysTopicVo implements Serializable {
    private static final long serialVersionUID = 554300702416545029L;

    private String id;
    private String name;
    private String unique;
    private String menu2Id;    // 二级菜单
    private String menu3Id;    // 二级菜单下面的按钮
    private String createTime;
    private String modifyTime;
}
