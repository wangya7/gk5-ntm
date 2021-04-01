package wang.bannong.gk5.ntm.sample.standalone.common.domain;

import java.io.Serializable;


import lombok.Data;

@Data
public class ChatGroupMember implements Serializable {
    private static final long serialVersionUID = 1627884608287055716L;

    private Long   id;
    private Long   chatGroupId;
    private String name;
}