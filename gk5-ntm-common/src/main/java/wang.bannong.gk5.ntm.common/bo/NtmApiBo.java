package wang.bannong.gk5.ntm.common.bo;

import java.util.Date;

import lombok.Data;
import wang.bannong.gk5.ntm.common.domain.NtmApi;

@Data
public class NtmApiBo extends NtmApi {
    private static final long serialVersionUID = 7645994800803896900L;

    private Long   notId;     // 判读不等于这个ID的数据
    private String likeName;
    private String likeAppId;
    private Date   beginTime;
    private Date   endTime;
}
