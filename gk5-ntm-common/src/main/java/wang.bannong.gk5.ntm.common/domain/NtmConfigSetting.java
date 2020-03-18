package wang.bannong.gk5.ntm.common.domain;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;

/**
 * Created by bn. on 2018/8/4 下午3:30
 */
@Data
public class NtmConfigSetting implements Serializable {
    private static final long serialVersionUID = 1913725498779185550L;

    private Long   id;
    private Long   pid;
    private String configKey;
    private String configValue;
    private String content;
    private Byte   status;
    private Date   createTime;
    private Date   modifyTime;

}
