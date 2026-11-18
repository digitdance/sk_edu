
package org.springcrazy.modules.edu.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import lombok.AllArgsConstructor;
import javax.validation.Valid;

import org.springcrazy.common.tool.StringUtils;
import org.springcrazy.core.mp.support.Condition;
import org.springcrazy.core.mp.support.Query;
import org.springcrazy.core.tool.api.R;
import org.springcrazy.core.tool.utils.Func;
import org.springcrazy.modules.edu.entity.TrxorderDetail;
import org.springcrazy.modules.edu.service.ICourseService;
import org.springcrazy.modules.edu.service.ITrxorderDetailService;
import org.springcrazy.modules.user.service.IStudentService;
import org.springcrazy.modules.web.entity.WebsiteRecommendDetail;
import org.springframework.web.bind.annotation.*;
import org.springcrazy.modules.edu.entity.CoursePackage;
import org.springcrazy.modules.edu.vo.CoursePackageVO;
import org.springcrazy.modules.edu.service.ICoursePackageService;
import org.springcrazy.core.boot.ctrl.CrazyController;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 *  控制器
 *
 * @author TongZhou
 * @since 2020-05-05
 */
@RestController
@AllArgsConstructor
@RequestMapping("/edu/coursepackage")
@Api(value = "课程套餐", tags = "课程套餐")
public class CoursePackageController extends CrazyController {

	private ICoursePackageService coursePackageService;
	private ITrxorderDetailService trxorderDetailService;
	private IStudentService studentService;
	private ICourseService courseService;
	/**
	 * 详情
	 */
	@GetMapping("/detail")
	@ApiOperationSupport(order = 1)
	@ApiOperation(value = "详情", notes = "传入coursePackage")
	public R<CoursePackage> detail(CoursePackage coursePackage) {
		CoursePackage detail = coursePackageService.getOne(Condition.getQueryWrapper(coursePackage));
		return R.data(detail);
	}

	/**
	 * 分页
	 */
	@GetMapping("/list")
	@ApiOperationSupport(order = 2)
	@ApiOperation(value = "分页", notes = "传入coursePackage")
	public R<IPage<CoursePackage>> list(CoursePackage coursePackage, Query query) {
		IPage<CoursePackage> pages = coursePackageService.page(Condition.getPage(query), Condition.getQueryWrapper(coursePackage));
		return R.data(pages);
	}

	/**
	 * 自定义分页
	 */
	@GetMapping("/page")
	@ApiOperationSupport(order = 3)
	@ApiOperation(value = "分页", notes = "传入coursePackage")
	public R<IPage<CoursePackageVO>> page(CoursePackageVO coursePackage, Query query) {

		IPage<CoursePackageVO> pages = coursePackageService.selectCoursePackagePage(Condition.getPage(query), coursePackage);
		return R.data(pages);
	}


	@GetMapping("/coursePage")
	@ApiOperationSupport(order = 3)
	@ApiOperation(value = "分页", notes = "传入coursePackage")
	public R<List<CoursePackageVO>> coursePage(CoursePackageVO coursePackage) {

		List<TrxorderDetail> trxorderDetails =  trxorderDetailService.list(new LambdaQueryWrapper<TrxorderDetail>()
				.eq(TrxorderDetail::getCourseId,coursePackage.getParentCourseId())
				.eq(TrxorderDetail::getTrxorderType,"PACKAGE")
				.gt(TrxorderDetail::getAuthTime,new Date()));

		List<Integer> userIds = trxorderDetails.stream().map(item->item.getUserId()).collect(Collectors.toList());
		coursePackage.setUserIds(Func.join(userIds,","));
		List<CoursePackageVO> pages = coursePackageService.selectClazzCoursePackageList(coursePackage);

		return R.data(pages);
	}


	@GetMapping("/syncStudentsCourse")
	@ApiOperationSupport(order = 3)
	@ApiOperation(value = "分页", notes = "传入coursePackage")
	public R syncStudentsCourse(CoursePackageVO coursePackage) {

		List<TrxorderDetail> trxorderDetails =  trxorderDetailService.list(new LambdaQueryWrapper<TrxorderDetail>()
				.eq(TrxorderDetail::getCourseId,coursePackage.getParentCourseId())
				.eq(TrxorderDetail::getTrxorderType,"PACKAGE")
				.gt(TrxorderDetail::getAuthTime,new Date()));
		List<Integer> userIds = trxorderDetails.stream().map(item->item.getUserId()).collect(Collectors.toList());
		if(userIds.size()>0){
			String uids =  Func.join(userIds,",");
			String cids =  coursePackage.getCourseId()+"";
			courseService.giveClazzCourse(coursePackage.getParentCourseId(),uids,cids);
		}

		return R.data("操作成功");
	}


	/**
	 * 新增
	 */
	@PostMapping("/save")
	@ApiOperationSupport(order = 4)
	@ApiOperation(value = "新增课程列表", notes = "传入coursePackage")
	public R save(@Valid @RequestBody List<CoursePackage> coursePackages,String courseId) {
		QueryWrapper<CoursePackage> coursePackageQueryWrapper = new QueryWrapper<>();
		coursePackageQueryWrapper.lambda().eq(Func.isNotBlank(courseId),CoursePackage::getParentCourseId,courseId);
		List<CoursePackage> list = coursePackageService.list(coursePackageQueryWrapper);
		List<Integer> ids = list.stream().map(coursePackage -> coursePackage.getCourseId()).collect(Collectors.toList());
		//筛选推荐课程下的未被添加的课程
		List<CoursePackage> addList = coursePackages.stream().filter(detail -> !ids.contains(detail.getCourseId())).collect(Collectors.toList());
		if(addList.size() > 0){
			coursePackageService.saveBatch(coursePackages);
			return R.status(true);
		}else{
			return R.fail("课程已添加");
		}

	}

	/**
	 * 修改
	 */
	@PostMapping("/update")
	@ApiOperationSupport(order = 5)
	@ApiOperation(value = "修改", notes = "传入coursePackage")
	public R update(@Valid @RequestBody CoursePackage coursePackage) {
		return R.status(coursePackageService.updateById(coursePackage));
	}

	@PostMapping("/end")
	@ApiOperationSupport(order = 5)
	@ApiOperation(value = "修改", notes = "传入coursePackage")
	public R end(@Valid @RequestBody CoursePackage coursePackage) {

		CoursePackage coursePackage1 =  coursePackageService.getById(coursePackage.getId());
		coursePackage1.setCompletionStatus(coursePackage.getCompletionStatus());
		coursePackage1.setCompletionTime(LocalDateTime.now());
		coursePackageService.updateById(coursePackage);

		List<TrxorderDetail> clazzOrders =  trxorderDetailService.list(new LambdaQueryWrapper<TrxorderDetail>()
				.eq(TrxorderDetail::getTrxorderType,"PACKAGE")
				.eq(TrxorderDetail::getCourseId,coursePackage1.getParentCourseId()));

		List<Integer> uids =  clazzOrders.stream().map(item->{
			return  item.getUserId();
		}).collect(Collectors.toList());

		TrxorderDetail trxorderDetail = new TrxorderDetail();
		trxorderDetail.setAuthTime(new Date());
		trxorderDetailService.update(trxorderDetail,new LambdaQueryWrapper<TrxorderDetail>()
				.eq(TrxorderDetail::getTrxorderType,"COURSE")
				.eq(TrxorderDetail::getCourseId,coursePackage1.getCourseId())
				.in(TrxorderDetail::getUserId,uids));

		return R.data("操作成功");
	}


	@PostMapping("/updateStatus")
	@ApiOperationSupport(order = 5)
	@ApiOperation(value = "修改", notes = "传入coursePackage")
	public R updateStatus(@Valid @RequestBody CoursePackage coursePackage) {
		CoursePackage coursePackage1 =  coursePackageService.getById(coursePackage.getId());
		coursePackage1.setCompletionStatus(coursePackage.getCompletionStatus());
		return R.status(coursePackageService.updateById(coursePackage1));
	}

	/**
	 * 新增或修改
	 */
	@PostMapping("/submit")
	@ApiOperationSupport(order = 6)
	@ApiOperation(value = "新增或修改", notes = "传入coursePackage")
	public R submit(@Valid @RequestBody CoursePackage coursePackage) {
		return R.status(coursePackageService.saveOrUpdate(coursePackage));
	}


	/**
	 * 删除
	 */
	@PostMapping("/remove")
	@ApiOperationSupport(order = 8)
	@ApiOperation(value = "删除", notes = "传入ids")
	public R remove(@ApiParam(value = "主键集合", required = true) @RequestParam String ids) {
		return R.status(coursePackageService.removeByIds(Func.toIntList(ids)));
	}


}
