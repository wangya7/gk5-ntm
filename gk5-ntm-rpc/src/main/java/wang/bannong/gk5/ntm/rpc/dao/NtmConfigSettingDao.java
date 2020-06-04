package wang.bannong.gk5.ntm.rpc.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import wang.bannong.gk5.ntm.common.domain.NtmConfigSetting;


public interface NtmConfigSettingDao extends BaseMapper<NtmConfigSetting> {

    NtmConfigSetting selectValueByKeyEff(String key);

}