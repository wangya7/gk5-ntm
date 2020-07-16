package wang.bannong.gk5.ntm.rpc.biz;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import wang.bannong.gk5.ntm.common.constant.NtmConstant;
import wang.bannong.gk5.ntm.common.domain.NtmAdmin;
import wang.bannong.gk5.ntm.common.model.NtmResult;
import wang.bannong.gk5.ntm.rpc.dao.NtmAdminDao;
import wang.bannong.gk5.util.MD5Util;

@Slf4j
@Component
public class NtmAdminMgr {

    @Autowired
    private NtmAdminDao masterNtmAdminDao;

    public NtmResult login(String name, String password) throws Exception {
        String passwd = MD5Util.md5LowerCase(name + password);
        NtmAdmin ntmAdmin = masterNtmAdminDao.selectByNameAndPassword(name, passwd);
        if (ntmAdmin == null) {
            return NtmResult.fail("用户名密码不匹配");
        }

        if (!ntmAdmin.getStatus().equals(NtmConstant.EFF_STATUS)) {
            return NtmResult.fail("用户状态异常");
        }

        return NtmResult.success(ntmAdmin);
    }

    public static void main(String[] args) {
        System.out.println(MD5Util.md5LowerCase("ntm" + "ntm123456"));
    }
}
