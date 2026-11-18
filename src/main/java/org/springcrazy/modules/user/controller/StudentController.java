
package org.springcrazy.modules.user.controller;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.read.builder.ExcelReaderBuilder;
import com.alibaba.excel.support.ExcelTypeEnum;
import com.alibaba.excel.write.builder.ExcelWriterSheetBuilder;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.alibaba.excel.write.metadata.WriteWorkbook;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.google.common.collect.Maps;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import lombok.AllArgsConstructor;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import lombok.SneakyThrows;
import org.apache.commons.codec.Charsets;
import org.apache.commons.lang.math.NumberUtils;
import org.springcrazy.core.mp.support.Condition;
import org.springcrazy.core.mp.support.Query;
import org.springcrazy.core.secure.CrazyUser;
import org.springcrazy.core.secure.utils.SecureUtil;
import org.springcrazy.core.tool.api.R;
import org.springcrazy.core.tool.constant.CrazyConstant;
import org.springcrazy.core.tool.utils.DigestUtil;
import org.springcrazy.core.tool.utils.Func;
import org.springcrazy.modules.auth.service.OnlineUserService;
import org.springcrazy.modules.edu.entity.TrxorderDetail;
import org.springcrazy.modules.edu.service.ITrxorderDetailService;
import org.springcrazy.modules.partyLogin.entity.UserProfile;
import org.springcrazy.modules.partyLogin.service.IUserProfileService;
import org.springcrazy.modules.system.entity.User;
import org.springcrazy.modules.system.excel.*;
import org.springcrazy.modules.user.entity.UserAccount;
import org.springcrazy.modules.user.wrapper.StudentWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springcrazy.modules.user.entity.Student;
import org.springcrazy.modules.user.vo.StudentVO;
import org.springcrazy.modules.user.service.IStudentService;
import org.springcrazy.core.boot.ctrl.CrazyController;
import org.springframework.web.multipart.MultipartFile;
import springfox.documentation.annotations.ApiIgnore;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 学员表 控制器
 *
 * @author TongZhou
 * @since 2020-04-28
 */
@RestController
@AllArgsConstructor
@RequestMapping("/user/student")
@Api(value = "学员表", tags = "学员表接口")
public class StudentController extends CrazyController {

	private IStudentService studentService;
	private final OnlineUserService onlineUserService;
	private IUserProfileService userProfileService;
	private ITrxorderDetailService trxorderDetailService;
	/**
	 * 详情
	 */
	@GetMapping("/detail")
	@ApiOperationSupport(order = 1)
	@ApiOperation(value = "详情", notes = "传入student")
	public R<StudentVO> detail(Student student) {
		Student detail = studentService.getOne(Condition.getQueryWrapper(student));

		return R.data(StudentWrapper.build().entityVO(detail));
	}


	/**
	 * 详情
	 */
	@GetMapping("")
	@ApiOperationSupport(order = 1)
	@ApiOperation(value = "详情", notes = "传入student")
	public R<Student> student() {
		CrazyUser user = SecureUtil.getUser();
		Student detail = studentService.getById(user.getUserId());
		detail.setWeibo("");
		detail.setWeiboname("");
		detail.setWechat("");
		detail.setWechatname("");
		detail.setQq("");
		detail.setQqname("");
		detail.setOpenId("");
		List<UserProfile> userProfileList = userProfileService.getStudetThreeLise(user.getUserId());
		for(UserProfile profile:userProfileList){
			if(profile.getProfiletype().equals("qqPC")||profile.getProfiletype().equals("qqApp")){
				detail.setQq(profile.getOpenid());
				detail.setQqname(profile.getName());
			}
			if(profile.getProfiletype().equals("wxPC")||profile.getProfiletype().equals("wxH5")||profile.getProfiletype().equals("wxApp")){
				detail.setWechat(profile.getUnionid());
				detail.setWechatname(profile.getName());
				detail.setOpenId(profile.getOpenid());
			}
			if(profile.getProfiletype().equals("weibo")){
				detail.setWeibo(profile.getOpenid());
				detail.setWeiboname(profile.getName());
			}
		}

		List<TrxorderDetail> trxorderDetailList = 	trxorderDetailService.list(new LambdaQueryWrapper<TrxorderDetail>()
				.eq(TrxorderDetail::getUserId,detail.getId())
				.eq(TrxorderDetail::getTrxorderType,"PACKAGE")
			);
		if(trxorderDetailList.size()>0){
			detail.setClazzId(trxorderDetailList.get(trxorderDetailList.size()-1).getCourseId());
			detail.setClazzName(trxorderDetailList.get(trxorderDetailList.size()-1).getCourseName());
		}

		return R.data(detail);
	}

	/**
	 * 分页 学员表
	 */
	@GetMapping("/removeStudentClazz")
	@ApiOperationSupport(order = 2)
	public R removeStudentClazz(@RequestParam int id,@RequestParam int courseId) {
		LambdaQueryWrapper<TrxorderDetail> queryWrapper = new LambdaQueryWrapper<TrxorderDetail>();
		queryWrapper
				.eq(TrxorderDetail::getCourseId,courseId)
				.eq(TrxorderDetail::getTrxorderType,"PACKAGE")
				.eq(TrxorderDetail::getUserId,id)
				;
		trxorderDetailService.remove(queryWrapper);
		return R.data("");
	}



	@GetMapping("/list")
	@ApiOperationSupport(order = 2)
	@ApiOperation(value = "分页", notes = "传入student")
	public R<IPage<StudentVO>> list(Student student, Query query) {
		//如果值为-1说明是代理商查询，则设置当登录用户的id设置
		if(Func.isNotEmpty(student.getAgentId())&&-1==student.getAgentId()){
			student.setAgentId(SecureUtil.getUserId());
		}

		if(!SecureUtil.getUserRole().equals("administrator")){
			if(Func.isNotBlank(SecureUtil.getDeptId())){
				student.setSchoolId(Long.parseLong(SecureUtil.getDeptId()));
			}
		}

		Map<String,Object> params = Maps.newHashMap();
		params.put("id_like",student.getId());
		params.put("mobile_like",student.getMobile());
		params.put("email_like",student.getEmail());
		params.put("userName_like",student.getUserName());
		params.put("showName_like",student.getShowName());
		if(!SecureUtil.getUserRole().equals("administrator")){
			System.out.println(SecureUtil.getDeptId());
			//params.put("schoolId",SecureUtil.getDeptId());
		}
		QueryWrapper<Student> queryWrapper = Condition.getQueryWrapper(params, Student.class);
		queryWrapper.lambda()
				.like(Func.isNotBlank(student.getRealName()),Student::getRealName,student.getRealName())
				.eq(Func.isNotBlank(student.getRegisterFrom()),Student::getRegisterFrom,student.getRegisterFrom())
				.eq(Func.isNotEmpty(student.getAgentId()),Student::getAgentId,student.getAgentId())
				.eq(Func.isNotBlank(student.getSubject()),Student::getSubject,student.getSubject()).orderByDesc(Student::getId);

		if(!SecureUtil.getUserRole().equals("administrator")){
			queryWrapper.lambda().eq(Student::getSchoolId,SecureUtil.getDeptId());
		}

		IPage<Student> pages = studentService.page(Condition.getPage(query), queryWrapper);

		return R.data(StudentWrapper.build().pageVO(pages));
	}


	@GetMapping("/getStudentsClazz")
	@ApiOperationSupport(order = 2)
	@ApiOperation(value = "分页", notes = "传入student")
	public R<IPage<StudentVO>> getStudentsClazz(StudentVO student, Query query) {
		//如果值为-1说明是代理商查询，则设置当登录用户的id设置
		if(Func.isNotEmpty(student.getAgentId())&&-1==student.getAgentId()){
			student.setAgentId(SecureUtil.getUserId());
		}

		if(!SecureUtil.getUserRole().equals("administrator")){
			if(Func.isNotBlank(SecureUtil.getDeptId())){
				student.setSchoolId(Long.parseLong(SecureUtil.getDeptId()));
			}
		}

		IPage<StudentVO> pages = studentService.selectStudentInClazzPage(Condition.getPage(query), student);

		return R.data(pages);
	}

	@GetMapping("/getStudentsNotinClazz")
	@ApiOperationSupport(order = 2)
	@ApiOperation(value = "分页", notes = "传入student")
	public R<IPage<StudentVO>> getStudentsNotinClazz(StudentVO student, Query query) {
		//如果值为-1说明是代理商查询，则设置当登录用户的id设置
		if(Func.isNotEmpty(student.getAgentId())&&-1==student.getAgentId()){
			student.setAgentId(SecureUtil.getUserId());
		}

		if(!SecureUtil.getUserRole().equals("administrator")){
			if(Func.isNotBlank(SecureUtil.getDeptId())){
				student.setSchoolId(Long.parseLong(SecureUtil.getDeptId()));
			}
		}

		IPage<StudentVO> pages = studentService.selectStudentNotInClazzPage(Condition.getPage(query), student);

		return R.data(pages);
	}


	@GetMapping("/selectClazzStudentPage")
	@ApiOperationSupport(order = 2)
	@ApiOperation(value = "分页", notes = "传入student")
	public R<IPage<StudentVO>> selectClazzStudentPage(StudentVO student, Query query) {
		//如果值为-1说明是代理商查询，则设置当登录用户的id设置
		if(Func.isNotEmpty(student.getAgentId())&&-1==student.getAgentId()){
			student.setAgentId(SecureUtil.getUserId());
		}

		if(!SecureUtil.getUserRole().equals("administrator")){
			if(Func.isNotBlank(SecureUtil.getDeptId())){
				student.setSchoolId(Long.parseLong(SecureUtil.getDeptId()));
			}
		}

		IPage<StudentVO> pages = studentService.selectClazzStudentPage(Condition.getPage(query), student);

		return R.data(pages);
	}


	/**
	 * 分页 学员表
	 */
	@GetMapping("/studentList")
	@ApiOperationSupport(order = 2)
	@ApiOperation(value = "分页", notes = "传入student")
	public R<IPage<StudentVO>> studentList(StudentVO student, Query query) {
		//如果值为-1说明是代理商查询，则设置当登录用户的id设置
		if(Func.isNotEmpty(student.getAgentId())&&-1==student.getAgentId()){
			student.setAgentId(SecureUtil.getUserId());
		}

		if(!SecureUtil.getUserRole().equals("administrator")){
			if(Func.isNotBlank(SecureUtil.getDeptId())){
				student.setSchoolId(Long.parseLong(SecureUtil.getDeptId()));
			}
		}

		IPage<StudentVO> pages = studentService.selectStudentInClazzPage(Condition.getPage(query), student);

		return R.data(pages);
	}

	/**
	 * 自定义分页 学员表
	 */
	@GetMapping("/page")
	@ApiOperationSupport(order = 3)
	@ApiOperation(value = "分页", notes = "传入student")
	public R<IPage<StudentVO>> page(StudentVO student, Query query) {

		if(!SecureUtil.getUserRole().equals("administrator")){
			student.setSchoolId(Long.parseLong(SecureUtil.getDeptId()));
		}

		IPage<StudentVO> pages = studentService.selectStudentPage(Condition.getPage(query), student);
		return R.data(pages);
	}

	/**
	 * 新增 学员表
	 */
	@PostMapping("/save")
	@ApiOperationSupport(order = 4)
	@ApiOperation(value = "新增", notes = "传入student")
	public R save(@Valid @RequestBody Student student) {
		//如果值为-1说明是代理商查询，则设置当登录用户的id设置
		if(Func.isNotEmpty(student.getAgentId())&&-1==student.getAgentId()){
			student.setAgentId(SecureUtil.getUserId());
		}
		if(!SecureUtil.getUserRole().equals("administrator")){
			student.setSchoolId(Long.parseLong(SecureUtil.getDeptId()));
			student.setDeptId(SecureUtil.getDeptId());
		}
		student.setCreater(SecureUtil.getUserName());
		student.setCreateTime(LocalDateTime.now());
		//检查手机号或账号或邮箱是否已存在
		if (Func.isNotEmpty(student.getMobile())) {
			Student tempMobile = studentService.checkStudentMobile(student);
			if (Func.isNotEmpty(tempMobile)) {
				return R.fail("手机号已存在");
			}
		}

		if (Func.isNotEmpty(student.getEmail())) {
			Student tempEmail = studentService.checkStudentEmail(student);
			if (Func.isNotEmpty(tempEmail)) {
				return R.fail("邮箱已存在");
			}
		}

		if (Func.isNotEmpty(student.getUserName())) {
			Student tempUserName = studentService.checkStudentUserName(student);
			if (Func.isNotEmpty(tempUserName)) {
				return R.fail("账号已存在");
			}
		}
		//如果注册来源为空则设置为4
		if(Func.isEmpty(student.getRegisterFrom())){
			student.setRegisterFrom("4");
		}

		studentService.register(student);
		return R.status(true);
	}

	/**
	 * 修改 学员表
	 */
	@PostMapping("/update")
	@ApiOperationSupport(order = 5)
	@ApiOperation(value = "修改", notes = "传入student")
	public R update(@Valid @RequestBody Student student) {
		return R.status(studentService.updateById(student));
	}


	/**
	 * 新增或修改 学员表
	 */
	@PostMapping("/submit")
	@ApiOperationSupport(order = 6)
	@ApiOperation(value = "新增或修改", notes = "传入student")
	public R submit(@RequestBody Student student) {

		if(!SecureUtil.getUserRole().equals("administrator")){
			student.setSchoolId(Long.parseLong(SecureUtil.getDeptId()));
			student.setDeptId(SecureUtil.getDeptId());
		}
		student.setCreater(SecureUtil.getUserName());
		student.setCreateTime(LocalDateTime.now());
		//检查手机号或账号或邮箱是否已存在
		if (Func.isNotEmpty(student.getMobile())) {
			Student tempMobile = studentService.checkStudentMobile(student);
			if ( Func.isNotEmpty(tempMobile) && !student.getId().equals(tempMobile.getId())) {
				return R.fail("手机号已存在");
			}
		}

		if (Func.isNotEmpty(student.getEmail())) {
			Student tempEmail = studentService.checkStudentEmail(student);
			if (Func.isNotEmpty(tempEmail)  && !student.getId().equals(tempEmail.getId())) {
				return R.fail("邮箱已存在");
			}
		}

		if (Func.isNotEmpty(student.getUserName())) {
			Student tempUserName = studentService.checkStudentUserName(student);
			if (Func.isNotEmpty(tempUserName) && !student.getId().equals(tempUserName.getId())) {
				return R.fail("账号已存在");
			}
		}
		//冻结学员
		if (student.getIsAvalible() == 1){
			String key = "online" + (Func.isNull(student.getMobile())? (Func.isNull(student.getUserName())?student.getEmail():student.getUserName()):student.getMobile());
			onlineUserService.kickOut(key);
		}
		return R.status(studentService.saveOrUpdate(student));
	}


	/**
	 * 删除 学员表
	 */
	@PostMapping("/remove")
	@ApiOperationSupport(order = 8)
	@ApiOperation(value = "删除", notes = "传入ids")
	public R remove(@ApiParam(value = "主键集合", required = true) @RequestParam String ids) {
		userProfileService.delectUserProfile(Func.toIntList(ids));
		return R.status(studentService.removeByIds(Func.toIntList(ids)));
	}

	/**
	 * 导入用户
	 */
	@PostMapping("import-user")
	@ApiOperationSupport(order = 12)
	@ApiOperation(value = "导入用户", notes = "传入excel")
	public R importUser(MultipartFile file) {
		String filename = file.getOriginalFilename();
		if (StringUtils.isEmpty(filename)) {
			throw new RuntimeException("请上传文件!");
		}
		if ((!StringUtils.endsWithIgnoreCase(filename, ".xls") && !StringUtils.endsWithIgnoreCase(filename, ".xlsx"))) {
			throw new RuntimeException("请上传正确的excel文件!");
		}
		InputStream inputStream;
		try {
			StudentImportListener importListener = new StudentImportListener(studentService);
			inputStream = new BufferedInputStream(file.getInputStream());
			ExcelReaderBuilder builder = EasyExcel.read(inputStream, StudentExcel.class, importListener);
			builder.doReadAll();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return R.success("操作成功");
	}


	/**
	 * 导入代理商用户
	 */
	@PostMapping("import-user-agent")
	@ApiOperationSupport(order = 12)
	@ApiOperation(value = "导入代理商用户", notes = "传入excel")
	public R importUserAgent(MultipartFile file) {
		String filename = file.getOriginalFilename();
		if (StringUtils.isEmpty(filename)) {
			throw new RuntimeException("请上传文件!");
		}
		if ((!StringUtils.endsWithIgnoreCase(filename, ".xls") && !StringUtils.endsWithIgnoreCase(filename, ".xlsx"))) {
			throw new RuntimeException("请上传正确的excel文件!");
		}

		InputStream inputStream;
		try {
			StudentAgentImportListener importListener = new StudentAgentImportListener(studentService);
			inputStream = new BufferedInputStream(file.getInputStream());
			ExcelReaderBuilder builder = EasyExcel.read(inputStream, StudentExcel.class, importListener);
			builder.doReadAll();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return R.success("操作成功");
	}


	/**
	 * 导出模板
	 */
	@SneakyThrows
	@GetMapping("export-template")
	@ApiOperationSupport(order = 14)
	@ApiOperation(value = "导出模板")
	public void exportUser(HttpServletResponse response) {
		List<StudentExcel> list = new ArrayList<>();
		response.setContentType("application/vnd.ms-excel");
		response.setCharacterEncoding(Charsets.UTF_8.name());
		String fileName = URLEncoder.encode("用户数据模板", Charsets.UTF_8.name());
		response.setHeader("Content-disposition", "attachment;filename=" + fileName + ".xlsx");
		EasyExcel.write(response.getOutputStream(), StudentExcel.class).sheet("用户数据表").doWrite(list);
	}


	/**
	 * 导出用户
	 */
	@SneakyThrows
	@GetMapping("export-user")
	@ApiOperationSupport(order = 13)
	@ApiOperation(value = "导出用户", notes = "传入user")
	public void exportUser(@ApiIgnore @RequestParam Map<String, Object> user, HttpServletResponse response) {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
		String date=simpleDateFormat.format(new Date());
		user.remove("crazy-auth");

		response.setContentType("application/vnd.ms-excel");
		response.setCharacterEncoding(Charsets.UTF_8.name());
		String fileName = URLEncoder.encode("用户数据导出"+date, Charsets.UTF_8.name());
		response.setHeader("Content-disposition", "attachment;filename=" + fileName + ".xlsx");
		studentService.exportUser(response , user);

	}

	/**
	 * 修改 学员表
	 */
	@PostMapping("/setPassword")
	@ApiOperationSupport(order = 5)
	@ApiOperation(value = "修改密码", notes = "修改密码")
	public R setPassword(@Valid @RequestBody Student student) {
		student.setPassword(DigestUtil.encrypt(student.getPassword()));
		return R.status(studentService.updateById(student));
	}

	/**
	 * 搜索
	 */
	@GetMapping("/searchParent")
	@ApiOperationSupport(order = 5)
	@ApiOperation(value = "搜索", notes = "搜索")
	public R searchParent(@RequestParam("keyword") String keyword) {
		return R.data(null);
	}
}
