package wang.bannong.gk5.ntm.sample.standalone.common.domain;

import java.io.Serializable;
import java.util.Date;


import lombok.Data;

@Data
public class ChatGroup implements Serializable {
    private static final long serialVersionUID = 1120267069047675826L;

    private Long   id;
    private String name;
    private Date   createTime;
    private Date   modifyTime;
}