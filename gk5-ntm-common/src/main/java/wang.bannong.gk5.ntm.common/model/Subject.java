package wang.bannong.gk5.ntm.common.model;

import java.io.Serializable;

import lombok.Data;

/**
 * 登录/注销 模型
 * Created by wang.bannong on 2018/7/1 上午1:13
 */
@Data
public class Subject implements Serializable {
    private static final long serialVersionUID = 4297631829121850860L;
    private Long   id;
    private String mobile;
    private String name;
}
