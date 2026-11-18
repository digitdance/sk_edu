
package org.springcrazy.modules.edu.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.AllArgsConstructor;
import org.springcrazy.core.boot.ctrl.CrazyController;
import org.springcrazy.core.mp.support.Condition;
import org.springcrazy.core.mp.support.Query;
import org.springcrazy.core.tool.api.R;
import org.springcrazy.core.tool.utils.Func;
import org.springcrazy.core.tool.utils.StringUtil;
import org.springcrazy.modules.edu.entity.CoursePackage;
import org.springcrazy.modules.edu.entity.CoursePackageExam;
import org.springcrazy.modules.edu.entity.TrxorderDetail;
import org.springcrazy.modules.edu.service.ICoursePackageExamService;
import org.springcrazy.modules.edu.service.ICoursePackageService;
import org.springcrazy.modules.edu.service.ICourseService;
import org.springcrazy.modules.edu.service.ITrxorderDetailService;
import org.springcrazy.modules.edu.vo.CoursePackageExamVO;
import org.springcrazy.modules.edu.vo.CoursePackageVO;
import org.springcrazy.modules.exam.entity.Exampaper;
import org.springcrazy.modules.exam.entity.ExampaperRecord;
import org.springcrazy.modules.exam.service.IExampaperRecordService;
import org.springcrazy.modules.exam.service.IExampaperService;
import org.springcrazy.modules.exam.vo.ExampaperRecordVO;
import org.springcrazy.modules.exam.vo.ExampaperVO;
import org.springcrazy.modules.exam.wrapper.ExampaperWrapper;
import org.springcrazy.modules.user.entity.Student;
import org.springcrazy.modules.user.service.IStudentService;
import org.springcrazy.modules.user.vo.StudentVO;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 *  控制器
 *
 * @author TongZhou
 * @since 2020-05-05
 */
@RestController
@AllArgsConstructor
@RequestMapping("/edu/coursepackageExam")
@Api(value = "班级考试", tags = "班级考试")
public class CoursePackageExamController extends CrazyController {

	private ICoursePackageExamService coursePackageExamService;
	private ICoursePackageService coursePackageService;
	private IExampaperService exampaperService;

	private ITrxorderDetailService trxorderDetailService;
	private IStudentService studentService;
	private ICourseService courseService;
	private IExampaperRecordService exampaperRecordService;
	/**
	 * 详情
	 */
	@GetMapping("/detail")
	@ApiOperationSupport(order = 1)
	@ApiOperation(value = "详情", notes = "传入coursePackageExam")
	public R<CoursePackageExam> detail(CoursePackageExam coursePackageExam) {
		CoursePackageExam detail = coursePackageExamService.getOne(Condition.getQueryWrapper(coursePackageExam));
		return R.data(detail);
	}

	/**
	 * 分页
	 */
	@GetMapping("/list")
	@ApiOperationSupport(order = 2)
	@ApiOperation(value = "分页", notes = "传入coursePackage")
	public R<IPage<CoursePackageExam>> list(CoursePackageExam coursePackage, Query query) {
		IPage<CoursePackageExam> pages = coursePackageExamService.page(Condition.getPage(query), Condition.getQueryWrapper(coursePackage));
		return R.data(pages);
	}

	/**
	 * 自定义分页
	 */
	@GetMapping("/page")
	@ApiOperationSupport(order = 3)
	@ApiOperation(value = "分页", notes = "传入coursePackage")
	public R<IPage<CoursePackageExamVO>> page(CoursePackageExamVO coursePackageExam, Query query) {
		IPage<CoursePackageExamVO> pages = coursePackageExamService.selectCoursePackageExamPage(Condition.getPage(query), coursePackageExam);
		return R.data(pages);
	}

//	@GetMapping("/getStudents")
//	@ApiOperationSupport(order = 3)
//	@ApiOperation(value = "分页", notes = "传入coursePackage")
//	public R<IPage<CoursePackageExamVO>> getStudents(CoursePackageExamVO coursePackageExam, Query query) {
//
//		query.setSize(10000);
//		Student student = new Student();
//		student.setCourseId(coursePackageExam.getCourseId());
//		IPage<CoursePackageExamVO> pages = studentService.selectStudentInClazzPage(Condition.getPage(query), coursePackageExam);
//		return R.data(pages);
//	}

	@GetMapping("/getStudentScores")
	@ApiOperationSupport(order = 3)
	@ApiOperation(value = "分页", notes = "传入coursePackage")
	public R getStudentScores(CoursePackageExamVO coursePackageExam, Query query) {


		Map<String,Object> res =new HashMap<String,Object>();
		query.setSize(10000);
		StudentVO student = new StudentVO();
		student.setCourseId(coursePackageExam.getCourseId());
		List<StudentVO> pages = studentService.selectCourseStudent( student);
		List<String> stus =  pages.stream().map(i->i.getId()+"").collect(Collectors.toList());
		res.put("students",pages);
		//随堂测验
	   List<CoursePackageExam> coursePackageExams = coursePackageExamService.list(new LambdaQueryWrapper<CoursePackageExam>()
				.eq(CoursePackageExam::getCourseId,coursePackageExam.getCourseId())
			   .eq(CoursePackageExam::getExamType,4));
		List<String> stExams = 	coursePackageExams.stream().map(item-> item.getExampaperId()+"").collect(Collectors.toList());
		res.put("stExams",coursePackageExams);
		//随堂测验所有成绩
		ExampaperRecordVO recordVO = new ExampaperRecordVO();
		recordVO.setExamType(4);
		recordVO.setCourseId(coursePackageExam.getCourseId());
		List<ExampaperRecord> recordsstcy = exampaperRecordService.selectExampaperRecords(recordVO);
		Map<String, BigDecimal> stcyMap = new HashMap<String,BigDecimal>();
		recordsstcy.stream().map(t->{
			stcyMap.put(t.getUserId()+"",t.getUserScore());
			return t;
		}).collect(Collectors.toList());

		res.put("stMap",stcyMap);

		//实操
		List<CoursePackageExam> coursePackageExamsc = coursePackageExamService.list(new LambdaQueryWrapper<CoursePackageExam>()
				.eq(CoursePackageExam::getCourseId,coursePackageExam.getCourseId())
				.eq(CoursePackageExam::getExamType,3));
		List<String> scExams = 	coursePackageExamsc.stream().map(item-> item.getExampaperId()+"").collect(Collectors.toList());
		res.put("scExams",coursePackageExamsc);
		//sx实操所有成绩
		ExampaperRecordVO sxrecordVO = new ExampaperRecordVO();
		sxrecordVO.setExamType(3);
		sxrecordVO.setCourseId(coursePackageExam.getCourseId());
		List<ExampaperRecord> recordssc = exampaperRecordService.selectExampaperRecords(sxrecordVO);
		Map<String, BigDecimal> scMap = new HashMap<String,BigDecimal>();
		recordssc.stream().map(t->{
			scMap.put(t.getUserId()+"",t.getUserScore());
			return t;
		}).collect(Collectors.toList());
		res.put("scMap",scMap);
		//实操
		List<CoursePackageExam> coursePackageExamzh = coursePackageExamService.list(
				new LambdaQueryWrapper<CoursePackageExam>()
				.eq(CoursePackageExam::getCourseId,coursePackageExam.getCourseId())
				.eq(CoursePackageExam::getExamType,2));
		List<String> zhExams = 	coursePackageExamzh.stream().map(item-> item.getExampaperId()+"").collect(Collectors.toList());
		res.put("zhExams",zhExams);
		//综合所有成绩
		ExampaperRecordVO zhrecordVO = new ExampaperRecordVO();
		zhrecordVO.setExamType(2);
		zhrecordVO.setCourseId(coursePackageExam.getCourseId());
		List<ExampaperRecord> recordszh = exampaperRecordService.selectExampaperRecords(zhrecordVO);
		Map<String, BigDecimal> zhMap = new HashMap<String,BigDecimal>();
		recordszh.stream().map(t->{
			zhMap.put(t.getUserId()+"",t.getUserScore());
			return t;
		}).collect(Collectors.toList());
		res.put("zhMap",zhMap);


//		pages.getRecords().stream().map()
//
//		query.setSize(10000);
//		Student student = new Student();
//		student.setCourseId(coursePackageExam.getCourseId());
//		IPage<CoursePackageExamVO> pages = studentService.selectStudentInClazzPage(Condition.getPage(query), coursePackageExam);
		return R.data(res);
	}

//
	@GetMapping("/examPage")
	@ApiOperationSupport(order = 3)
	@ApiOperation(value = "分页", notes = "传入coursePackage")
	public R<List<CoursePackageExamVO>> coursePage(CoursePackageExamVO coursePackageExam) {

		List<TrxorderDetail> trxorderDetails =  trxorderDetailService.list(new LambdaQueryWrapper<TrxorderDetail>()
				.eq(TrxorderDetail::getCourseId,coursePackageExam.getClazzId())
				.eq(TrxorderDetail::getTrxorderType,"PACKAGE")
				.gt(TrxorderDetail::getAuthTime,new Date()));
		List<Integer> userIds = trxorderDetails.stream().map(item->item.getUserId()).collect(Collectors.toList());
		if(userIds.size()>0){
			String uids =  Func.join(userIds,",");
			coursePackageExam.setUserIds(uids);
		}

		List<CoursePackageExamVO> pages = coursePackageExamService.selectClazzCoursePackageExamList(coursePackageExam);
		return R.data(pages);
	}


	@GetMapping("/clazzExamlist")
	@ApiOperationSupport(order = 2)
	@ApiOperation(value = "分页", notes = "传入exampaper")
	public R<IPage<ExampaperVO>> list(Exampaper exampaper, Query query, String sort) {
		QueryWrapper<CoursePackage> queryWrapper3 = new QueryWrapper<CoursePackage>();
		queryWrapper3.lambda().eq(CoursePackage::getParentCourseId,exampaper.getClazzId());
		List<CoursePackage> coursePackages2 =  coursePackageService.list(queryWrapper3);
		List<Integer> courseIds =  coursePackages2.stream().map(i->i.getCourseId()).collect(Collectors.toList());

		ExampaperVO exampaperVO = new ExampaperVO();
		exampaperVO.setCourseIds(StringUtil.join(courseIds,","));
		IPage<ExampaperVO> list = exampaperService.selectCourseExamList(Condition.getPage(query),exampaperVO);
		return R.data(list);
	}

//
//
//	@GetMapping("/syncStudentsCourse")
//	@ApiOperationSupport(order = 3)
//	@ApiOperation(value = "分页", notes = "传入coursePackage")
//	public R syncStudentsCourse(CoursePackageVO coursePackage) {
//
//		List<TrxorderDetail> trxorderDetails =  trxorderDetailService.list(new LambdaQueryWrapper<TrxorderDetail>()
//				.eq(TrxorderDetail::getCourseId,coursePackage.getParentCourseId())
//				.eq(TrxorderDetail::getTrxorderType,"PACKAGE")
//				.gt(TrxorderDetail::getAuthTime,new Date()));
//		List<Integer> userIds = trxorderDetails.stream().map(item->item.getUserId()).collect(Collectors.toList());
//		if(userIds.size()>0){
//			String uids =  Func.join(userIds,",");
//			String cids =  coursePackage.getCourseId()+"";
//			courseService.giveClazzCourse(coursePackage.getParentCourseId(),uids,cids);
//		}
//
//		return R.data("操作成功");
//	}
//

	/**
	 * 新增
	 */
	@PostMapping("/save")
	@ApiOperationSupport(order = 4)
	@ApiOperation(value = "新增课程列表", notes = "传入coursePackage")
	public R save(@Valid @RequestBody List<CoursePackageExam> coursePackageExams,String courseId) {
		QueryWrapper<CoursePackageExam> coursePackageQueryWrapper = new QueryWrapper<>();
		coursePackageQueryWrapper.lambda().eq(Func.isNotBlank(courseId),CoursePackageExam::getClazzId,courseId);
		List<CoursePackageExam> list = coursePackageExamService.list(coursePackageQueryWrapper);
		List<Integer> ids = list.stream().map(coursePackage -> coursePackage.getExampaperId()).collect(Collectors.toList());
		//筛选推荐课程下的未被添加的课程
		//List<CoursePackageExam> addList = coursePackageExams.stream().filter(detail -> !ids.contains(detail.getExampaperId())).collect(Collectors.toList());
		//List<CoursePackageExam> addList = coursePackageExams.stream().filter(detail -> !ids.contains(detail.getExampaperId())).collect(Collectors.toList());
		if(coursePackageExams.size() > 0){
			coursePackageExams.stream().map(item->{
				Integer count =  coursePackageExamService.count(new LambdaQueryWrapper<CoursePackageExam>()
						.eq(CoursePackageExam::getCourseId,courseId)
						.eq(CoursePackageExam::getExampaperId,item.getExampaperId()));
				if(count==0){
					coursePackageExamService.save(item);
				}
				return item;
			}).collect(Collectors.toList());

			return R.status(true);
		}else{
			return R.fail("考试已添加");
		}

	}


	@PostMapping("/setKpointExam")
	@ApiOperationSupport(order = 4)
	@ApiOperation(value = "复制考试内容", notes = "传入coursePackage")
	public R setKpointExam(@RequestBody CoursePackageExam source) {

		coursePackageExamService.remove(new LambdaQueryWrapper<CoursePackageExam>()
				.eq(CoursePackageExam::getKpointId,source.getKpointId())
				.eq(CoursePackageExam::getExampaperId,source.getExampaperId()));

		coursePackageExamService.save(source);
		return R.status(true);

	}


	/**
	 * 修改
	 */
	@PostMapping("/update")
	@ApiOperationSupport(order = 5)
	@ApiOperation(value = "修改", notes = "传入coursePackage")
	public R update(@Valid @RequestBody CoursePackageExam coursePackage) {
		return R.status(coursePackageExamService.updateById(coursePackage));
	}

//	@PostMapping("/end")
//	@ApiOperationSupport(order = 5)
//	@ApiOperation(value = "修改", notes = "传入coursePackage")
//	public R end(@Valid @RequestBody CoursePackage coursePackage) {
//
//		CoursePackage coursePackage1 =  coursePackageService.getById(coursePackage.getId());
//		coursePackage1.setCompletionStatus(coursePackage.getCompletionStatus());
//		coursePackage1.setCompletionTime(LocalDateTime.now());
//		coursePackageService.updateById(coursePackage);
//
//		List<TrxorderDetail> clazzOrders =  trxorderDetailService.list(new LambdaQueryWrapper<TrxorderDetail>()
//				.eq(TrxorderDetail::getTrxorderType,"PACKAGE")
//				.eq(TrxorderDetail::getCourseId,coursePackage1.getParentCourseId()));
//
//		List<Integer> uids =  clazzOrders.stream().map(item->{
//			return  item.getUserId();
//		}).collect(Collectors.toList());
//
//		TrxorderDetail trxorderDetail = new TrxorderDetail();
//		trxorderDetail.setAuthTime(new Date());
//		trxorderDetailService.update(trxorderDetail,new LambdaQueryWrapper<TrxorderDetail>()
//				.eq(TrxorderDetail::getTrxorderType,"COURSE")
//				.eq(TrxorderDetail::getCourseId,coursePackage1.getCourseId())
//				.in(TrxorderDetail::getUserId,uids));
//
//		return R.data("操作成功");
//	}


	@PostMapping("/updateStatus")
	@ApiOperationSupport(order = 5)
	@ApiOperation(value = "修改", notes = "传入coursePackage")
	public R updateStatus(@Valid @RequestBody CoursePackageExam coursePackageExam) {
		CoursePackageExam coursePackage1 =  coursePackageExamService.getById(coursePackageExam.getId());
		coursePackage1.setStatus(coursePackageExam.getStatus());
		return R.status(coursePackageExamService.updateById(coursePackage1));
	}

	/**
	 * 新增或修改
	 */
	@PostMapping("/submit")
	@ApiOperationSupport(order = 6)
	@ApiOperation(value = "新增或修改", notes = "传入coursePackage")
	public R submit(@Valid @RequestBody CoursePackageExam coursePackage) {
		return R.status(coursePackageExamService.saveOrUpdate(coursePackage));
	}


	/**
	 * 删除
	 */
	@PostMapping("/remove")
	@ApiOperationSupport(order = 8)
	@ApiOperation(value = "删除", notes = "传入ids")
	public R remove(@ApiParam(value = "主键集合", required = true) @RequestParam String ids) {
		return R.status(coursePackageExamService.removeByIds(Func.toIntList(ids)));
	}


}
