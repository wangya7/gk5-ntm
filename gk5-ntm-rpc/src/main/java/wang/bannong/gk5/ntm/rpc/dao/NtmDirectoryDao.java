package wang.bannong.gk5.ntm.rpc.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import org.apache.ibatis.annotations.Param;

import java.util.List;

import wang.bannong.gk5.ntm.common.domain.NtmDirectory;

public interface NtmDirectoryDao extends BaseMapper<NtmDirectory> {
    List<NtmDirectory> selectByPid(Long pid);
    List<NtmDirectory> selectByPids(List<Long> pids);
    NtmDirectory selectByIdAndName(@Param("pid") Long pid,@Param("name") String name);
}
