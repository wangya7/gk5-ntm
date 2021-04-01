/**
 * Copyright (c) 2021 Choice, Inc.
 * All Rights Reserved.
 * Choice Proprietary and Confidential.
 */

package wang.bannong.gk5.ntm.sample.standalone.mgr;

import java.util.List;


import wang.bannong.gk5.ntm.sample.standalone.common.domain.ChatGroup;

/**
 *
 * @author <a href="mailto:danliang@myweimai.com">丹良</a>
 * @date 2021/4/1
 */
public interface ChatGroupMemberMgr {
    int create(ChatGroup chatGroup, List<String> member);
}
