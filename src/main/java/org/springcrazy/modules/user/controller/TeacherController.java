
package org.springcrazy.modules.user.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.google.common.collect.Maps;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.AllArgsConstructor;
import org.springcrazy.core.boot.ctrl.CrazyController;
import org.springcrazy.core.mp.support.Condition;
import org.springcrazy.core.mp.support.Query;
import org.springcrazy.core.secure.CrazyUser;
import org.springcrazy.core.secure.utils.SecureUtil;
import org.springcrazy.core.tool.api.R;
import org.springcrazy.core.tool.constant.CrazyConstant;
import org.springcrazy.core.tool.utils.DigestUtil;
import org.springcrazy.core.tool.utils.Func;
import org.springcrazy.modules.partyLogin.entity.UserProfile;
import org.springcrazy.modules.system.entity.User;
import org.springcrazy.modules.system.service.IUserService;
import org.springcrazy.modules.system.vo.UserVO;
import org.springcrazy.modules.system.wrapper.UserWrapper;
import org.springcrazy.modules.user.entity.Student;
import org.springcrazy.modules.user.entity.Teacher;
import org.springcrazy.modules.user.entity.UserLoginLog;
import org.springcrazy.modules.user.service.ITeacherService;
import org.springcrazy.modules.user.vo.TeacherVO;
import org.springcrazy.modules.user.wrapper.TeacherWrapper;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * 讲师 控制器
 *
 * @author TongZhou
 * @since 2020-04-28
 */
@RestController
@AllArgsConstructor
@RequestMapping("/user/teacher")
@Api(value = "讲师", tags = "讲师接口")
public class TeacherController extends CrazyController {

	private ITeacherService teacherService;
	private IUserService userService;
	/**
	 * 详情
	 */
	@GetMapping("/detail")
	@ApiOperationSupport(order = 1)
	@ApiOperation(value = "详情", notes = "传入teacher")
	public R<Teacher> detail(Teacher teacher) {
		Teacher detail = teacherService.getOne(Condition.getQueryWrapper(teacher));
		return R.data(detail);
	}

	/**
	 * 分页 讲师
	 */
	@GetMapping("/list")
	@ApiOperationSupport(order = 2)
	@ApiOperation(value = "分页", notes = "传入teacher")
	public R<IPage<TeacherVO>> list(Teacher teacher, Query query) {
		Map<String, Object> param = Maps.newHashMap();
		param.put("name_like",teacher.getName());
		param.put("subjectId_equal",teacher.getSubjectId());
		param.put("teaTitle_equal",teacher.getTeaTitle());

		System.out.println(SecureUtil.getUserRole());
		if(!SecureUtil.getUserRole().equals("administrator")){
			param.put("schoolId",SecureUtil.getDeptId());
		}
		QueryWrapper<Teacher> queryWrapper = Condition.getQueryWrapper(param, Teacher.class);
		queryWrapper.orderByDesc("sort desc,id");
		IPage<Teacher> pages = teacherService.page(Condition.getPage(query),queryWrapper );


		return R.data(TeacherWrapper.build().pageVO(pages));
	}
	/**
	 * 分页 讲师
	 */
	@GetMapping("/all")
	@ApiOperationSupport(order = 2)
	@ApiOperation(value = "分页", notes = "传入teacher")
	public R<List<TeacherVO>> all(Teacher teacher) {
		Map<String, Object> param = Maps.newHashMap();
//		param.put("name_like",teacher.getName());
//		param.put("subjectId_equal",teacher.getSubjectId());
//		param.put("teaTitle_equal",teacher.getTeaTitle());

		System.out.println(SecureUtil.getUserRole());
		if(!SecureUtil.getUserRole().equals("administrator")){
			param.put("schoolId",SecureUtil.getDeptId());
		}
		QueryWrapper<Teacher> queryWrapper = Condition.getQueryWrapper(param, Teacher.class);
		queryWrapper.orderByDesc("sort desc,id");
		List<Teacher> data = teacherService.list(queryWrapper);


		return R.data(TeacherWrapper.build().listVO(data));
	}
	/**
	 * 自定义分页 讲师
	 */
	@GetMapping("/page")
	@ApiOperationSupport(order = 3)
	@ApiOperation(value = "分页", notes = "传入teacher")
	public R<IPage<TeacherVO>> page(TeacherVO teacher, Query query) {

		if(!SecureUtil.getUserRole().equals("administrator")){
			teacher.setSchoolId(Long.parseLong(SecureUtil.getDeptId()));
		}

		IPage<Teacher> pages = teacherService.selectTeacherPage(Condition.getPage(query), teacher);
		return R.data(TeacherWrapper.build().pageVO(pages));
	}

	/**
	 * 新增 讲师
	 */
	@PostMapping("/save")
	@ApiOperationSupport(order = 4)
	@ApiOperation(value = "新增", notes = "传入teacher")
	public R save(@Valid @RequestBody Teacher teacher) {
		if(!SecureUtil.getUserRole().equals("administrator")){
			teacher.setSchoolId(Long.parseLong(SecureUtil.getDeptId()));
		}
		teacherService.save(teacher);

		User user= new User();
		user.setName(teacher.getName());
		user.setAccount(teacher.getUserName());
		user.setPassword(DigestUtil.encrypt(teacher.getPassword()));
		user.setTenantId("000000");
		user.setDeptId(SecureUtil.getDeptId());
		user.setRoleId("6");
		user.setRealName(teacher.getName());
		user.setPhone(teacher.getMobile());
		userService.save(user);
		teacher.setUserId(user.getId());
		teacherService.updateById(teacher);
		//Teacher teacher1 =  teacherService.save(teacher);
		//UserLoginLog userLoginLog = new UserLoginLog();
		//userLoginLog.set
		return R.status(true);
	}

	/**
	 * 修改密码
	 *
	 * @param oldPassword
	 * @param newPassword
	 * @param newPassword1
	 * @return
	 */
	@PostMapping("/update-password")
	@ApiOperationSupport(order = 9)
	@ApiOperation(value = "修改教師賬號密码", notes = "传入密码")
	public R updatePassword(Integer teacherId, @ApiParam(value = "旧密码", required = true) @RequestParam String oldPassword,
							@ApiParam(value = "新密码", required = true) @RequestParam String newPassword,
							@ApiParam(value = "新密码", required = true) @RequestParam String newPassword1) {

		Teacher teacher =  teacherService.getById(teacherId);

		boolean temp = userService.updatePassword(teacher.getUserId(), oldPassword, newPassword, newPassword1);
		return R.status(temp);
	}


	/**
	 * 修改 讲师
	 */
	@PostMapping("/update")
	@ApiOperationSupport(order = 5)
	@ApiOperation(value = "修改", notes = "传入teacher")
	public R update(@Valid @RequestBody Teacher teacher) {
		if(!SecureUtil.getUserRole().equals("administrator")){
			teacher.setSchoolId(Long.parseLong(SecureUtil.getDeptId()));
		}
		User user =  userService.getById(teacher.getUserId());
		user.setPassword(DigestUtil.encrypt(user.getPassword()));
		userService.updateById(user);
//
//		boolean temp = userService.updatePassword(teacher.getUserId(), oldPassword, newPassword, newPassword1);


		return R.status(teacherService.updateById(teacher));
	}

	/**
	 * 新增或修改 讲师
	 */
	@PostMapping("/submit")
	@ApiOperationSupport(order = 6)
	@ApiOperation(value = "新增或修改", notes = "传入teacher")
	public R submit(@Valid @RequestBody Teacher teacher) {
		if(!SecureUtil.getUserRole().equals("administrator")){
			teacher.setSchoolId(Long.parseLong(SecureUtil.getDeptId()));
		}

		User exist =  userService.getOne(new LambdaQueryWrapper<User>().eq(User::getAccount,teacher.getUserName()));
		if(exist!=null){
			exist.setName(teacher.getName());
			exist.setAccount(teacher.getUserName());
			exist.setPassword(DigestUtil.encrypt(teacher.getPassword()));
			exist.setTenantId("000000");
			exist.setDeptId(SecureUtil.getDeptId());
			exist.setRoleId("6");
			exist.setRealName(teacher.getName());
			exist.setPhone(teacher.getMobile());
			userService.updateById(exist);
			teacher.setUserId(exist.getId());
		}else{
			User user= new User();
			user.setName(teacher.getName());
			user.setAccount(teacher.getUserName());
			user.setPassword(DigestUtil.encrypt(teacher.getPassword()));
			user.setTenantId("000000");
			user.setDeptId(SecureUtil.getDeptId());
			user.setRoleId("6");
			user.setRealName(teacher.getName());
			user.setPhone(teacher.getMobile());
			userService.save(user);
			teacher.setUserId(user.getId());
		}

		teacher.setDeptId(SecureUtil.getDeptId());
		teacherService.saveOrUpdate(teacher);


		teacherService.updateById(teacher);

		return R.status(true);
	}


	/**
	 * 删除 讲师
	 */
	@PostMapping("/remove")
	@ApiOperationSupport(order = 8)
	@ApiOperation(value = "删除", notes = "传入ids")
	public R remove(@ApiParam(value = "主键集合", required = true) @RequestParam String ids) {
		return R.status(teacherService.removeByIds(Func.toIntList(ids)));
	}


	/**
	 * 详情
	 */
	@GetMapping("")
	@ApiOperationSupport(order = 1)
	@ApiOperation(value = "详情", notes = "传入student")
	public R<Teacher> student() {
		CrazyUser user = SecureUtil.getUser();

		Map<String, Object> param = Maps.newHashMap();
		param.put("userId",user.getUserId());

		QueryWrapper<Teacher> queryWrapper = Condition.getQueryWrapper(param, Teacher.class);
		Teacher detail = teacherService.getOne(queryWrapper);

		//Teacher detail = teacherService.getById(user.getUserId());
//		detail.setWeibo("");
//		detail.setWeiboname("");
//		detail.setWechat("");
//		detail.setWechatname("");
//		detail.setQq("");
//		detail.setQqname("");
//		detail.setOpenId("");
	//	List<UserProfile> userProfileList = userProfileService.getStudetThreeLise(user.getUserId());
//		for(UserProfile profile:userProfileList){
//			if(profile.getProfiletype().equals("qqPC")||profile.getProfiletype().equals("qqApp")){
//				detail.setQq(profile.getOpenid());
//				detail.setQqname(profile.getName());
//			}
//			if(profile.getProfiletype().equals("wxPC")||profile.getProfiletype().equals("wxH5")||profile.getProfiletype().equals("wxApp")){
//				detail.setWechat(profile.getUnionid());
//				detail.setWechatname(profile.getName());
//				detail.setOpenId(profile.getOpenid());
//			}
//			if(profile.getProfiletype().equals("weibo")){
//				detail.setWeibo(profile.getOpenid());
//				detail.setWeiboname(profile.getName());
//			}
//		}
		return R.data(detail);
	}



}
