package wang.bannong.gk5.ntm.rpc.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import org.apache.ibatis.annotations.Param;

import java.util.List;

import wang.bannong.gk5.ntm.common.domain.NtmApi;

public interface NtmApiDao extends BaseMapper<NtmApi> {
    int updateDict(@Param("apiIds") List<Long> apiIds, @Param("dictId") Long dictId);
    List<NtmApi> all();
}