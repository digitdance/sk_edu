
package org.springcrazy.modules.edu.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.google.common.collect.Maps;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.AllArgsConstructor;
import org.springcrazy.core.boot.ctrl.CrazyController;
import org.springcrazy.core.boot.file.CrazyFile;
import org.springcrazy.core.launch.props.CrazyProperties;
import org.springcrazy.core.mp.support.Condition;
import org.springcrazy.core.mp.support.Query;
import org.springcrazy.core.tool.api.R;
import org.springcrazy.core.tool.constant.SystemConstant;
import org.springcrazy.core.tool.utils.FileUtil;
import org.springcrazy.core.tool.utils.Func;
import org.springcrazy.modules.edu.entity.CourseExam;
import org.springcrazy.modules.edu.entity.CourseHomework;
import org.springcrazy.modules.edu.service.ICourseExamService;
import org.springcrazy.modules.edu.service.ICourseHomeworkService;
import org.springcrazy.modules.edu.vo.CourseHomeworkVO;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * 章节资料信息 控制器
 *
 * @author TongZhou
 * @since 2020-10-14
 */
@RestController
@AllArgsConstructor
@RequestMapping("/edu/courseexam")
@Api(value = "章节测验信息", tags = "章节测验接口")
public class CourseExamController extends CrazyController {

	private ICourseExamService courseExamService;
	private CrazyProperties crazyProperties;

	/**
	 * 详情
	 */
	@GetMapping("/detail")
	@ApiOperationSupport(order = 1)
	@ApiOperation(value = "详情", notes = "传入courseExam")
	public R<CourseExam> detail(CourseExam courseExam) {
		CourseExam detail = courseExamService.getOne(Condition.getQueryWrapper(courseExam));
		return R.data(detail);
	}

	/**
	 * 分页 章节资料信息
	 */
	@GetMapping("/list")
	@ApiOperationSupport(order = 2)
	@ApiOperation(value = "分页", notes = "传入courseHomework")
	public R<IPage<CourseExam>> list(CourseExam courseExam, Query query) {
		Map<String,Object> params = Maps.newHashMap();
		params.put("kpointId_like",courseExam.getKpointId());
		params.put("name_like",courseExam.getName());
		QueryWrapper<CourseExam> queryWrapper = Condition.getQueryWrapper(params, CourseExam.class);
		queryWrapper.lambda().like(Func.isNotBlank(courseExam.getName()),CourseExam::getName,courseExam.getName());
		IPage<CourseExam> pages = courseExamService.page(Condition.getPage(query), queryWrapper.lambda().orderByDesc(CourseExam::getSort));
		return R.data(pages);
	}



	/**
	 * 新增 章节资料信息
	 */
	@PostMapping("/save")
	@ApiOperationSupport(order = 4)
	@ApiOperation(value = "新增", notes = "传入courseExam")
	public R save(@Valid @RequestBody CourseExam courseExam) {
		return R.status(courseExamService.save(courseExam));
	}
	@PostMapping("/saveBatch")
	@ApiOperationSupport(order = 4)
	@ApiOperation(value = "新增", notes = "传入courseExam")
	public R saveBatch(@Valid @RequestBody List<CourseExam> courseExam) {
		return R.status(courseExamService.saveBatch(courseExam));
	}
	/**
	 * 修改 章节资料信息
	 */
	@PostMapping("/update")
	@ApiOperationSupport(order = 5)
	@ApiOperation(value = "修改", notes = "传入courseExam")
	public R update(@Valid @RequestBody CourseExam courseExam) {
		return R.status(courseExamService.updateById(courseExam));
	}

	/**
	 * 新增或修改 章节资料信息
	 */
	@PostMapping("/submit")
	@ApiOperationSupport(order = 6)
	@ApiOperation(value = "新增或修改", notes = "传入courseExam")
	public R submit(@Valid @RequestBody CourseExam courseExam) {
		return R.status(courseExamService.saveOrUpdate(courseExam));
	}


	/**
	 * 删除 章节资料信息
	 */
	@PostMapping("/remove")
	@ApiOperationSupport(order = 8)
	@ApiOperation(value = "删除", notes = "传入ids")
	public R remove(@ApiParam(value = "主键集合", required = true) @RequestParam String ids) {
		return R.status(courseExamService.removeByIds(Func.toIntList(ids)));
	}

	

}
