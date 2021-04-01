/**
 * Copyright (c) 2021 Choice, Inc.
 * All Rights Reserved.
 * Choice Proprietary and Confidential.
 */

package wang.bannong.gk5.ntm.sample.standalone.mgr;

import java.util.List;


import wang.bannong.gk5.ntm.sample.standalone.common.domain.ChatGroupConsult;

/**
 *
 * @author <a href="mailto:danliang@myweimai.com">丹良</a>
 * @date 2021/4/1
 */
public interface ChatGroupConsultMgr {

    ChatGroupConsult queryById(Long id);

    ChatGroupConsult create(String content, String chatGroupName, List<String> members);

}
