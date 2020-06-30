package wang.bannong.gk5.ntm.iam.common.vo;

import java.io.Serializable;
import java.util.List;

import lombok.Data;
import wang.bannong.gk5.ntm.iam.common.domain.SysUser;
import wang.bannong.gk5.util.DateUtils;
import wang.bannong.gk5.util.domain.Pair;

@Data
public class SysUserVo implements Serializable {
    private static final long serialVersionUID = 1457281962255582986L;

    private String id;
    private String orgId;
    private String orgName;
    private String mobile;
    private String identity;
    private String password;
    private String workNumber;
    private String icon;
    private String email;
    private String name;
    private Byte   status;
    private Byte   type;
    private String lastLoginTime;
    private String creatorId;
    private String creatorName;
    private String createTime;
    private String modifyTime;

    private List<Pair<String, String>> roles;


    public static SysUserVo of(SysUser record) {
        SysUserVo vo = new SysUserVo();
        vo.setId(String.valueOf(record.getId()));
        vo.setName(record.getName());
        vo.setMobile(record.getMobile());
        vo.setIdentity(record.getIdentity());
        vo.setWorkNumber(record.getWorkNumber());
        vo.setIcon(record.getIcon());
        vo.setEmail(record.getEmail());
        vo.setStatus(record.getStatus());
        vo.setType(record.getType());

        if (record.getLastLoginTime() != null) {
            vo.setLastLoginTime(DateUtils.format(record.getLastLoginTime()));
        }
        vo.setCreateTime(DateUtils.format(record.getCreateTime()));
        return vo;
    }
}
