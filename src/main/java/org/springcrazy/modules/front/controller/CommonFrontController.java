
package org.springcrazy.modules.front.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;

import com.google.common.collect.Maps;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springcrazy.common.cache.CacheNames;
import org.springcrazy.common.tool.CommonUtil;
import org.springcrazy.common.tool.CossUtil;
import org.springcrazy.common.tool.CourseUtils;
import org.springcrazy.common.tool.ProfileConfig;
import org.springcrazy.core.boot.ctrl.CrazyController;
import org.springcrazy.core.boot.file.CrazyFile;
import org.springcrazy.core.launch.props.CrazyProperties;
import org.springcrazy.core.launch.server.ServerInfo;
import org.springcrazy.core.log.exception.ServiceException;
import org.springcrazy.core.mp.support.Condition;
import org.springcrazy.core.secure.AuthInfo;
import org.springcrazy.core.secure.CrazyUser;
import org.springcrazy.core.secure.utils.SecureUtil;
import org.springcrazy.core.tool.api.R;
import org.springcrazy.core.tool.api.ResultCode;
import org.springcrazy.core.tool.constant.SystemConstant;
import org.springcrazy.core.tool.utils.*;
import org.springcrazy.modules.auth.granter.ITokenGranter;
import org.springcrazy.modules.auth.granter.TokenGranterBuilder;
import org.springcrazy.modules.auth.granter.TokenParameter;
import org.springcrazy.modules.auth.service.OnlineUserService;
import org.springcrazy.modules.auth.utils.TokenUtil;
import org.springcrazy.modules.clazz.entity.ClassGrade;
import org.springcrazy.modules.clazz.entity.Grade;
import org.springcrazy.modules.clazz.service.ClassGradeService;
import org.springcrazy.modules.clazz.service.GradeService;
import org.springcrazy.modules.cms.entity.WebsiteProfile;
import org.springcrazy.modules.edu.dto.OrdersDTO;
import org.springcrazy.modules.edu.entity.*;
import org.springcrazy.modules.edu.service.*;
import org.springcrazy.modules.edu.vo.CourseVO;
import org.springcrazy.modules.edu.vo.SubjectVO;
import org.springcrazy.modules.edu.wrapper.SubjectWrapper;
import org.springcrazy.modules.lineclass.entity.LineclassEnroll;
import org.springcrazy.modules.lineclass.service.ILineclassEnrollService;
import org.springcrazy.modules.partyLogin.entity.UserProfile;
import org.springcrazy.modules.partyLogin.service.IUserProfileService;
import org.springcrazy.modules.system.entity.Dept;
import org.springcrazy.modules.system.entity.Dict;
import org.springcrazy.modules.system.entity.UserInfo;
import org.springcrazy.modules.system.service.IDeptService;
import org.springcrazy.modules.system.service.IDictService;
import org.springcrazy.modules.system.service.ILogService;
import org.springcrazy.modules.user.entity.Student;
import org.springcrazy.modules.user.service.IStudentService;
import org.springcrazy.modules.web.entity.MsgConfig;
import org.springcrazy.modules.web.service.IMsgConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
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
@RequestMapping("/front/common")
@Api(value = "前端用户相关接口", tags = "前端接口")
public class CommonFrontController extends CrazyController {

    IStudentService studentService;
    private OnlineUserService onlineUserService;
    private RedisUtil redisUtil;
    IMsgConfigService msgConfigService;
    private ICourseService courseService;
    private ICoursePackageService coursePackageService;

    private IOrdersService ordersService;
    private ITrxorderDetailService trxorderDetailService;
    private ILineclassEnrollService lineclassEnrollService;
    private IUserProfileService userProfileService;

    private final ISubjectService subjectService;
    private final IDeptService deptService;
    private final ClassGradeService gradeService;
    private final ILogService logService;
    private final ServerInfo serverInfo;
    private final CrazyProperties crazyProperties;

    @Autowired
    CossUtil cossUtil;
    @Autowired
    IDictService dictService;

    /**
     * 通用上传请求
     */
    @PostMapping("/uploadOss")
    public R uploadOss(MultipartFile file,HttpServletRequest httpServletRequest){

        SystemConstant me = SystemConstant.me();
        //获取文件名判断文件是否支持上传
        if(me.getFileExt().size() > 0){
            if(!me.getFileExt().contains(FileUtil.getFileExtension(file.getOriginalFilename()))){
                return R.fail("不支持该文件类型上传");
            }
        }
        String url ="";
        String frontPath = crazyProperties.get("front-icon-path");
        String adminPath = crazyProperties.get("admin-icon-path");
        CrazyFile crazyFile = getFile(file,"ico");
        crazyFile.transfer();
        String tenant  = httpServletRequest.getHeader("tenant");
        try {
            url =  cossUtil.upload(crazyFile,tenant);
        } catch (Exception e) {
            e.printStackTrace();
            return R.fail("上传失败");
        }
        //上传文件
        Map<String,String> result = Maps.newHashMap();
        result.put("url", url);
        return R.data(result);
    }

    @GetMapping("/schools")
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "部门信息", notes = "获取所有学校")
    @ApiImplicitParams({
            @ApiImplicitParam(name="teacherId",value="teacherId",dataType="string", paramType = "query"),
    })
    public R<List<Dept>> schools() {
        QueryWrapper<Dept> deptQueryWrapper = new QueryWrapper<Dept>();
        deptQueryWrapper.lambda().eq(Dept::getType,1)
        ;
        // int pcount = courseService.count(coursePackageQueryWrapper);

        List<Dept> courseList =   deptService.list(deptQueryWrapper);

        return R.data(courseList);
    }

    @GetMapping("/grades")
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "年级信息", notes = "获取所有年级")
    @ApiImplicitParams({
            @ApiImplicitParam(name="schoolId",value="schoolId",dataType="string", paramType = "query"),
    })
    public R<List<ClassGrade>> grades() {

        String deptId = SecureUtil.getDeptId();

        QueryWrapper<ClassGrade> deptQueryWrapper = new QueryWrapper<ClassGrade>();
        deptQueryWrapper.lambda().eq(ClassGrade::getSchoolId,deptId);


        // int pcount = courseService.count(coursePackageQueryWrapper);
        List<ClassGrade> gradeList =   gradeService.list(deptQueryWrapper);
        return R.data(gradeList);
    }

    @GetMapping("/dict")
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "部门信息", notes = "获取字典")
    public R<List<Dict>> dictionary(String code) {
        List<Dict> dicts = dictService.getList(code);
        return R.data(dicts);
    }

    /**
     * 分页 专业分类
     */
    @GetMapping("/subjectList")
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "分页", notes = "传入subject")
    public R<List<SubjectVO>> list(Subject subject) {
        List<Subject> list = new ArrayList<>();
        if(ObjectUtil.isNotEmpty(subject.getType())){
            if("lineclass".equals(subject.getType())){
                QueryWrapper<Subject> subjectWrapper = Condition.getQueryWrapper(new Subject());
                subjectWrapper.lambda().like(Func.isNotBlank(subject.getSubjectName()),Subject::getSubjectName,subject.getSubjectName())
                        .eq(Subject::getType, subject.getType()).orderByDesc(Subject::getSort);
                list =subjectService.list(subjectWrapper);
            }else {
                list =subjectService.list(Condition.getQueryWrapper(subject).lambda().orderByDesc(Subject::getSort));
            }
        }else {
            list =subjectService.list(Condition.getQueryWrapper(subject).lambda().orderByDesc(Subject::getSort));
        }
        return R.data(SubjectWrapper.build().listNodeVO(list));
    }

    @GetMapping("/topSubjects")
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "部门信息", notes = "获取所有学校")
    @ApiImplicitParams({
            @ApiImplicitParam(name="teacherId",value="teacherId",dataType="string", paramType = "query"),
    })
    public R<List<Dept>> topSubjects() {
        QueryWrapper<Dept> deptQueryWrapper = new QueryWrapper<Dept>();
        deptQueryWrapper.lambda().eq(Dept::getType,1)
        ;
        // int pcount = courseService.count(coursePackageQueryWrapper);
        List<Dept> courseList =   deptService.list(deptQueryWrapper);
        return R.data(courseList);
    }

    @GetMapping("/subjectTree")
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "部门信息", notes = "获取所有学校")
    @ApiImplicitParams({
            @ApiImplicitParam(name="teacherId",value="teacherId",dataType="string", paramType = "query"),
    })
    public R<List<Dept>> subjectTree() {
        QueryWrapper<Dept> deptQueryWrapper = new QueryWrapper<Dept>();
        deptQueryWrapper.lambda().eq(Dept::getType,1)
        ;
        // int pcount = courseService.count(coursePackageQueryWrapper);

        List<Dept> courseList =   deptService.list(deptQueryWrapper);

        return R.data(courseList);
    }


    @GetMapping("/systemInfo")
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "部门信息", notes = "获取所有学校")
    @ApiImplicitParams({
            @ApiImplicitParam(name="teacherId",value="teacherId",dataType="string", paramType = "query"),
    })
    public R systemInfo() {


        return R.data("");
    }
}
