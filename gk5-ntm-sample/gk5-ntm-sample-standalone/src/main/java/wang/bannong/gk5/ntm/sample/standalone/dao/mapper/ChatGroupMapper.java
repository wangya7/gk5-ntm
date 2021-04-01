package wang.bannong.gk5.ntm.sample.standalone.dao.mapper;

import wang.bannong.gk5.ntm.sample.standalone.common.domain.ChatGroup;

public interface ChatGroupMapper {
    int deleteByPrimaryKey(Long id);

    int insert(ChatGroup record);

    int insertSelective(ChatGroup record);

    ChatGroup selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(ChatGroup record);

    int updateByPrimaryKey(ChatGroup record);
}