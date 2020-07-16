package wang.bannong.gk5.ntm.rpc.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import wang.bannong.gk5.ntm.common.domain.NtmAdmin;

public interface NtmAdminDao extends BaseMapper<NtmAdmin> {
    NtmAdmin selectByNameAndPassword(String name, String passwd);
}
