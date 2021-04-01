package wang.bannong.gk5.ntm.sample.standalone.dao.mapper;

import java.util.List;


import wang.bannong.gk5.ntm.sample.standalone.common.domain.ChatGroupMember;

public interface ChatGroupMemberMapper {
    int deleteByPrimaryKey(Long id);

    int insert(ChatGroupMember record);
    int batchInsert(List<ChatGroupMember> records);

    int insertSelective(ChatGroupMember record);

    ChatGroupMember selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(ChatGroupMember record);

    int updateByPrimaryKey(ChatGroupMember record);
}