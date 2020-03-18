package wang.bannong.gk5.ntm.common.domain;

import java.io.Serializable;

import lombok.Data;

@Data
public class NtmUser implements Serializable {
    private static final long serialVersionUID = -2954941965082124815L;
    
    private String userName;
    private String passwd;
}
