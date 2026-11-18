//package org.springcrazy.common.service;
//
//import com.hzb.erp.common.entity.Staff;
//import com.hzb.erp.common.entity.StaffOrginfo;
//import com.hzb.erp.common.entity.User;
//import com.hzb.erp.common.entity.rbac.SysRole;
//import com.hzb.erp.common.pojo.StaffVO;
//import com.hzb.erp.common.service.RbacService;
//import com.hzb.erp.common.service.StaffOrginfoService;
//import com.hzb.erp.common.service.StaffService;
//import com.hzb.erp.common.service.UserService;
//import com.hzb.erp.security.Util.UserAuthUtil;
//import com.hzb.erp.utils.IpUtil;
//import org.springframework.beans.BeanUtils;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import javax.annotation.PostConstruct;
//import javax.annotation.Resource;
//import javax.servlet.http.HttpServletRequest;
//import java.time.LocalDateTime;
//import java.util.List;
//
///**
// * @author Ryan
// * description 员工登录验证服务
// **/
//@Service
//public class StaffAuthService extends UserAuthUtil {
//
//    @Autowired
//    private StaffService staffService;
//
//    @Autowired
//    private UserService userService;
//
//    @Resource
//    private RbacService rbacService;
//
//    @Resource
//    private StaffOrginfoService staffOrginfoService;
//    private static StaffService stStaffService;
//    private static UserService stUserService;
//    private static RbacService stRbacService;
//    private static StaffOrginfoService stStaffOrginfoService;
//
//    @PostConstruct
//    public void init() {
//        stStaffService = staffService;
//        stUserService = userService;
//        stRbacService = rbacService;
//        stStaffOrginfoService = staffOrginfoService;
//    }
//
//    /**
//     * 从db获取登录者信息
//     */
//    public static StaffVO getLoginStaff() {
//        StaffVO vo = new StaffVO();
//        Long StaffId = getCurrentUserId();
//        if(StaffId == null) return vo;
//        Staff staff = stStaffService.getById(StaffId);
//        BeanUtils.copyProperties(staff, vo);
//        return vo;
//    }
//
//    /**
//     * 从db获取登录者机构信息
//     */
//    public static StaffOrginfo getLoginStaffOrgInfo() {
//        Long StaffId = getCurrentUserId();
//        if(StaffId == null) return new StaffOrginfo();
//        return stStaffOrginfoService.getByStaffId(StaffId);
//    }
//
//    /**
//     * 是否是超级管理员
//     */
//    public static boolean isSuperAdmin() {
//        Long curUid = getCurrentUserId();
//        if(curUid == null) {
//            return false;
//        }
//        List<SysRole> roles = stRbacService.selectRoleByUser(curUid);
//        for(SysRole role:roles) {
//            if ("superadmin".equals(role.getCode())) {
//                return true;
//            }
//        }
//        return false;
//    }
//
//    /**
//     * 记录登录信息
//     */
//    public static void updateUserLoginInfo(HttpServletRequest request, Long userId) {
//        User user = stUserService.getById(userId);
//        user.setLatestLoginIp(IpUtil.getIpAddr(request));
//        user.setLatestLoginTime(LocalDateTime.now());
//        user.setLoginTimes(user.getLoginTimes() + 1);
//        stUserService.updateById(user);
//    }
//}
