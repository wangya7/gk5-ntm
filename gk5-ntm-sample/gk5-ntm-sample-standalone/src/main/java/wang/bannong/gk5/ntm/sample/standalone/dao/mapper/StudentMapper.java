package wang.bannong.gk5.ntm.sample.standalone.dao.mapper;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Set;


import wang.bannong.gk5.ntm.sample.standalone.common.domain.Student;

public interface StudentMapper {

    Student selectByPrimaryKey(Integer id);

    /**
     * 直接使用 Map<String, Long>  不要使用 Map<String, Object>
     * 否则获取值很麻烦需要转换成String
     *
     *
     * Map<String, Long> 不行 ！上天晚上的测试是有问题的，直接打印的，当直接复制给Long报错：
     * java.lang.ClassCastException: java.math.BigDecimal cannot be cast to java.lang.Long
     *
     * @return
     */
    Map<String, Object> selectMultiSumResult();

    /**
     * 注意 foreach 中的  collection="collection"  不是 "set" 也不是 "ids"
     * @param ids
     * @return
     */
    List<Student> selectByIds(Set<Integer> ids);

}