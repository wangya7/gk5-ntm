package wang.bannong.gk5.ntm.sample.standalone.dao.mapper;

import wang.bannong.gk5.ntm.sample.standalone.common.domain.ChatGroupConsult;

public interface ChatGroupConsultMapper {
    int deleteByPrimaryKey(Long id);

    int insert(ChatGroupConsult record);

    int insertSelective(ChatGroupConsult record);

    ChatGroupConsult selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(ChatGroupConsult record);

    int updateByPrimaryKey(ChatGroupConsult record);
}