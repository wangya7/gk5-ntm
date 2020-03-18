package wang.bannong.gk5.ntm.common.model;

import java.io.Serializable;

import lombok.Data;

/**
 * 统计接口
 * Created by bn. on 2019/10/11 6:47 PM
 */
@Data
public class GroupModel implements Serializable {
    private static final long   serialVersionUID = -7665872796987421633L;
    public static final  String MODEL_ID         = "modelId";
    public static final  String MODEL_NAME       = "modelName";
    public static final  String MODEL_TOTAL      = "modelTotal";

    private long   modelId;
    private String modelName;
    private long   modelTotal;
}
