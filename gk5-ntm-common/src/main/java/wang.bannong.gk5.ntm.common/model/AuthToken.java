package wang.bannong.gk5.ntm.common.model;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

import lombok.Data;
import lombok.Getter;
import lombok.ToString;

@Data
public class AuthToken implements Serializable {
    private static final long serialVersionUID = 7667237598333795475L;

    private String              appid;
    private String              ia;
    private long                subjectId;
    private String              mobile;
    private String              name;
    private Role                role;
    private Date                createTime;
    private Date                accessTime;
    private Date                lastAccessTime;
    private Map<String, String> extend;


    @Getter
    @ToString
    public static enum Role {
        user,
        admin,
    }
}
