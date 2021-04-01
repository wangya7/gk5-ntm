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
import org.springframework.transaction.annotation.Transactional;
import wang.bannong.gk5.ntm.sample.standalone.common.domain.ChatGroup;
import wang.bannong.gk5.ntm.sample.standalone.dao.mapper.ChatGroupMapper;
import wang.bannong.gk5.ntm.sample.standalone.mgr.ChatGroupMemberMgr;
import wang.bannong.gk5.ntm.sample.standalone.mgr.ChatGroupMgr;
import wang.bannong.gk5.util.SnowFlakeGenerator;

/**
 * @author <a href="mailto:danliang@myweimai.com">丹良</a>
 * @date 2021/4/1
 */
@Slf4j
@Service("chatGroupMgr")
public class ChatGroupMgrImpl implements ChatGroupMgr {

    @Autowired
    private ChatGroupMapper     masterChatGroupMapper;
    @Autowired
    private ChatGroupMemberMgr  chatGroupMemberMgr;


    public ChatGroup queryById(Long id) {
        return masterChatGroupMapper.selectByPrimaryKey(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ChatGroup create(String name, Date time, List<String> members) {
        ChatGroup record = new ChatGroup();
        record.setId(SnowFlakeGenerator.Factory.creategGnerator(3L, 7L).nextId());
        record.setName(name);
        record.setCreateTime(time);
        record.setModifyTime(time);
        log.info("begin insert ChatGroup, record={}", record);
        if (masterChatGroupMapper.insert(record) > 0) {
            log.info("insert ChatGroup success, record={}", record);
            if (chatGroupMemberMgr.create(record, members) > 0) {
                return record;
            }
        }
        throw new RuntimeException("更新聊天组失败");
    }
}
