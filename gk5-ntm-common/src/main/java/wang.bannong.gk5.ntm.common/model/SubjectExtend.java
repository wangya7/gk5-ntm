package wang.bannong.gk5.ntm.common.model;

import java.util.HashMap;
import java.util.Map;

import lombok.Data;

@Data
public class SubjectExtend extends Subject {
    private static final long serialVersionUID = -7977363893225662L;

    public static final String EXTEND = "extend";

    private String              icon;
    private Map<String, String> extend = new HashMap<>(5);
}
