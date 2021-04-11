package wang.bannong.gk5.ntm.sample.standalone.dao.mapper;

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
     * @return
     */
    Map<String, Long> selectMultiSumResult();

    /**
     * 注意 foreach 中的  collection="collection"  不是 "set" 也不是 "ids"
     * @param ids
     * @return
     */
    List<Student> selectByIds(Set<Integer> ids);

}