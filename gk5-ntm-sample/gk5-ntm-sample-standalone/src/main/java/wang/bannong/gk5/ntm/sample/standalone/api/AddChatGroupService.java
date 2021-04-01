/**
 * Copyright (c) 2021 Choice, Inc.
 * All Rights Reserved.
 * Choice Proprietary and Confidential.
 */

package wang.bannong.gk5.ntm.sample.standalone.api;

import com.google.common.base.Splitter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import wang.bannong.gk5.ntm.common.model.NtmInnerRequest;
import wang.bannong.gk5.ntm.common.model.NtmResult;
import wang.bannong.gk5.ntm.common.model.ResultCode;
import wang.bannong.gk5.ntm.sample.standalone.common.domain.ChatGroupConsult;
import wang.bannong.gk5.ntm.sample.standalone.mgr.ChatGroupConsultMgr;
import wang.bannong.gk5.ntm.standalone.BaseInnerService;
import wang.bannong.gk5.util.Constant;

/**
 * @author <a href="mailto:danliang@myweimai.com">丹良</a>
 * @date 2021/4/1
 */
@Service
public class AddChatGroupService implements BaseInnerService {
    @Autowired
    private ChatGroupConsultMgr chatGroupConsultMgr;

    @Override
    public NtmResult api(NtmInnerRequest request) {
        String content = request.get("content");
        if (StringUtils.isBlank(content)) {
            return NtmResult.of(ResultCode.api_param_missing, "content");
        }

        String chatGroupName = request.get("chatGroupName");
        if (StringUtils.isBlank(content)) {
            return NtmResult.of(ResultCode.api_param_missing, "chatGroupName");
        }

        String members = request.get("members");
        if (StringUtils.isBlank(members)) {
            return NtmResult.of(ResultCode.api_param_missing, "members");
        }

        ChatGroupConsult record = chatGroupConsultMgr.create(content, chatGroupName,
                Splitter.on(Constant.COMMA).trimResults().splitToList(members));
        return NtmResult.success(record);
    }
}
