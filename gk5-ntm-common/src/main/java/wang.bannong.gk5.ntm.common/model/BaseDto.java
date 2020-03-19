package wang.bannong.gk5.ntm.common.model;

import java.io.Serializable;

import lombok.Data;

@Data
public class BaseDto implements Serializable {
    private static final long serialVersionUID = 8854980620093685685L;

    protected long subjectId = 0;
    protected int  pageNum   = 1;
    protected int  pageSize  = 10;
}
