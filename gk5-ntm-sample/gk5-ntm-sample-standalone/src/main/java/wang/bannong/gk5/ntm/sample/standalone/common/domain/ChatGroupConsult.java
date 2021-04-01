package wang.bannong.gk5.ntm.sample.standalone.common.domain;

import java.io.Serializable;
import java.util.Date;


import lombok.Data;

@Data
public class ChatGroupConsult implements Serializable {
    private static final long serialVersionUID = -7331941389203222757L;

    private Long   id;
    private String content;
    private Long   chatGroupId;
    private Byte   status;
    private Byte   idDel;
    private Date   createTime;
    private Date   modifyTime;

}