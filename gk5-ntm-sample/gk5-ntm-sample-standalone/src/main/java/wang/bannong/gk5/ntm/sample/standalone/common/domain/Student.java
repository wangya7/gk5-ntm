package wang.bannong.gk5.ntm.sample.standalone.common.domain;

import java.io.Serializable;

import lombok.Data;

@Data
public class Student implements Serializable {
    private static final long serialVersionUID = 7221641809704228605L;

    private Integer id;
    private String  name;
    private Integer age;
    private String  num;
    private Byte    type;
}