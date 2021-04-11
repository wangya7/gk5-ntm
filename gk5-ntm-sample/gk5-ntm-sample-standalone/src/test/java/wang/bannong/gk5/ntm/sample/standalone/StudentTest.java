package wang.bannong.gk5.ntm.sample.standalone;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;


import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import wang.bannong.gk5.ntm.sample.standalone.common.domain.Student;
import wang.bannong.gk5.ntm.sample.standalone.dao.mapper.StudentMapper;

@Slf4j
@SpringBootTest(classes = Gk5NtmSampleStandaloneApplication.class)
@RunWith(SpringJUnit4ClassRunner.class)
public class StudentTest {

    @Autowired
    private StudentMapper masterStudentMapper;

    @Test
    public void queryById() {
        Student student = masterStudentMapper.selectByPrimaryKey(6);
        log.info("student={}", student);
    }


    @Test
    public void queryMultiSumResult() {
        Map<String, Long> map = masterStudentMapper.selectMultiSumResult();
        // 对于 Map<String, Object> 采用下面两种方式获取值，方案一可行
        // 方案一 Long ageSum = Long.valueOf(String.valueOf(map.get("ageSum")));
        // 方案二 Long typeSum = (Long) map.get("typeSum");
        log.info("map={}, ageSum={}, typeSum={}", map, map.get("ageSum"), map.get("typeSum"));
    }

    @Test
    public void queryByIds() {
        List<Student> list = masterStudentMapper.selectByIds(new HashSet<>(Arrays.asList(2,4,6,8)));
        log.info("list={}", list);
    }
}
