
package org.springcrazy.modules.front.controller;

import cn.hutool.crypto.digest.MD5;
import cn.hutool.extra.mail.Mail;
import cn.hutool.extra.mail.MailAccount;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
//import com.google.common.collect.Lists;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springcrazy.common.cache.CacheNames;
import org.springcrazy.common.tool.CommonUtil;
import org.springcrazy.common.tool.CourseUtils;
import org.springcrazy.common.tool.MsgSMSFactory;
import org.springcrazy.common.tool.ProfileConfig;
import org.springcrazy.core.boot.ctrl.CrazyController;
import org.springcrazy.core.launch.props.CrazyProperties;
import org.springcrazy.core.log.exception.ServiceException;
import org.springcrazy.core.mp.support.Condition;
import org.springcrazy.core.mp.support.Query;
import org.springcrazy.core.secure.AuthInfo;
import org.springcrazy.core.secure.CrazyUser;
import org.springcrazy.core.secure.utils.SecureUtil;
import org.springcrazy.core.tool.api.R;
import org.springcrazy.core.tool.api.ResultCode;
import org.springcrazy.core.tool.utils.*;
import org.springcrazy.modules.auth.granter.ITokenGranter;
import org.springcrazy.modules.auth.granter.TokenGranterBuilder;
import org.springcrazy.modules.auth.granter.TokenParameter;
import org.springcrazy.modules.auth.service.OnlineUserService;
import org.springcrazy.modules.auth.utils.TokenUtil;
import org.springcrazy.modules.book.entity.BookCollection;
import org.springcrazy.modules.book.service.BookCollectionService;
import org.springcrazy.modules.book.wrapper.BookCollectionWrapper;
import org.springcrazy.modules.book.wrapper.BookHistoryWrapper;
import org.springcrazy.modules.cms.entity.Article;
import org.springcrazy.modules.cms.entity.CmsSubject;
import org.springcrazy.modules.cms.entity.WebsiteImages;
import org.springcrazy.modules.cms.entity.WebsiteProfile;
import org.springcrazy.modules.cms.service.IArticleService;
import org.springcrazy.modules.cms.service.ICmsSubjectService;
import org.springcrazy.modules.cms.service.IWebsiteImagesService;
import org.springcrazy.modules.cms.service.IWebsiteProfileService;
import org.springcrazy.modules.cms.vo.ArticleVO;
import org.springcrazy.modules.cms.wrapper.ArticleWrapper;
import org.springcrazy.modules.edu.dto.OrdersDTO;
import org.springcrazy.modules.edu.entity.*;
import org.springcrazy.modules.edu.service.*;
import org.springcrazy.modules.edu.service.impl.HistoryServiceImpl;
import org.springcrazy.modules.edu.vo.CoursePackageVO;
import org.springcrazy.modules.edu.vo.CourseVO;
import org.springcrazy.modules.edu.vo.SubjectVO;
import org.springcrazy.modules.edu.wrapper.CoursePackageWrapper;
import org.springcrazy.modules.edu.wrapper.PointHomeworkHistoryWrapper;
import org.springcrazy.modules.lineclass.entity.LineclassEnroll;
import org.springcrazy.modules.lineclass.service.ILineclassEnrollService;
import org.springcrazy.modules.msg.entity.MsgReceive;
import org.springcrazy.modules.msg.entity.MsgSystem;
import org.springcrazy.modules.msg.service.IMsgReceiveService;
import org.springcrazy.modules.msg.vo.MsgSystemVO;
import org.springcrazy.modules.msg.wrapper.MsgSystemWrapper;
import org.springcrazy.modules.partyLogin.entity.UserProfile;
import org.springcrazy.modules.partyLogin.service.IUserProfileService;
import org.springcrazy.modules.system.entity.Dept;
import org.springcrazy.modules.system.entity.UserInfo;
import org.springcrazy.modules.system.service.IDeptService;
import org.springcrazy.modules.user.entity.Student;
import org.springcrazy.modules.user.entity.StudentClock;
import org.springcrazy.modules.user.service.IStudentClockService;
import org.springcrazy.modules.user.service.IStudentService;
import org.springcrazy.modules.user.service.IUserAccountService;
import org.springcrazy.modules.user.vo.StudentVO;
import org.springcrazy.modules.user.wrapper.StudentWrapper;
import org.springcrazy.modules.web.entity.HelpMenu;
import org.springcrazy.modules.web.entity.MsgConfig;
import org.springcrazy.modules.web.entity.WebsiteNavigate;
import org.springcrazy.modules.web.service.IHelpMenuService;
import org.springcrazy.modules.web.service.IMsgConfigService;
import org.springcrazy.modules.web.service.IWebsiteNavigateService;
import org.springcrazy.modules.web.service.IWebsiteRecommendCategoryService;
import org.springcrazy.modules.web.vo.WebsiteRecommendCategoryVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.jaxb.SpringDataJaxb;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * banner图管理 控制器
 *
 * @author TongZhou
 * @since 2020-03-06
 */
@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/front")
@Api(value = "前端用户相关接口", tags = "前端接口")
public class UserFrontController extends CrazyController {

    IStudentService studentService;

    IStudentClockService studentClockService;

    private OnlineUserService onlineUserService;
    private RedisUtil redisUtil;
    IMsgConfigService msgConfigService;
    private ICourseService courseService;
    private ICoursePackageService coursePackageService;
    private BookCollectionService bookCollectionService;
    private IDeptService deptService;
    private IOrdersService ordersService;
    private ITrxorderDetailService trxorderDetailService;
    private ILineclassEnrollService lineclassEnrollService;
    private IUserProfileService userProfileService;
    private ICourseStudyhistoryService courseStudyhistoryService;
    private ICourseKpointService courseKpointService;
    private IPointHomeworkhistoryService iPointHomeworkhistoryService;
    private IHistoryService historyService;


    @GetMapping("/user/setClientId")
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "用户登录", notes = "用户注册 ")
    public R<Boolean> setClientId(String clientId, HttpServletRequest request) {
        //注册成功完成登录流程，生成token
        int userId=SecureUtil.getUserId();

        Student student =  studentService.getStudent(userId);
        student.setClientId(clientId);
        studentService.updateById(student);

        return R.data(true);
    }


    private IMsgReceiveService msgReceiveService;
    /**
     * 分页 系统消息
     */
    @GetMapping("/user/inbox")
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "分页", notes = "传入msgSystem")
    public R<IPage<MsgReceive>> myMessage(MsgReceive msgReceive, Query query) {
        //判断为学生 则进行学生id查询
        if(Func.equals(SecureUtil.getUserRole(),"student")){
            Integer userId = SecureUtil.getUserId();
            msgReceive.setReceivingCusid(userId);
        }
        IPage<MsgReceive> pages = msgReceiveService.page(Condition.getPage(query), Condition.getQueryWrapper(msgReceive).lambda().orderByDesc(MsgReceive::getCreateTime));
        return R.data(pages);
    }

    @GetMapping("/user/unreadNum")
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "分页", notes = "传入msgSystem")
    public R<Integer> unreadNum(MsgReceive msgReceive) {
        //判断为学生 则进行学生id查询
        if(Func.equals(SecureUtil.getUserRole(),"student")){
            Integer userId = SecureUtil.getUserId();
            msgReceive.setReceivingCusid(userId);
        }
        int count = msgReceiveService.count(Condition.getQueryWrapper(msgReceive));
        return R.data(count);
    }

    /**
     * 更新站内信状态
     */
    @PostMapping("/user/setRead")
    @ApiOperationSupport(order = 5)
    @ApiOperation(value = "修改", notes = "传入msgReceive")
    public R update() {
        Integer userId = SecureUtil.getUserId();
        UpdateWrapper<MsgReceive> msgReceiveUpdateWrapper = new UpdateWrapper<>();
        msgReceiveUpdateWrapper.set("status",MsgReceive.STATUS_READ).eq("receiving_cusid",userId);
        return R.status(msgReceiveService.update(msgReceiveUpdateWrapper));
    }

    @PostMapping("/user/userLogin")
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "用户登录", notes = "用户注册 ")
    public R<AuthInfo> userLogin(String username,String password, HttpServletRequest request) {

        //注册成功完成登录流程，生成token
        TokenParameter tokenParameter = new TokenParameter();
        tokenParameter.getArgs()
                .set("account", username)
                .set("password", password)
                .set("grantType", "app")
                .set("userType", "student");

        ITokenGranter granter = TokenGranterBuilder.getGranter("password");
        UserInfo userInfo = granter.grant(tokenParameter);

        if (userInfo == null || userInfo.getUser() == null) {
            return R.fail(TokenUtil.USER_NOT_FOUND);
        }
        AuthInfo authInfo = TokenUtil.createAuthInfo(userInfo);
        //添加在线用户数据 实现spring 异步事件监听
        onlineUserService.save(authInfo);
        return R.data(authInfo);
    }
        /**
         * 用户PC前端注册
         */
    @PostMapping("/user/register")
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "用户注册", notes = "用户注册 ")
    @ApiImplicitParams({
            @ApiImplicitParam(name="username",value="账号",dataType="string", paramType = "query"),
            @ApiImplicitParam(name="mobile",value="手机号",dataType="string", paramType = "query"),
            @ApiImplicitParam(name="password",value="用户密码",dataType="string", paramType = "query"),
            @ApiImplicitParam(name="smsCode",value="短信验证码",dataType="string", paramType = "query"),
            @ApiImplicitParam(name="openId",value="微信openId",dataType="string", paramType = "query"),
            @ApiImplicitParam(name="registerFrom",value="注册来源 register_from\t\t注册来源  1(pc网站) /2(h5注册) /3(app) /4(后台管理单独) 5( 5后台管理批量) 6 (代理商开通)",dataType="string", paramType = "query")})
    public R<AuthInfo> register(String username,String mobile,String password,String smsCode,String openId,@RequestParam(defaultValue = "1") String registerFrom, HttpServletRequest request) {
        //短信验证码判断
        Map<String,String> map = ProfileConfig.getConfig(WebsiteProfile.WEBREGISTER);
        String smsOpenFlag = map.get("smsOpenFlag");
        if(Func.equals(smsOpenFlag,"1")){
            // 获取验证码
            String redisCode = String.valueOf(redisUtil.get(CacheNames.SMSCODE_KEY + mobile));
            // 判断验证码
            if (Func.isBlank(smsCode) || !StringUtil.equalsIgnoreCase(redisCode, smsCode)) {
                throw new ServiceException("请输入正确的验证码");
            }
        }

        //判断账号是否注册
        Integer count =0;
        // count=studentService.count(new QueryWrapper<Student>().lambda().eq(Student::getUserName, username));
        //if(count > 0) throw new ServiceException("账号已注册");
        //判断手机号是否注册
        count = studentService.count(new QueryWrapper<Student>().lambda().eq(Student::getMobile, mobile));
        if(count > 0) throw new ServiceException("手机号已注册");
        //创建用户
        Student student = new Student();
        student.setUserName(username);
        student.setPassword(password);
        student.setMobile(mobile);
        student.setRegisterFrom("1");
        student.setOpenId(openId);
        studentService.register(student);
        //更新用户默认昵称
        student.setShowName("小学徒"+student.getId());
        studentService.updateById(student);
        //注册成功完成登录流程，生成token
        TokenParameter tokenParameter = new TokenParameter();
        tokenParameter.getArgs()
                .set("account", mobile)
                .set("password", password)
                .set("grantType", "")
                .set("userType", "student");

        ITokenGranter granter = TokenGranterBuilder.getGranter("password");
        UserInfo userInfo = granter.grant(tokenParameter);

        if (userInfo == null || userInfo.getUser() == null) {
            return R.fail(TokenUtil.USER_NOT_FOUND);
        }
        AuthInfo authInfo = TokenUtil.createAuthInfo(userInfo);
        //添加在线用户数据 实现spring 异步事件监听
        onlineUserService.save(authInfo);
        return R.data(authInfo);
    }



    /**
     * 用户PC前端注册
     */
    @PostMapping("/user/registerByEmail")
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "用户注册", notes = "用户注册 ")
    @ApiImplicitParams({
            @ApiImplicitParam(name="username",value="账号",dataType="string", paramType = "query"),
            @ApiImplicitParam(name="email",value="手机号",dataType="string", paramType = "query"),
            @ApiImplicitParam(name="password",value="用户密码",dataType="string", paramType = "query"),
            @ApiImplicitParam(name="code",value="短信验证码",dataType="string", paramType = "query"),
//            @ApiImplicitParam(name="openId",value="微信openId",dataType="string", paramType = "query"),
            @ApiImplicitParam(name="registerFrom",value="注册来源 register_from\t\t注册来源  1(pc网站) /2(h5注册) /3(app) /4(后台管理单独) 5( 5后台管理批量) 6 (代理商开通)",dataType="string", paramType = "query")})
    public R<Boolean> registerByEmail(String username,String email,String password,String code,@RequestParam(defaultValue = "1") String registerFrom, HttpServletRequest request) {
        //短信验证码判断
        Map<String,String> map = ProfileConfig.getConfig(WebsiteProfile.WEBREGISTER);
        String smsOpenFlag = map.get("smsOpenFlag");
        if(Func.equals(smsOpenFlag,"1")){
            // 获取验证码
            String redisCode = String.valueOf(redisUtil.get(CacheNames.SMSCODE_KEY + email));
            // 判断验证码
            if (Func.isBlank(code) || !StringUtil.equalsIgnoreCase(redisCode, code)) {
                throw new ServiceException("请输入正确的验证码");
            }
        }

        //判断账号是否注册
        Integer count =0;
        count=studentService.count(new QueryWrapper<Student>().lambda().eq(Student::getUserName, username));
        if(count > 0) throw new ServiceException("账号已注册");
        //判断手机号是否注册
        count = studentService.count(new QueryWrapper<Student>().lambda().eq(Student::getEmail, email));
        if(count > 0) throw new ServiceException("邮箱已注册");
        //创建用户
        Student student = new Student();
        student.setUserName(username);
        student.setPassword(password);
        student.setEmail(email);
        student.setRegisterFrom(registerFrom);
        //student.setOpenId(openId);
        student.setLoginAccount(email);
        studentService.register(student);
        //更新用户默认昵称
        student.setShowName("小学徒"+student.getId());
        studentService.updateById(student);
        //注册成功完成登录流程，生成token
//        TokenParameter tokenParameter = new TokenParameter();
//        tokenParameter.getArgs()
//                .set("account", username)
//                .set("password", password)
//                .set("grantType", "")
//                .set("userType", "student");
//
//        ITokenGranter granter = TokenGranterBuilder.getGranter("password");
//        UserInfo userInfo = granter.grant(tokenParameter);

//        if (userInfo == null || userInfo.getUser() == null) {
//            return R.fail(TokenUtil.USER_NOT_FOUND);
//        }
        //AuthInfo authInfo = TokenUtil.createAuthInfo(userInfo);
        //添加在线用户数据 实现spring 异步事件监听
        //onlineUserService.save(authInfo);
        return R.data(true);
    }


    private IWebsiteNavigateService websiteNavigateService;

    /**
     * 查询尾部链接与 首部导航条 尾部标签
     */
    @GetMapping("/websiteH5Navigate/list")
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "查询尾部链接与 首部导航条 尾部标签", notes = "查询尾部链接与 首部导航条 尾部标签 ")
    @ApiImplicitParams({
            @ApiImplicitParam(name="roleType",value="1首页、3尾部友链、4尾部标签",dataType="int", paramType = "query")})
    public R<List<WebsiteNavigate>> websiteh5Navigate(String roleType) {

        QueryWrapper<WebsiteNavigate> queryWrapper = new QueryWrapper<WebsiteNavigate>();

        queryWrapper.lambda().eq(WebsiteNavigate::getType,"6");
        queryWrapper.lambda().likeLeft(WebsiteNavigate::getVisibleRoles,roleType);
        queryWrapper.orderByAsc("sort");

        List<WebsiteNavigate> list = websiteNavigateService.list(queryWrapper);

        return R.data(list);
    }


    /**
     * 用户H5和APP微信注册
     */
    @PostMapping("/user/registerv1")
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "用户注册", notes = "用户注册 ")
    @ApiImplicitParams({
            @ApiImplicitParam(name="username",value="账号",dataType="string", paramType = "query"),
            @ApiImplicitParam(name="mobile",value="手机号",dataType="string", paramType = "query"),
            @ApiImplicitParam(name="password",value="用户密码",dataType="string", paramType = "query"),
            @ApiImplicitParam(name="smsCode",value="短信验证码",dataType="string", paramType = "query"),
            @ApiImplicitParam(name="openId",value="微信openId",dataType="string", paramType = "query"),
            @ApiImplicitParam(name="unionId",value="微信unionId",dataType="string", paramType = "query"),
            @ApiImplicitParam(name="wxName",value="微信用户名称userName",dataType="string", paramType = "query"),
            @ApiImplicitParam(name="oauthType",value="登录类型",dataType="string", paramType = "query"),
            @ApiImplicitParam(name="registerFrom",value="来源 1pc 网站  2 后台管理  3 h5注册",dataType="string", paramType = "query")})
    public R<AuthInfo> registerWx(String username,String mobile,String password,String smsCode,String openId,String unionId,String wxName,String oauthType,@RequestParam(defaultValue = "1") String registerFrom, HttpServletRequest request) {
        //短信验证码判断
        Map<String,String> map = ProfileConfig.getConfig(WebsiteProfile.WEBREGISTER);
        String smsOpenFlag = map.get("smsOpenFlag");
        if(Func.equals(smsOpenFlag,"1")){
            // 获取验证码
            String redisCode = String.valueOf(redisUtil.get(CacheNames.SMSCODE_KEY + mobile));
            // 判断验证码
            if (Func.isBlank(smsCode) || !StringUtil.equalsIgnoreCase(redisCode, smsCode)) {
                throw new ServiceException("请输入正确的验证码");
            }
        }

        //判断账号是否注册
        Integer count =0;
        // count = studentService.count(new QueryWrapper<Student>().lambda().eq(Student::getUserName, username));
        //if(count > 0) throw new ServiceException("账号已注册");
        //判断手机号是否注册
        count = studentService.count(new QueryWrapper<Student>().lambda().eq(Student::getMobile, mobile));
        if(count > 0) throw new ServiceException("手机号已注册");
        //创建用户
        Student student = new Student();
        student.setUserName(username);
        student.setPassword(password);
        student.setMobile(mobile);
        student.setRegisterFrom(registerFrom);
        studentService.register(student);
        //更新用户默认昵称
        student.setShowName("小学徒"+student.getId());
        studentService.updateById(student);

        if("".equals(oauthType)){
            oauthType="wxH5";
        }
        if(ObjectUtil.isNotEmpty(openId)){
            UserProfile userProfile = new UserProfile();
            userProfile.setOpenid(openId);
            userProfile.setUnionid(unionId);
            userProfile.setProfiletype(oauthType);
            userProfile.setUserid(student.getId());
            userProfile.setName(wxName);
            userProfile.setProfiledate(LocalDateTime.now());
            userProfileService.saveOrUpdate(userProfile);
        }
        //注册成功完成登录流程，生成token
        TokenParameter tokenParameter = new TokenParameter();
        tokenParameter.getArgs()
                .set("account", mobile)
                .set("password", password)
                .set("grantType", "")
                .set("userType", "student");

        ITokenGranter granter = TokenGranterBuilder.getGranter("password");
        UserInfo userInfo = granter.grant(tokenParameter);

        if (userInfo == null || userInfo.getUser() == null) {
            return R.fail(TokenUtil.USER_NOT_FOUND);
        }
        AuthInfo authInfo = TokenUtil.createAuthInfo(userInfo);
        //添加在线用户数据 实现spring 异步事件监听
        onlineUserService.save(authInfo);
        return R.data(authInfo);
    }

    /**
     * 检测注册手机号是否注册
     */
    @GetMapping("/user/checkMobile")
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "检查手机号是否注册", notes = "检查手机号是否注册")
    @ApiImplicitParams({
            @ApiImplicitParam(name="mobile",value="手机号/账号",dataType="string", paramType = "query"),
            @ApiImplicitParam(name="type",value="类型 1账号验证，2 手机号验证  3 邮箱",dataType="string", paramType = "query")})
    public R<String> checkMobile(String mobile,String type) {
        if(Func.equals(type,"1")){
            //判断账号是否注册
            Integer count = studentService.count(new QueryWrapper<Student>().lambda().eq(Student::getUserName, mobile));
            if(count > 0) throw new ServiceException("账号已注册");
            return R.status(true);
        }else if(Func.equals(type,"3")){
            //判断邮箱是否注册
            Integer count = studentService.count(new QueryWrapper<Student>().lambda().eq(Student::getEmail, mobile));
            if(count > 0) throw new ServiceException("邮箱已注册");
            return R.status(true);
        } else{
            //判断手机号是否注册
            Integer count = studentService.count(new QueryWrapper<Student>().lambda().eq(Student::getMobile, mobile));
            if(count > 0) throw new ServiceException("手机号已注册");
            return R.status(true);
        }
    }

    /**
     * 检查验证码
     */
    @GetMapping("/user/checkCaptcha")
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "检查验证码", notes = "检查验证码")
    @ApiImplicitParams({
            @ApiImplicitParam(name="key",value="验证码key",dataType="string", paramType = "query"),
            @ApiImplicitParam(name="code",value="验证码",dataType="string", paramType = "query")})
    public R checkCaptcha(String code,String key) {
        // 获取验证码
        String redisCode = String.valueOf(redisUtil.get(CacheNames.CAPTCHA_KEY + key));
        // 判断验证码
        if (code == null || !StringUtil.equalsIgnoreCase(redisCode, code)) {
            throw new ServiceException(TokenUtil.CAPTCHA_NOT_CORRECT);
        }
        return R.data(true);
    }


    /**
     *
     */
    @GetMapping("/user/deptInfo")
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "部门信息", notes = "获取部门信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name="deptId",value="deptId",dataType="string", paramType = "query"),
         })
    public R<Dept> deptInfo(String deptId) {
       Dept dept =  deptService.getById(deptId);
        return R.data(dept);
    }
    /**
     *
     */
    @GetMapping("/user/myClasses")
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "部门信息", notes = "获取部门信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name="teacherId",value="teacherId",dataType="string", paramType = "query"),
    })
    public R<List<CourseVO>> myClassesInfo(String teacherId) {
        QueryWrapper<Course> coursePackageQueryWrapper = new QueryWrapper<Course>();
        coursePackageQueryWrapper.lambda().eq(Course::getMainTeacherId,teacherId)
        ;
       // int pcount = courseService.count(coursePackageQueryWrapper);

       List<Course> courseList =   courseService.list(coursePackageQueryWrapper);

       QueryWrapper<CourseStudyhistory> courseStudyhistoryQueryWrapper = new QueryWrapper<CourseStudyhistory>();
       QueryWrapper<CoursePackage> courseQueryWrapper2 = new QueryWrapper<CoursePackage>();

        List<CourseVO> courseVOS = new ArrayList<>();
       for(Course course:courseList){
           CourseVO courseVO = new CourseVO();
           courseVO.setCourseName(course.getCourseName());
           courseVO.setLogo(course.getLogo());
           courseVO.setId(course.getId());

           courseStudyhistoryQueryWrapper.lambda().eq(CourseStudyhistory::getCourseId,course.getId());
           courseStudyhistoryQueryWrapper.lambda().eq(CourseStudyhistory::getCourseType,"PACKAGE");
           Integer stuCount = courseStudyhistoryService.count(courseStudyhistoryQueryWrapper);
           courseVO.setStudentCount(stuCount);

           courseQueryWrapper2.lambda().eq(CoursePackage::getParentCourseId,course.getId());
           Integer cusCount = coursePackageService.count(courseQueryWrapper2);
           courseVO.setCourseCount(cusCount);
           courseVOS.add(courseVO);

       }

       // Dept dept =  deptService.getById(deptId);
        return R.data(courseVOS);
    }

    /**
     * 注册验证码发送
     */
    @GetMapping("/user/sendSMS")
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "发送验证码", notes = "发送验证码 10分钟有效 ")
    @ApiImplicitParams({
            @ApiImplicitParam(name="key",value="验证码key",dataType="string", paramType = "query"),
            @ApiImplicitParam(name="mobile",value="手机号",dataType="string", paramType = "query"),
            @ApiImplicitParam(name="code",value="验证码",dataType="string", paramType = "query")})
    public R<String> sendSMS(String mobile, String code, String key, @RequestParam(defaultValue = MsgConfig.CODE_REGISTER) String type) {
        // 获取验证码
        String redisCode = String.valueOf(redisUtil.get(CacheNames.CAPTCHA_KEY + key));
        // 判断验证码
        if (code == null || !StringUtil.equalsIgnoreCase(redisCode, code)) {
            throw new ServiceException(TokenUtil.CAPTCHA_NOT_CORRECT);
        }
        //发送验证码
        String verCode = Func.random(4, RandomType.INT);
        redisUtil.set(CacheNames.SMSCODE_KEY + mobile, verCode, 10L, TimeUnit.MINUTES);
        Student student = new Student();
        student.setMobile(mobile);
        Map<String,String> params = Maps.newLinkedHashMap();
        params.put("code",verCode);
        msgConfigService.sendMsg(student,params, type);
        return R.data(verCode);
    }


    /**
     * 登录验证码发送
     */
    @GetMapping("/user/sendSMSByMobile")
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "发送验证码", notes = "发送验证码 10分钟有效 ")
    @ApiImplicitParams({
            @ApiImplicitParam(name="mobile",value="手机号",dataType="string", paramType = "query")})
    public R<String> sendSMS(String mobile, @RequestParam(defaultValue = MsgConfig.CODE_LOGIN) String type) {
        // 获取验证码
        Object mobileKey = redisUtil.get(CacheNames.SMSCODECHECK_KEY + mobile);
        // 判断验证码
        if (Func.notNull(mobileKey)) {
            throw new ServiceException("短信验证码60s内不能重复发送");
        }
        redisUtil.set(CacheNames.SMSCODECHECK_KEY + mobile, mobile, 1L, TimeUnit.MINUTES);
        //发送验证码
        String verCode = Func.random(4, RandomType.INT);
        redisUtil.set(CacheNames.SMSCODE_KEY + mobile, verCode, 10L, TimeUnit.MINUTES);
        Student student = new Student();
        student.setMobile(mobile);
        Map<String,String> params = Maps.newLinkedHashMap();
        params.put("code",verCode);
        msgConfigService.sendMsg(student,params, type);
        return R.data(verCode);
    }

    /**
     * 用户登出
     */
    @GetMapping("/user/logout")
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "用户登出", notes = "用户登出")
    public R<String> logout(String mobile) {
        //清除服务端在线用户信息
        CrazyUser user = SecureUtil.getUser();
        if(Func.isNotEmpty(user)){
            onlineUserService.logout(user.getAccount());
        }
        return R.status(true);
    }

    /**
     * 邮箱绑定
     */

    @GetMapping("/user/sendEmail")
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "发送邮箱", notes = "发送邮箱")
    public R<String> sendEmail(String email) {
        Integer userId = SecureUtil.getUserId();
        //判断如果没有用户信息则进行登录
        if(Func.equals(userId,-1)){
            return R.fail(ResultCode.UN_AUTHORIZED);
        }
        //判断邮箱是否已经注册
        Integer count = studentService.count(new QueryWrapper<Student>().lambda().eq(Student::getEmail, email));
        if(count >0){
            return R.fail("邮箱已注册");
        }
        //邮件发送
        Student student = studentService.getById(userId);
        student.setEmail(email);
        //生成激活地址
        CrazyProperties crazyProperties = SpringUtil.getBean(CrazyProperties.class);
        String domain = crazyProperties.get("domain");
        //生成激活码  激活码有效时间10分钟
        String activeCode = Func.random(4, RandomType.INT);
        redisUtil.set(CacheNames.ACTIVECODE_KEY + userId, activeCode, 10L, TimeUnit.MINUTES);
        Map<String,String> params = Maps.newLinkedHashMap();
        params.put("showName",student.getShowName());
        params.put("activeCode",activeCode);
        msgConfigService.sendMsg(student,params, MsgConfig.ACTIVE_EMAIL);
        return R.status(true);
    }
    @GetMapping("/user/sendEmailCode")
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "发送邮箱", notes = "发送邮箱")
    public R<String> sendEmailCode(String email) {
        Integer userId = SecureUtil.getUserId();
        //判断如果没有用户信息则进行登录
//        if(Func.equals(userId,-1)){
//            return R.fail(ResultCode.UN_AUTHORIZED);
//        }
        //判断邮箱是否已经注册
        Integer count = studentService.count(new QueryWrapper<Student>().lambda().eq(Student::getEmail, email));
        if(count >0){
            return R.fail("邮箱已注册");
        }
        //邮件发送
//        Student student = studentService.getById(userId);
//        student.setEmail(email);
        //生成激活地址
        CrazyProperties crazyProperties = SpringUtil.getBean(CrazyProperties.class);
        String domain = crazyProperties.get("domain");
        //生成激活码  激活码有效时间10分钟
        String activeCode = Func.random(4, RandomType.INT);

        redisUtil.set(CacheNames.ACTIVECODE_KEY + email, activeCode, 10L, TimeUnit.MINUTES);
        Map<String,String> params = Maps.newLinkedHashMap();
        //params.put("showName",student.getShowName());
        params.put("activeCode",activeCode);
        msgConfigService.sendEmailMsg(params,email, MsgConfig.ACTIVE_EMAIL);
        return R.status(true);
    }
    /**
     * 邮箱绑定
     */
    @Deprecated
    @GetMapping("/user/bindEmail")
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "绑定邮箱", notes = "绑定邮箱")
    public R<String> bindEmail(String email,String activeCode) {
        Integer userId = SecureUtil.getUserId();
        //判断如果没有用户信息则进行登录
        if(Func.equals(userId,-1)){
            return R.fail(ResultCode.UN_AUTHORIZED);
        }
        //获取激活码
        String ractiveCode = String.valueOf(redisUtil.get(CacheNames.ACTIVECODE_KEY + userId));
        String msg = "激活成功";
        if(Func.isNull(ractiveCode)){
            msg = "激活码已失效";
            return R.fail(msg);
        }
        if(!Func.equals(ractiveCode,activeCode)){
            msg = "激活码错误";
            return R.fail(msg);
        }
        //绑定邮箱
        Student student = studentService.getById(userId);
        student.setEmail(email);
        studentService.updateById(student);
        return R.success(msg);
    }

    /**
     * 修改密码
     */
    @Deprecated
    @GetMapping("/user/updatePassword")
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "修改密码", notes = "修改密码")
    public R<String> updatePassword(String oldPassword,String newPassword) {
        Integer userId = SecureUtil.getUserId();
        //判断如果没有用户信息则进行登录
        if(Func.equals(userId,-1)){
            return R.fail(ResultCode.UN_AUTHORIZED);
        }
        Student student = studentService.getById(userId);
        //判断密码是否正确
        if(!Func.equals(student.getPassword(),DigestUtil.encrypt(oldPassword))){
            return R.fail("原密码不正确");
        }
        student.setPassword(DigestUtil.encrypt(newPassword));
        studentService.updateById(student);
        return R.status(true);
    }

    /**
     * 修改手机号
     */
    @Deprecated
    @GetMapping("/user/updateMobile")
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "修改手机号", notes = "修改手机号")
    public R<String> updateMobile(String code,String mobile) {
        Integer userId = SecureUtil.getUserId();
        //判断如果没有用户信息则进行登录
        if(Func.equals(userId,-1)){
            return R.fail(ResultCode.UN_AUTHORIZED);
        }
        Student student = studentService.getById(userId);
        //判断验证码是否正确
        String redisCode = String.valueOf(redisUtil.get(CacheNames.SMSCODE_KEY + mobile));
        // 判断验证码
        if (Func.isBlank(code) || !StringUtil.equalsIgnoreCase(redisCode, code)) {
            throw new ServiceException("请输入正确的验证码");
        }
        student.setMobile(mobile);
        studentService.updateById(student);
        return R.status(true);
    }


    /**
     * 发送邮箱重置密码
     */
    @GetMapping("/user/sendEmailResetPwd")
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "发送邮箱", notes = "发送邮箱重置密码")
    public R<String> sendEmailResetPwd(String email,String code,String key) {
        //判断邮箱是否已经注册
        Integer count = studentService.count(new QueryWrapper<Student>().lambda().eq(Student::getEmail, email));
        if(count == 0){
            return R.fail("该邮箱无效");
        }
        // 获取验证码
        String redisCode = String.valueOf(redisUtil.get(CacheNames.CAPTCHA_KEY + key));
        if(code == null || !StringUtil.equalsIgnoreCase(redisCode, code)){
            return R.fail("验证码不正确");
        }
        //邮件发送
        Student student = studentService.getOne(new QueryWrapper<Student>().lambda().eq(Student::getEmail,email));
        if(Func.isNull(student)){
            return R.fail("该邮箱无效");
        }
        student.setEmail(email);
        //生成新密码
        String pwd = Func.random(8, RandomType.INT);
        //修改密码
        student.setPassword(DigestUtil.encrypt(pwd));
        studentService.updateById(student);
        //发送邮件
        Map<String,String> params = Maps.newLinkedHashMap();
        params.put("newpassword",pwd);
        msgConfigService.sendMsg(student,params, MsgConfig.RETRIEVE_PASSWORD);
        return R.status(true);
    }


    /**
     * 通过手机号修改密码
     */
    @GetMapping("/user/updatePwdByMobile")
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "修改手机号", notes = "修改手机号")
    public R<String> updatePwdByMobile(String code,String mobile,String newPwd) {
        Student student = studentService.getOne(new QueryWrapper<Student>().lambda().eq(Student::getMobile,mobile));
        if(Func.isNull(student)){
            return R.fail("暂无手机号");
        }
        //判断验证码是否正确
        String redisCode = String.valueOf(redisUtil.get(CacheNames.SMSCODE_KEY + mobile));
        // 判断验证码
        if (Func.isBlank(code) || !StringUtil.equalsIgnoreCase(redisCode, code)) {
            throw new ServiceException("请输入正确的验证码");
        }
        student.setPassword(DigestUtil.encrypt(newPwd));
        studentService.updateById(student);
        return R.status(true);
    }

    /**
     * 创建充值订单
     */
    @Deprecated
    @GetMapping("/user/createOrder")
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "创建充值订单", notes = "创建充值订单")
    public R<OrdersDTO> createOrder(String orderType,String money,String payType,@RequestParam(defaultValue = "web") String reqChannel) {
        Integer userId = SecureUtil.getUserId();
        //判断如果没有用户信息则进行登录
        if(Func.equals(userId,-1)){
            return R.fail(ResultCode.UN_AUTHORIZED);
        }
        BigDecimal bmoney = new BigDecimal(money);
        OrdersDTO order = new OrdersDTO();
        order.setUserId(userId);
        order.setOrderNo(CommonUtil.getOrderNum());
        order.setStates(Orders.STATES_INIT);
        order.setCreateTime(DateUtil.now());
        order.setPayType(payType);
        order.setReqChannel(reqChannel);
        order.setOrderAmount(bmoney);
        order.setCashAmount(BigDecimal.ZERO);
        order.setVmAmount(BigDecimal.ZERO);
        order.setBackAmount(BigDecimal.ZERO);
        order.setCouponAmount(BigDecimal.ZERO);
        order.setOutTradeNo(CommonUtil.getOrderNum());
        order.setOrderType(orderType);

        List<TrxorderDetail> list = Lists.newArrayList();
        //初始化订单详情
        TrxorderDetail detail = TrxorderDetail.builder().userId(userId).courseId(0).orderNo(order.getOrderNo()).losetype("0")
                .loseTime("").beginTime(DateUtil.now()).authTime(DateUtil.now()).createTime(DateUtil.now())
                .sourcePrice(bmoney).currentPrice(bmoney).courseName("账户充值").authStatus(TrxorderDetail.STATUS_INIT)
                .description("").lastUpdateTime(DateUtil.now()).delStatus("1").trxorderType(order.getOrderType()).build();
        list.add(detail);
        order.setList(list);
        ordersService.createOrder(order);

        return R.data(order);
    }

    /**
     * 创建课程订单
     */
    @Deprecated
    @GetMapping("/user/createCourseOrder")
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "创建课程订单", notes = "创建课程订单")
    public R<OrdersDTO> createCourseOrder(Integer courseId ,String payType,@RequestParam(defaultValue = "web") String reqChannel) {
        Integer userId = SecureUtil.getUserId();
        //判断如果没有用户信息则进行登录
        if(Func.equals(userId,-1)){
            return R.fail(ResultCode.UN_AUTHORIZED);
        }
        Course course = courseService.getById(courseId);
        String orderType = "";
        if(Func.equals(course.getSellType(),"1")){
            orderType = "COURSE";
        }
        if(Func.equals(course.getSellType(),"2")){
            orderType = "LIVE";
        }
        if(Func.equals(course.getSellType(),"3")){
            orderType = "PACKAGE";
        }
        if(Func.equals(course.getSellType(),"4")){
            orderType = "LINECLASS";
        }
        //如果课程类型为到期时间
        if(Func.equals(course.getLosetype(),"0")){
            Date courseEndTime = course.getEndTime();
            if(Func.isNotEmpty(courseEndTime)){
                Date nowDate = new Date();
                //如果到期时间小于当前时间则不能下订单
                if(courseEndTime.compareTo(nowDate)<0){
                    return R.fail("课程已过期，请学习其他课程");
                }
            }
        }

        BigDecimal bmoney = course.getCurrentPrice();
        OrdersDTO order = new OrdersDTO();
        order.setUserId(userId);
        order.setOrderNo(CommonUtil.getOrderNum());
        order.setStates(Orders.STATES_INIT);
        order.setCreateTime(DateUtil.now());
        order.setPayType(payType);
        order.setReqChannel(reqChannel);
        order.setOrderAmount(bmoney);
        order.setCashAmount(BigDecimal.ZERO);
        order.setVmAmount(BigDecimal.ZERO);
        order.setBackAmount(BigDecimal.ZERO);
        order.setCouponAmount(BigDecimal.ZERO);
        order.setOutTradeNo(CommonUtil.getOrderNum());
        order.setOrderType(orderType);

        List<TrxorderDetail> list = Lists.newArrayList();
        //初始化订单详情
        TrxorderDetail detail = TrxorderDetail.builder().userId(userId).courseId(courseId).orderNo(order.getOrderNo()).losetype(course.getLosetype())
                .loseTime(course.getLoseTime()).loseAbsTime(course.getEndTime()).beginTime(DateUtil.now()).authTime(DateUtil.now()).createTime(DateUtil.now())
                .sourcePrice(bmoney).currentPrice(bmoney).courseName(course.getCourseName()).authStatus(TrxorderDetail.STATUS_INIT)
                .description(course.getTitle()).lastUpdateTime(DateUtil.now()).delStatus("1").trxorderType(order.getOrderType()).build();
        list.add(detail);
        order.setList(list);
        ordersService.createOrder(order);

        return R.data(order);
    }

    /**
     * 创建免费课程订单
     */
    @Deprecated
    @GetMapping("/user/createFreeCourseOrder")
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "创建免费课程订单", notes = "创建免费课程订单")
    public R<OrdersDTO> createFreeCourseOrder(Integer courseId ,String payType,@RequestParam(defaultValue = "web") String reqChannel) {
        Integer userId = SecureUtil.getUserId();
        //判断如果没有用户信息则进行登录
        if(Func.equals(userId,-1)){
            return R.fail(ResultCode.UN_AUTHORIZED);
        }
        //判断该用户是否含有免费课程  如果没有则创建
        TrxorderDetail trxorderDetail = new TrxorderDetail();
        trxorderDetail.setUserId(userId);
        trxorderDetail.setCourseId(courseId);
        QueryWrapper<TrxorderDetail> trxorderDetailQueryWrapper = new QueryWrapper<>(trxorderDetail);
        trxorderDetailQueryWrapper.lambda().ge(TrxorderDetail::getAuthTime, DateUtil.now()).eq(TrxorderDetail::getAuthStatus,TrxorderDetail.STATUS_SUCCESS);
        Integer count = trxorderDetailService.count(trxorderDetailQueryWrapper);
        if(count == 0){
            Course course = courseService.getById(courseId);
            String orderType = "";
            if(Func.equals(course.getSellType(),"1")){
                orderType = "COURSE";
            }
            if(Func.equals(course.getSellType(),"2")){
                orderType = "LIVE";
            }
            if(Func.equals(course.getSellType(),"3")){
                orderType = "PACKAGE";
            }
            if(Func.equals(course.getSellType(),"4")){
                orderType = "LINECLASS";
            }


            BigDecimal bmoney = course.getCurrentPrice();
            OrdersDTO order = new OrdersDTO();
            order.setUserId(userId);
            order.setOrderNo(CommonUtil.getOrderNum());
            order.setStates(Orders.STATES_SUCCESS);
            order.setCreateTime(DateUtil.now());
            order.setPayType(payType);
            order.setPayTime(DateUtil.now());
            order.setReqChannel(reqChannel);
            order.setOrderAmount(bmoney);
            order.setCashAmount(BigDecimal.ZERO);
            order.setVmAmount(BigDecimal.ZERO);
            order.setBackAmount(BigDecimal.ZERO);
            order.setCouponAmount(BigDecimal.ZERO);
            order.setOutTradeNo(CommonUtil.getOrderNum());
            order.setOrderType(orderType);

            List<TrxorderDetail> list = Lists.newArrayList();
            //初始化订单详情
            TrxorderDetail detail = TrxorderDetail.builder().userId(userId).courseId(courseId).orderNo(order.getOrderNo()).losetype(course.getLosetype())
                    .loseTime(course.getLoseTime()).loseAbsTime(course.getEndTime()).beginTime(DateUtil.now()).authTime(CourseUtils.getAuthTime(course)).createTime(DateUtil.now())
                    .sourcePrice(bmoney).currentPrice(bmoney).courseName(course.getCourseName()).authStatus(TrxorderDetail.STATUS_SUCCESS)
                    .description(course.getTitle()).lastUpdateTime(DateUtil.now()).delStatus("1").trxorderType(order.getOrderType()).payTime(DateUtil.now()).build();
            list.add(detail);
            order.setList(list);
            ordersService.createOrder(order);
            //更新销售量 销售量+1
            //查询学习人数
            QueryWrapper<TrxorderDetail> queryWrapper = new QueryWrapper<TrxorderDetail>();
            queryWrapper.lambda().eq(TrxorderDetail::getCourseId,courseId).eq(TrxorderDetail::getAuthStatus,TrxorderDetail.STATUS_SUCCESS);
            Integer buyCount = trxorderDetailService.count(queryWrapper);
            course.setPageBuycount(buyCount);
            courseService.updateById(course);
            if("LINECLASS".equals(orderType)){
                LineclassEnroll lineclassEnroll = new LineclassEnroll();
                lineclassEnroll.setUserId(detail.getUserId());
                lineclassEnroll.setCourseId(detail.getCourseId());
                lineclassEnroll.setTrxorderId(detail.getTrxorderId());
                lineclassEnrollService.updateLineClassEnrol(lineclassEnroll);
            }
        }


        return R.status(true);
    }

    /**
     * 账户储值支付
     */
    @GetMapping("/user/accountPay")
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "账户储值支付", notes = "账户储值支付")
    public R accountPay(Integer orderId) {
        return R.data(ordersService.accountPay(orderId));
    }

    /**
     * 检查订单是否成功
     */
    @GetMapping("/user/checkOrder")
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "创建充值订单", notes = "创建充值订单")
    public R<OrdersDTO> checkOrder(Integer orderId) {
        Orders orders = ordersService.getById(orderId);
        if(Func.equals(Orders.STATES_INIT,orders.getStates())){
            return R.status(false);
        }
        return R.status(true);
    }

    /**
     * 检查是否拥有课程
     */
    @Deprecated
    @GetMapping("/user/checkHaveCourse")
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "检查是否拥有课程", notes = "检查是否拥有课程")
    public R checkHaveCourse(Integer courseId) {
        //判断直播与点播课程
        Integer userId = SecureUtil.getUserId();
        //判断如果没有用户信息则进行登录
        if(Func.equals(userId,-1)){
            return R.fail(ResultCode.UN_AUTHORIZED);
        }

        TrxorderDetail td = new TrxorderDetail();
        td.setUserId(userId);
        td.setCourseId(courseId);
        td.setAuthStatus(TrxorderDetail.STATUS_SUCCESS);
        QueryWrapper<TrxorderDetail> trxorderDetailQueryWrapper = new QueryWrapper<>(td);
        trxorderDetailQueryWrapper.lambda().ge(TrxorderDetail::getAuthTime,new Date());
        Integer count = trxorderDetailService.count(trxorderDetailQueryWrapper);
        if(count == 0){
            //判断套餐中是否含有次课程
            TrxorderDetail detail = new TrxorderDetail();
            detail.setUserId(userId);
            detail.setAuthStatus(TrxorderDetail.STATUS_SUCCESS);
            detail.setTrxorderType("PACKAGE");
            QueryWrapper<TrxorderDetail> wrapper = new QueryWrapper<TrxorderDetail>(detail);
            wrapper.lambda().ge(TrxorderDetail::getAuthTime,new Date());
            List<TrxorderDetail> list = trxorderDetailService.list(wrapper);
            List<Integer> idArr = list.stream().map(d->d.getCourseId()).collect(Collectors.toList());
            QueryWrapper<CoursePackage> coursePackageQueryWrapper = new QueryWrapper<CoursePackage>();
            coursePackageQueryWrapper.lambda().eq(CoursePackage::getCourseId,courseId).in(CoursePackage::getParentCourseId,idArr);
            Integer pcount = coursePackageService.count(coursePackageQueryWrapper);
            if(pcount == 0){
                return R.data(false);
            }
        }
        return R.data(true);
    }


    /**
     *
     */

    @GetMapping("/user/myCourseList/{userId}")
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "我的课程信息", notes = "我的课程信息")
    public R myCourseList(@PathVariable Integer userId,Query query) {
        Student student = studentService.getById(userId);
        //所有课时
        QueryWrapper<CoursePackage> queryWrapper3 = new QueryWrapper<CoursePackage>();
        queryWrapper3.lambda().eq(CoursePackage::getParentCourseId,student.getClassCourseId())
                .gt(CoursePackage::getCompletionStatus,0);

        IPage<CoursePackage> pages = coursePackageService.page(Condition.getPage(query), queryWrapper3);

        return R.data(CoursePackageWrapper.build().pageVO(pages));
    }


    @GetMapping("/user/myCollectionBooks/{studentId}")
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "我的课程信息", notes = "我的课程信息")
    public R myCollectionBooks(@PathVariable Integer studentId,Query query) {

        //所有课时
        QueryWrapper<BookCollection> queryWrapper3 = new QueryWrapper<BookCollection>();
        queryWrapper3.lambda().eq(BookCollection::getStudentId,studentId);
        IPage<BookCollection> pages = bookCollectionService.page(Condition.getPage(query), queryWrapper3);
        return R.data(BookCollectionWrapper.build().pageVO(pages));

    }

    @GetMapping("/user/myReadHistory/{studentId}")
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "我的课程信息", notes = "我的课程信息")
    public R myReadHistory(@PathVariable Integer studentId,Query query) {

        //所有课时
        QueryWrapper<History> queryWrapper3 = new QueryWrapper<History>();
        queryWrapper3.lambda()
                .eq(History::getStudentId,studentId)
                .eq(History::getType,"book_read");

        IPage<History> pages = historyService.page(Condition.getPage(query), queryWrapper3);
        return R.data(BookHistoryWrapper.build().pageVO(pages));

    }

    @GetMapping("/user/myHomeworks/{userId}")
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "我的作品信息", notes = "我的作品信息")
    public R myHomeworks(@PathVariable Integer userId,Query query) {
        Student student = studentService.getById(userId);
        //所有课时
        QueryWrapper<CoursePackage> queryWrapper3 = new QueryWrapper<CoursePackage>();
        queryWrapper3.lambda().eq(CoursePackage::getParentCourseId,student.getClassCourseId())
                .gt(CoursePackage::getCompletionStatus,0);
        List<CoursePackage> coursePackages2 =  coursePackageService.list(queryWrapper3);
        List<Integer> ids2=new ArrayList<>();
        for(CoursePackage coursePackage:coursePackages2){
            ids2.add(coursePackage.getCourseId());
        }
        IPage<PointHomeworkhistory> pages  =null;
        if(ids2.size()>0){
            QueryWrapper<PointHomeworkhistory> queryWrapper12 = new QueryWrapper<PointHomeworkhistory>();
            queryWrapper12.lambda().in(PointHomeworkhistory::getCourseId,ids2)
            ;
             pages  = iPointHomeworkhistoryService.page(Condition.getPage(query), queryWrapper12);
            return R.data(PointHomeworkHistoryWrapper.build().pageVO(pages));
        }

        return R.success("暂无作业");

    }


    @GetMapping("/user/myStatistic/{userId}")
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "我的統計信息", notes = "我赠与的课程")
    public R myStatistic(@PathVariable  Integer userId) {

        Student student = studentService.getById(userId);

        //参与学习 学习中
        QueryWrapper<CoursePackage> queryWrapper1 = new QueryWrapper<CoursePackage>();
        queryWrapper1.lambda().eq(CoursePackage::getParentCourseId,student.getClassCourseId())
        .in(CoursePackage::getCompletionStatus,"2","3");
        Integer coursePackageCount =  coursePackageService.count(queryWrapper1);
        List<CoursePackage> coursePackages =  coursePackageService.list(queryWrapper1);
        List<Integer> ids=new ArrayList<>();
        for(CoursePackage coursePackage:coursePackages){
            ids.add(coursePackage.getCourseId());
        }
        Integer homeworkCount =0;
        if(ids.size()>0){
            QueryWrapper<PointHomeworkhistory> queryWrapper11 = new QueryWrapper<PointHomeworkhistory>();
            queryWrapper11.lambda().in(PointHomeworkhistory::getCourseId,ids)
            ;
             homeworkCount = iPointHomeworkhistoryService.count(queryWrapper11);
        }



        //所有课时
        QueryWrapper<CoursePackage> queryWrapper3 = new QueryWrapper<CoursePackage>();
        queryWrapper3.lambda().eq(CoursePackage::getParentCourseId,student.getClassCourseId())
                .gt(CoursePackage::getCompletionStatus,0);
        List<CoursePackage> coursePackages2 =  coursePackageService.list(queryWrapper3);
        //courseService.getById(coursePackage.getCourseId());
        Integer courseStuedCount = coursePackageService.count(queryWrapper3);
        List<Integer> ids2=new ArrayList<>();
        for(CoursePackage coursePackage:coursePackages2){
            ids2.add(coursePackage.getCourseId());
        }
        Integer oneHomeworkCount =0;
        if(ids2.size()>0){
            QueryWrapper<PointHomeworkhistory> queryWrapper12 = new QueryWrapper<PointHomeworkhistory>();
            queryWrapper12.lambda().in(PointHomeworkhistory::getCourseId,ids2)
            ;
             oneHomeworkCount = iPointHomeworkhistoryService.count(queryWrapper12);
        }




        Map<String,Integer> res  = new HashMap<>();
        res.put("courseKpointCount",coursePackageCount);
        res.put("homeworkCount",homeworkCount);
        res.put("courseStuedCountAll",courseStuedCount);
        res.put("homeworkCountAll",oneHomeworkCount);
        return R.data(res);


    }



    @GetMapping("/user/userData")
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "我的资料", notes = "我的资料")
    public R userData(int studentId) {

        Integer userId = SecureUtil.getUserId();
        Student  student = studentService.getById(userId);


        int count =  studentClockService.count(new QueryWrapper<StudentClock>().lambda().eq(StudentClock::getUserId,SecureUtil.getUserId()).eq(StudentClock::getStudentId,studentId).eq(StudentClock::getClockDay,new Date()));

        StudentVO studentVO =  StudentWrapper.build().entityVO(student);
        studentVO.setIsClock(count>0);
        return R.data(student);
    }

    @GetMapping("/user/userStatus")
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "我的资料", notes = "我的资料")
    public R userStatus() {

        Integer userId = SecureUtil.getUserId();

        Integer  studentStatus = studentService.getStudent(userId).getStatus();
        return R.data(studentStatus);
    }
    @GetMapping("/user/userInfo")
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "我的资料", notes = "我的资料")
    public R userInfo() {

        Integer userId = SecureUtil.getUserId();

        Student  student = studentService.getStudent(userId);
        return R.data(student);
    }
    /**
     * 我赠与的课程
     */
    @Deprecated
    @GetMapping("/user/myCourses")
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "我赠与的课程", notes = "我赠与的课程")
    public R myCourses(TrxorderDetail trxorderDetail, Query query) {
        //判断直播与点播课程
        Integer userId = SecureUtil.getUserId();
        //判断如果没有用户信息则进行登录
        if(Func.equals(userId,-1)){
            return R.fail(ResultCode.UN_AUTHORIZED);
        }


        Map<String, Object> param = Maps.newHashMap();
        param.put("userId",userId);

        IPage<TrxorderDetail> pages = trxorderDetailService.page(Condition.getPage(query), Condition.getQueryWrapper(param,TrxorderDetail.class));
        if(pages.getRecords().size()!=0){
            if(pages.getRecords().get(0).getTrxorderType().equals("1")){
                pages.getRecords().get(0).setTrxorderType("COURSE");
            }
            if(pages.getRecords().get(0).getTrxorderType().equals("2")){
                pages.getRecords().get(0).setTrxorderType("LIVE");
            }
            if(pages.getRecords().get(0).getTrxorderType().equals("3")){
                pages.getRecords().get(0).setTrxorderType("PACKAGE");
            }
            if(pages.getRecords().get(0).getTrxorderType().equals("4")){
                pages.getRecords().get(0).setTrxorderType("LINECLASS");
            }
        }
        return R.data(pages);

    }
    @GetMapping("/user/subStudents")
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "", notes = "所有绑定的学生")
    public R subStudents() {
        QueryWrapper<Student> studentQueryWrapper = new QueryWrapper<>();
        studentQueryWrapper.lambda().eq(Student::getParentId,SecureUtil.getUserId());
        List<Student> pages = studentService.list(studentQueryWrapper);
        return R.data(StudentWrapper.build().listVO(pages));
    }

    /**
     * 保存学生信息
     */
    @PostMapping("/submitStudent")
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "查询教师详情", notes = "保存学生信息")
    public R submitStudent(StudentVO student) {

        int count = studentService.count(new QueryWrapper<Student>().lambda().eq(Student::getParentId,SecureUtil.getUserId()).eq(Student::getIsDefault,true));
        student.setParentId(SecureUtil.getUserId());
        student.setRegisterFrom("3");//app家长注册
        if(count==0){
            student.setIsDefault(true);
        }
        //student.setSchoolId(SecureUtil.getUser().get);
        studentService.saveOrUpdate(student);
        //Teacher teacher = teacherService.getById(id);
        return R.data(true);
    }

    @PostMapping("/user/setDefaultStudent")
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "查询教师详情", notes = "移除学生信息")
    public R setDefaultStudent(int studentId) {

        List<Student> students =  studentService.list(new QueryWrapper<Student>().lambda().eq(Student::getParentId,SecureUtil.getUserId()));
        List<Student> newStudents = students.stream().map(i->{
            i.setIsDefault(false);
            if(i.getId()==studentId){
                i.setIsDefault(true);
            }
            return i;
        }).collect(Collectors.toList());
        boolean res =  studentService.saveOrUpdateBatch(newStudents);

        return R.data(res);
    }

    @PostMapping("/user/removeStudent")
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "查询教师详情", notes = "移除学生信息")
    public R removeStudent(int studentId) {
        boolean  count = studentService.remove(new QueryWrapper<Student>().lambda().eq(Student::getParentId,SecureUtil.getUserId()).eq(Student::getId,studentId));
        return R.data(count);
    }
    @GetMapping("/user/getStudent")
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "查询教师详情", notes = "获取学生信息")
    public R getStudentById(int studentId) {
        Student  count = studentService.getById(studentId);
        return R.data(StudentWrapper.build().entityVO(count));
    }
}
