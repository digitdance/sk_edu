
package org.springcrazy.modules.edu.controller;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.read.builder.ExcelReaderBuilder;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import lombok.AllArgsConstructor;
import javax.validation.Valid;

import org.apache.poi.ss.formula.functions.T;
import org.springcrazy.common.tool.CustomQueryUtils;
import org.springcrazy.core.mp.support.Condition;
import org.springcrazy.core.mp.support.Query;
import org.springcrazy.core.secure.utils.SecureUtil;
import org.springcrazy.core.tool.api.R;
import org.springcrazy.core.tool.utils.BeanUtil;
import org.springcrazy.core.tool.utils.DateUtil;
import org.springcrazy.core.tool.utils.Func;
import org.springcrazy.modules.cms.entity.Article;
import org.springcrazy.modules.desk.entity.Notice;
import org.springcrazy.modules.desk.service.INoticeService;
import org.springcrazy.modules.edu.entity.*;
import org.springcrazy.modules.edu.service.*;
import org.springcrazy.modules.edu.vo.CoursePackageExamVO;
import org.springcrazy.modules.exam.entity.Exampaper;
import org.springcrazy.modules.exam.entity.PaperMiddle;
import org.springcrazy.modules.exam.entity.Qstmiddle;
import org.springcrazy.modules.exam.service.IExampaperService;
import org.springcrazy.modules.exam.service.IPaperMiddleService;
import org.springcrazy.modules.exam.service.IQstmiddleService;
import org.springcrazy.modules.system.excel.GiveCourseExcel;
import org.springcrazy.modules.system.excel.GiveCourseImportListener;
import org.springcrazy.modules.system.excel.StudentExcel;
import org.springcrazy.modules.user.entity.Teacher;
import org.springcrazy.modules.user.service.ITeacherService;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springcrazy.modules.edu.vo.CourseVO;
import org.springcrazy.core.boot.ctrl.CrazyController;
import org.springframework.web.multipart.MultipartFile;
import springfox.documentation.annotations.ApiIgnore;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 课程表 控制器
 *
 * @author TongZhou
 * @since 2020-04-25
 */
@RestController
@AllArgsConstructor
@RequestMapping("/edu/course")
@Api(value = "课程表", tags = "课程表接口")
public class CourseController extends CrazyController {

	private ICourseService courseService;
	private INoticeService noticeService;
	private ICourseKpointService courseKpointService;
	private ICoursePackageService coursePackageService;
	private ITeacherService teacherService;

	private ICourseMaterialService courseMaterialService;
	private IExampaperService exampaperService;
	private IQstmiddleService qstmiddleService;
	private IPaperMiddleService paperMiddleService;

	private ICoursePackageExamService coursePackageExamService;

	/**
	 * 详情
	 */
	@GetMapping("/detail")
	@ApiOperationSupport(order = 1)
	@ApiOperation(value = "详情", notes = "传入course")
	public R<Course> detail(Course course) {
		Course detail = courseService.getOne(Condition.getQueryWrapper(course));
		return R.data(detail);
	}




	/**
	 * 分页 课程表
	 */
	@GetMapping("/list")
	@ApiOperationSupport(order = 2)
	@ApiOperation(value = "分页", notes = "传入course")
	public R<IPage<Course>> list(Course course, Query query) {

		List<Integer> authCourseIds = null;
		Teacher teacher =null;
		if(SecureUtil.getUserRole().equals("teacher")){
			teacher = teacherService.getOne(new LambdaQueryWrapper<Teacher>()
					.eq(Teacher::getUserId,SecureUtil.getUserId()));
			QueryWrapper<CoursePackage> coursePackageQueryWrapper =new QueryWrapper<CoursePackage>();
			String[] ids = new String[] {teacher.getId()+""};
			String findInSetConditions = CustomQueryUtils.buildFindInSetConditions(ids);
			coursePackageQueryWrapper.lambda().eq(CoursePackage::getIsDelete,-1);
			coursePackageQueryWrapper.last(findInSetConditions);
			//在sql最后面添加sql
			List<CoursePackage> coursePackages = 	coursePackageService.list(coursePackageQueryWrapper);
			authCourseIds = coursePackages.stream().map(item->item.getCourseId()).collect(Collectors.toList());

		}

		String courseName = course.getCourseName();
		Integer courseId = course.getId();

		QueryWrapper<Course> queryWrapper = Condition.getQueryWrapper(course);
		final List<Integer> finalAuthCourseIds = authCourseIds;
		final Teacher finalTeacher = teacher;
		queryWrapper.lambda()
				.like(Func.isNotBlank(courseName),Course::getCourseName,courseName)
				.eq(Func.isNotEmpty(courseId),Course::getId,courseId);

		if(!StringUtils.isEmpty(course.getMine())){
			queryWrapper.lambda().eq(Course::getCreater,SecureUtil.getUserId());
		}
		if(!StringUtils.isEmpty(course.getTemplateCourse())){
			queryWrapper.lambda().eq(Course::getIsTemplateCourse,true);
		}
//		if(finalTeacher!=null&&finalAuthCourseIds.size()==0){
//			queryWrapper.lambda().eq(Course::getMainTeacherId,finalTeacher.getId())
//					.or(e->e.eq(Course::getCreater,SecureUtil.getUserId()));
//		}
//		if(finalTeacher!=null&&finalAuthCourseIds.size()>0){
//			queryWrapper.lambda().eq(Course::getMainTeacherId,finalTeacher.getId()).or(e->e.in(finalAuthCourseIds.size()>0,Course::getId,finalAuthCourseIds))
//				.or(e->e.eq(Course::getCreater,SecureUtil.getUserId()));
//			//queryWrapper.lambda().in(Course::getId,authCourseIds.toArray());
//		}

		IPage<Course> pages = courseService.page(Condition.getPage(query), queryWrapper);
		return R.data(pages);
	}


	/**
	 * 分页 文章信息表
	 */
	@GetMapping("/allCourses")
	@ApiOperationSupport(order = 2)
	@ApiOperation(value = "分页", notes = "传入")
	public R<List<Course>> allCourses(@ApiIgnore @RequestParam Map<String, Object> course, Query query) {
		query.setSize(10000);
		IPage<Course> pages = courseService.page(Condition.getPage(query), Condition.getQueryWrapper(course, Course.class).lambda().orderByDesc(Course::getSort));
		pages.getRecords().stream().map(item->{
			item.setValue(item.getId());
			return item;
		}).collect(Collectors.toList());
		return R.data(pages.getRecords());
	}



	@GetMapping("/allMyCourses")
	@ApiOperationSupport(order = 2)
	@ApiOperation(value = "分页", notes = "传入")
	public R<List<Course>> allMyCourses(Course course) {
		List<Integer> authCourseIds = null;
		Teacher teacher =null;
		if(SecureUtil.getUserRole().equals("teacher")){
			teacher = teacherService.getOne(new LambdaQueryWrapper<Teacher>()
					.eq(Teacher::getUserId,SecureUtil.getUserId()));
			QueryWrapper<CoursePackage> coursePackageQueryWrapper =new QueryWrapper<CoursePackage>();
			String[] ids = new String[] {teacher.getId()+""};
			String findInSetConditions = CustomQueryUtils.buildFindInSetConditions(ids);
			coursePackageQueryWrapper.lambda().eq(CoursePackage::getIsDelete,-1);
			coursePackageQueryWrapper.last(findInSetConditions);
			//在sql最后面添加sql
			List<CoursePackage> coursePackages = 	coursePackageService.list(coursePackageQueryWrapper);
			authCourseIds = coursePackages.stream().map(item->item.getCourseId()).collect(Collectors.toList());
		}
		QueryWrapper<Course> queryWrapper = Condition.getQueryWrapper(course);
		//queryWrapper.lambda().eq(Course::getCreater,SecureUtil.getUserId());
		queryWrapper.lambda().like(Course::getTeacherIds,teacher.getId());
		List<Course> courses = courseService.list(queryWrapper);

		return R.data(courses);
	}

	/**
	 * 自定义分页 课程表
	 * 用于查询课程  排除 套餐类型
	 */
	@GetMapping("/page")
	@ApiOperationSupport(order = 3)
	@ApiOperation(value = "分页", notes = "传入course")
	public R<IPage<Course>> page(Course course, Query query) {
		QueryWrapper<Course> queryWrapper = Condition.getQueryWrapper(course);
		queryWrapper.lambda().like(Func.isNotBlank(course.getCourseName()),Course::getCourseName,course.getCourseName());
		course.setCourseName(null);
		queryWrapper.in("sell_type","1","2","5");
		queryWrapper.orderByDesc("sort DESC,create_time");
		IPage<Course> pages = courseService.page(Condition.getPage(query), queryWrapper);
		return R.data(pages);
	}


	/**
	 * 自定义分页 课程表
	 * 用于查询课程  排除 套餐类型
	 */
	@GetMapping("/ClassCoursePage")
	@ApiOperationSupport(order = 3)
	@ApiOperation(value = "分页", notes = "传入course")
	public R<IPage<Course>> ClassCoursePage(Course course, Query query) {
		QueryWrapper<Course> queryWrapper = Condition.getQueryWrapper(course);
		queryWrapper.lambda().like(Func.isNotBlank(course.getCourseName()),Course::getCourseName,course.getCourseName());
		course.setCourseName(null);
		queryWrapper.orderByDesc("sort");
		IPage<Course> pages = courseService.page(Condition.getPage(query), queryWrapper);
		return R.data(pages);
	}

	/**
	 * 新增 课程表
	 */
	@PostMapping("/save")
	@ApiOperationSupport(order = 4)
	@ApiOperation(value = "新增", notes = "传入course")
	public R save(@Valid @RequestBody Course course) {

		//if(course.getSellType().equals("3")){
			if(Func.isNotBlank(course.getPassword())){
				List<Course> courseList  =	courseService.list(new LambdaQueryWrapper<Course>()
						.eq(Course::getPassword,course.getPassword()));
				if (courseList.size()>0){
					return R.fail("授课密码重复，请更换密码");
				}
			}
		//}

		//course.setCreater(SecureUtil.getUserId());
		//Teacher teacher =  teacherService.getOne(new LambdaQueryWrapper<Teacher>().eq(Teacher::getUserId,SecureUtil.getUserId()));
		//course.setTeacherId(teacher.getId());
		return R.status(courseService.save(course));
	}


	@PostMapping("/copy")
	@ApiOperationSupport(order = 4)
	@ApiOperation(value = "新增", notes = "传入course")
	public R copy(@Valid @RequestBody Course param) {

		Course course = courseService.getCourseById(param.getId());
//		if(course.getSellType().equals("3")){
//			if(Func.isNotBlank(course.getPassword())){
//				List<Course> courseList  =	courseService.list(new LambdaQueryWrapper<Course>()
//						.eq(Course::getSellType,"3")
//						.eq(Course::getPassword,course.getPassword()));
//				if (courseList.size()>0){
//					return R.fail("授课密码重复，请更换密码");
//				}
//			}
//		}
		Course clazz = courseService.getCourseById(param.getCopy2ClazzId());
		Course courseNew = new Course();
		BeanUtil.copy(course,courseNew);
		courseNew.setIsTemplateCourse(0);
		courseNew.setCourseName(course.getCourseName()+"-"+clazz.getCourseName());
		courseNew.setCreater(SecureUtil.getUserId());
		Teacher teacher =  teacherService.getOne(new LambdaQueryWrapper<Teacher>().eq(Teacher::getUserId,SecureUtil.getUserId()));
		courseNew.setTeacherId(teacher.getId());
		courseService.save(courseNew);//复制课程






		Map<String,Integer> zhangMap =new HashMap<>();
		List<CourseKpoint> courseKpoints = courseKpointService.list(new LambdaQueryWrapper<CourseKpoint>()
				.eq(CourseKpoint::getCourseId,course.getId()));
		courseKpoints.stream().map(item->{
			Integer kpintiD =item.getId();
			CourseKpoint courseKpointNew = new CourseKpoint();
			BeanUtil.copy(item,courseKpointNew);
			courseKpointNew.setCourseId(courseNew.getId());
			courseKpointService.save(courseKpointNew);//复制章节
			if(item.getParentId()==0){
				zhangMap.put(item.getId()+"",courseKpointNew.getId());
			}
			if(courseKpointNew.getParentId()!=0){
				if(zhangMap.containsKey(courseKpointNew.getParentId()+"-"+clazz.getCourseName())){
					courseKpointNew.setParentId(zhangMap.get(courseKpointNew.getParentId()+""));
					courseKpointService.updateById(courseKpointNew);
				}
			}

			 List<CourseMaterial> courseMaterials = courseMaterialService.list(new LambdaQueryWrapper<CourseMaterial>()
					 .eq(CourseMaterial::getKpointId,kpintiD));
			courseMaterials.stream().map(cm->{
				CourseMaterial cmnew = new CourseMaterial();
				BeanUtil.copy(cm,cmnew);
				cmnew.setCourseId(courseNew.getId());
				cmnew.setKpointId(courseKpointNew.getId());
				cmnew.setClazzId(param.getCopy2ClazzId());
				//cmnew.setClazzId();
				courseMaterialService.save(cmnew);
				return cm;
			}).collect(Collectors.toList());

			List<Exampaper> exampapers =exampaperService.list(new LambdaQueryWrapper<Exampaper>()
					.eq(Exampaper::getKpointId,kpintiD));
			exampapers.stream().map(exampaper->{
				Exampaper exampaperNew = new Exampaper();
				BeanUtil.copy(exampaper,exampaperNew);
				exampaperNew.setKpointId(courseKpointNew.getId());
				exampaperNew.setCourseId(courseNew.getId());
				//exampaperNew.setClazzId(param.getCopy2ClazzId());
				//exampaperNew.setClazzId();
				exampaperService.save(exampaperNew);//复制课堂测验
				//exam_qstmiddle
				List<Qstmiddle> qstmiddles = qstmiddleService.list(new LambdaQueryWrapper<Qstmiddle>()
						.eq(Qstmiddle::getPaperId,exampaper.getId()));
				qstmiddles.stream().map(qstmiddle -> {
					Qstmiddle qstmiddle1 = new Qstmiddle();
					BeanUtil.copy(qstmiddle,qstmiddle1);
					qstmiddle1.setPaperId(exampaperNew.getId());
					qstmiddleService.save(qstmiddle1);
					return qstmiddle;
				}).collect(Collectors.toList());
				//exam_paper_middle
				List<PaperMiddle> paperMiddles = paperMiddleService.list(new LambdaQueryWrapper<PaperMiddle>()
						.eq(PaperMiddle::getPaperId,exampaper.getId()));
				paperMiddles.stream().map(paperMiddle -> {
					PaperMiddle paperMiddle1 = new PaperMiddle();
					BeanUtil.copy(paperMiddle,paperMiddle1);
					paperMiddle1.setPaperId(exampaperNew.getId());
					paperMiddleService.save(paperMiddle1);
					return paperMiddle;
				}).collect(Collectors.toList());
				return exampaper;
			}).collect(Collectors.toList());


			CoursePackageExamVO coursePackageExamVO = new CoursePackageExamVO();
			coursePackageExamVO.setKpointId(kpintiD);
			coursePackageExamVO.setCourseId(course.getId());
			List<CoursePackageExamVO> exams =  coursePackageExamService.selectCoursePackageExamList(coursePackageExamVO);
			exams.stream().map(coursePackageExamVO1->{
				CoursePackageExam coursePackageExamNew = new CoursePackageExam();
				BeanUtil.copy(coursePackageExamVO1,coursePackageExamNew);
				coursePackageExamNew.setCourseId(courseNew.getId());
				coursePackageExamNew.setKpointId(courseKpointNew.getId());
				coursePackageExamNew.setClazzId(param.getCopy2ClazzId());
				coursePackageExamService.save(coursePackageExamNew);
				return coursePackageExamVO1;
			}).collect(Collectors.toList());


			return item;
		}).collect(Collectors.toList());

		//绑定班级
		CoursePackage coursePackage = new CoursePackage();
		coursePackage.setCourseId(courseNew.getId());
		coursePackage.setParentCourseId(param.getCopy2ClazzId());
		coursePackageService.save(coursePackage);

		return R.status(true);
	}

	@PostMapping("/sendNotice")
	@ApiOperationSupport(order = 4)
	@ApiOperation(value = "新增", notes = "传入course")
	public R sendNotice(@Valid @RequestBody Notice notice) {
		//NoticeMapper
		return R.status(noticeService.save(notice));
	}





	/**
	 * 修改 课程表
	 */
	@PostMapping("/update")
	@ApiOperationSupport(order = 5)
	@ApiOperation(value = "修改", notes = "传入course")
	public R update(@Valid @RequestBody Course course) {

		if(course.getSellType().equals("3")){
			if(Func.isNotBlank(course.getPassword())){
				List<Course> courseList  =	courseService.list(new LambdaQueryWrapper<Course>()
						.eq(Course::getPassword,course.getPassword())
						.eq(Course::getSellType,"3")
						.ne(Course::getId,course.getId()));
				if (courseList.size()>0){
					return R.fail("授课密码重复，请更换密码");
				}
			}
		}

		course.setUpdater(SecureUtil.getUserId());
		return R.status(courseService.updateById(course));
	}

	/**
	 * 新增或修改 课程表
	 */
	@PostMapping("/submit")
	@ApiOperationSupport(order = 6)
	@ApiOperation(value = "新增或修改", notes = "传入course")
	public R submit(@Valid @RequestBody Course course) {


		if(Func.isNotBlank(course.getPassword())){
			List<Course> courseList  =	courseService.list(new LambdaQueryWrapper<Course>()
					.eq(Course::getPassword,course.getPassword())
					.ne(Course::getId,course.getId()));
			if (courseList.size()>0){
				return R.fail("授课密码重复，请更换密码");
			}
		}
		//course.setCreater(SecureUtil.getUserId());
		return R.status(courseService.saveOrUpdate(course));
	}


	/**
	 * 删除 课程表
	 */
	@PostMapping("/remove")
	@ApiOperationSupport(order = 8)
	@ApiOperation(value = "删除", notes = "传入ids")
	public R remove(@ApiParam(value = "主键集合", required = true) @RequestParam String ids) {
		return R.status(courseService.removeByIds(Func.toIntList(ids)));
	}

	/**
	 * 赠送 课程
	 */
	@PostMapping("/give")
	@ApiOperationSupport(order = 4)
	@ApiOperation(value = "赠送", notes = "传入course")
	public R give(String userIds,String courseIds,@RequestParam(defaultValue = "COURSE") String orderType) {

		courseService.giveCourse(userIds,courseIds);
		return R.status(true);
	}



	/**
	 * 赠送 课程
	 */
	@PostMapping("/openCourse")
	@ApiOperationSupport(order = 4)
	@ApiOperation(value = "开通课程", notes = "传入courseIds和userIds")
	public R openCourse(String userIds,String courseIds,@RequestParam(defaultValue = "COURSE") String orderType) {
		courseService.openCourse(userIds,courseIds);
		return R.status(true);
	}
	/**
	 * 导入用户赠送的课程
	 */
	@PostMapping("import-course-user")
	@ApiOperationSupport(order = 12)
	@ApiOperation(value = "导入用户赠送的课程", notes = "传入excel")
	public R importCourseUser(MultipartFile file) {
		String filename = file.getOriginalFilename();
		if (StringUtils.isEmpty(filename)) {
			throw new RuntimeException("请上传文件!");
		}
		if ((!StringUtils.endsWithIgnoreCase(filename, ".xls") && !StringUtils.endsWithIgnoreCase(filename, ".xlsx"))) {
			throw new RuntimeException("请上传正确的excel文件!");
		}
		InputStream inputStream;
		try {
			GiveCourseImportListener importListener = new GiveCourseImportListener(courseService);
			inputStream = new BufferedInputStream(file.getInputStream());
			ExcelReaderBuilder builder = EasyExcel.read(inputStream, GiveCourseExcel.class, importListener);
			builder.doReadAll();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return R.success("操作成功");
	}

	@PostMapping("/kpoints")
	@ApiOperationSupport(order = 4)
	@ApiOperation(value = "检测课程中是否有章节", notes = "传入courseId")
	public R selectCourseKpoint(Integer courseId) {
		CourseKpoint courseKpoint = new CourseKpoint();
		courseKpoint.setCourseId(courseId);
		List<CourseKpoint> list = courseKpointService.list(Condition.getQueryWrapper(courseKpoint).lambda().orderByDesc(CourseKpoint::getSort));

		//判断是否有章节
		Boolean status = false;
		if (list.size() > 0){
			status = true;
		}
		return R.data(status);
	}
	/**
	 * 所有的课程
	 */
	@GetMapping("/allList")
	@ApiOperationSupport(order = 2)
	@ApiOperation(value = "分页", notes = "传入course")
	public R<List<Course>> allList(Course course, Query query) {
		String courseName = course.getCourseName();
		Integer courseId = course.getId();
		course.setCourseName(null);
		course.setId(null);
		QueryWrapper<Course> queryWrapper = Condition.getQueryWrapper(course);
		queryWrapper.lambda().like(Func.isNotBlank(courseName),Course::getCourseName,courseName)
				.like(Func.isNotEmpty(courseId),Course::getId,courseId);
		queryWrapper.lambda().orderByDesc(Course::getCreateTime).orderByDesc(Course::getSort);
		List<Course> pages = courseService.list(queryWrapper);
		List<Course> courseList = new ArrayList();
		for(Course course1:pages){
			Course course2 = new Course();
			course2.setId(course1.getId());
			course2.setCourseName(course1.getCourseName());
			courseList.add(course2);

		}
		return R.data(courseList);
	}
}
