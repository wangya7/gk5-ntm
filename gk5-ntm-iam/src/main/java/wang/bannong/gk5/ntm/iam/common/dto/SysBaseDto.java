package wang.bannong.gk5.ntm.iam.common.dto;

import java.io.Serializable;

import lombok.Data;

@Data
public class SysBaseDto implements Serializable {
    private static final long serialVersionUID = 1106631376126435463L;

    private Long userId; // 正在登陆的管理员
    private Long id;     // 业务主键
    private int  pageNum;
    private int  pageSize;
}
