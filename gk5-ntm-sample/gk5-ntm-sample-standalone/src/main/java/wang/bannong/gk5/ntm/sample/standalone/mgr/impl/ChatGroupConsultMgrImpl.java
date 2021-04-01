/**
 * Copyright (c) 2021 Choice, Inc.
 * All Rights Reserved.
 * Choice Proprietary and Confidential.
 */

package wang.bannong.gk5.ntm.sample.standalone.mgr.impl;

import java.util.Date;
import java.util.List;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import wang.bannong.gk5.ntm.sample.standalone.common.domain.ChatGroup;
import wang.bannong.gk5.ntm.sample.standalone.common.domain.ChatGroupConsult;
import wang.bannong.gk5.ntm.sample.standalone.dao.mapper.ChatGroupConsultMapper;
import wang.bannong.gk5.ntm.sample.standalone.mgr.ChatGroupConsultMgr;
import wang.bannong.gk5.ntm.sample.standalone.mgr.ChatGroupMgr;
import wang.bannong.gk5.util.SnowFlakeGenerator;

/**
 * @author <a href="mailto:danliang@myweimai.com">丹良</a>
 * @date 2021/4/1
 */
@Slf4j
@Service
public class ChatGroupConsultMgrImpl implements ChatGroupConsultMgr {

    @Autowired
    private ChatGroupConsultMapper masterChatGroupConsultMapper;
    @Autowired
    private ChatGroupMgr           chatGroupMgr;

    @Override
    public ChatGroupConsult queryById(Long id) {
        return masterChatGroupConsultMapper.selectByPrimaryKey(id);
    }

    @Override
    public ChatGroupConsult create(String content, String chatGroupName, List<String> members) {
        ChatGroupConsult record = new ChatGroupConsult();
        record.setId(SnowFlakeGenerator.Factory.creategGnerator(7L, 11L).nextId());
        record.setContent(content);
        record.setChatGroupId(0L);
        record.setStatus((byte) 1);
        record.setIdDel((byte) 0);
        Date now = new Date();
        record.setCreateTime(now);
        record.setModifyTime(now);
        if (masterChatGroupConsultMapper.insert(record) > 0) {
            ChatGroup chatGroup = null;
            try {
                /**
                 * 控制下面的逻辑在一个事务中，要么创建群和群成员失败，要么都失败
                 */
                chatGroup = chatGroupMgr.create(chatGroupName, now, members);
            } catch (Exception e) {
                log.error("chatGroupName={}, now={}, members={},exception detail:", chatGroupName,
                        now, members, e);
            }
            if (chatGroup != null) {
                record.setChatGroupId(chatGroup.getId());
                masterChatGroupConsultMapper.updateByPrimaryKey(record);
            }
        }
        return record;
    }
}
