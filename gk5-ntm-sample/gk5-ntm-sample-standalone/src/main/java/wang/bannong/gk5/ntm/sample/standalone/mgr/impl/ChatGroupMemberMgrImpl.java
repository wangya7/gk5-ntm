/**
 * Copyright (c) 2021 Choice, Inc.
 * All Rights Reserved.
 * Choice Proprietary and Confidential.
 */

package wang.bannong.gk5.ntm.sample.standalone.mgr.impl;

import java.util.ArrayList;
import java.util.List;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import wang.bannong.gk5.ntm.sample.standalone.common.domain.ChatGroup;
import wang.bannong.gk5.ntm.sample.standalone.common.domain.ChatGroupMember;
import wang.bannong.gk5.ntm.sample.standalone.dao.mapper.ChatGroupMemberMapper;
import wang.bannong.gk5.ntm.sample.standalone.mgr.ChatGroupMemberMgr;

/**
 *
 * @author <a href="mailto:danliang@myweimai.com">丹良</a>
 * @date 2021/4/1
 */
@Slf4j
@Service
public class ChatGroupMemberMgrImpl implements ChatGroupMemberMgr {
    @Autowired
    private ChatGroupMemberMapper masterChatGroupMemberMapper;

    @Override
    public int create(ChatGroup chatGroup, List<String> members) {
        List<ChatGroupMember> records = new ArrayList<>();
        Long chatGroupId = chatGroup.getId();
        for (String member : members) {
            ChatGroupMember record = new ChatGroupMember();
            record.setChatGroupId(chatGroupId);
            record.setName(member);
            records.add(record);
        }
        log.info("begin insert ChatGroupMember, records={}", records);
        int result = masterChatGroupMemberMapper.batchInsert(records);
        if (result > 0) {
            log.info("insert ChatGroupMember success, records={}", records);
            for (String member : members) {
                if (member.equalsIgnoreCase("err")) {
                    throw new RuntimeException("手工制造异常信息");
                }
            }
            return 1;
        }
        throw new RuntimeException("更新群成员失败");
    }

}
