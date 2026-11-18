
package org.springcrazy.modules.web.controller;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import lombok.AllArgsConstructor;
import javax.validation.Valid;

import org.springcrazy.core.mp.support.Condition;
import org.springcrazy.core.mp.support.Query;
import org.springcrazy.core.tool.api.R;
import org.springcrazy.core.tool.jackson.JsonUtil;
import org.springcrazy.core.tool.utils.Func;
import org.springcrazy.modules.edu.dto.CourseDTO;
import org.springcrazy.modules.edu.entity.Course;
import org.springcrazy.modules.edu.service.ICourseService;
import org.springcrazy.modules.edu.wrapper.CourseWrapper;
import org.springcrazy.modules.system.dto.DeptDTO;
import org.springcrazy.modules.system.entity.Dept;
import org.springcrazy.modules.system.mapper.DeptMapper;
import org.springcrazy.modules.system.service.IDeptService;
import org.springcrazy.modules.system.wrapper.DeptWrapper;
import org.springframework.web.bind.annotation.*;
import org.springcrazy.modules.web.entity.StatData;
import org.springcrazy.modules.web.vo.StatDataVO;
import org.springcrazy.modules.web.service.IStatDataService;
import org.springcrazy.core.boot.ctrl.CrazyController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *  控制器
 *
 * @author TongZhou
 * @since 2020-05-19
 */
@RestController
@AllArgsConstructor
@RequestMapping("/web/statdata")
@Api(value = "站点统计信息", tags = "站点统计信息")
public class StatDataController extends CrazyController {

	private IStatDataService statDataService;
	private ICourseService courseService;
	private IDeptService deptService;
	/**
	 * 详情
	 */
	@GetMapping("/detail")
	@ApiOperationSupport(order = 1)
	@ApiOperation(value = "详情", notes = "传入statData")
	public R<StatData> detail(StatData statData) {
		StatData detail = statDataService.getOne(Condition.getQueryWrapper(statData));

		if (detail.getCode().equals("statadminuserdata")){
			Map<String , Object> map = JsonUtil.toMap(detail.getData());

			List<String> loginDataNum = (List<String>) map.get("loginDataList");
			List<String> data = (List<String>) map.get("data");
			List<String> viewDataNum = (List<String>) map.get("viewDataList");

			List<String> logindataNew = new ArrayList<>();
			List<String> dataNew = new ArrayList<>();
			List<String> viewdataNew = new ArrayList<>();
			for (int i = 7 ; i > 0 ; i--){
				logindataNew.add(loginDataNum.get(loginDataNum.size()-i));
				dataNew.add(data.get(data.size()-i));
				viewdataNew.add(viewDataNum.get(viewDataNum.size()-i));
			}

			Map<String , Object> mapNew = new HashMap<>();
			mapNew.put("loginDataList",logindataNew);
			mapNew.put("data",dataNew);
			mapNew.put("viewDataList",viewdataNew);

			detail.setData(JSONUtil.toJsonStr(mapNew));
		}
		return R.data(detail);
	}

	/**
	 * 统计图详情
	 */
	@GetMapping("/user/statistics")
	@ApiOperationSupport(order = 1)
	@ApiOperation(value = "详情", notes = "传入statData")
	public R<StatData> userDetail(StatData statData) {
		StatData detail = statDataService.getOne(Condition.getQueryWrapper(statData));
		return R.data(detail);
	}

	/**
	 * 分页
	 */
	@GetMapping("/list")
	@ApiOperationSupport(order = 2)
	@ApiOperation(value = "分页", notes = "传入statData")
	public R<IPage<StatData>> list(StatData statData, Query query) {
		IPage<StatData> pages = statDataService.page(Condition.getPage(query), Condition.getQueryWrapper(statData));
		return R.data(pages);
	}

	/**
	 * 自定义分页
	 */
	@GetMapping("/page")
	@ApiOperationSupport(order = 3)
	@ApiOperation(value = "分页", notes = "传入statData")
	public R<IPage<StatDataVO>> page(StatDataVO statData, Query query) {
		IPage<StatDataVO> pages = statDataService.selectStatDataPage(Condition.getPage(query), statData);
		return R.data(pages);
	}

	/**
	 * 新增
	 */
	@PostMapping("/save")
	@ApiOperationSupport(order = 4)
	@ApiOperation(value = "新增", notes = "传入statData")
	public R save(@Valid @RequestBody StatData statData) {
		return R.status(statDataService.save(statData));
	}

	/**
	 * 修改
	 */
	@PostMapping("/update")
	@ApiOperationSupport(order = 5)
	@ApiOperation(value = "修改", notes = "传入statData")
	public R update(@Valid @RequestBody StatData statData) {
		return R.status(statDataService.updateById(statData));
	}

	/**
	 * 新增或修改
	 */
	@PostMapping("/submit")
	@ApiOperationSupport(order = 6)
	@ApiOperation(value = "新增或修改", notes = "传入statData")
	public R submit(@Valid @RequestBody StatData statData) {
		return R.status(statDataService.saveOrUpdate(statData));
	}


	/**
	 * 删除
	 */
	@PostMapping("/remove")
	@ApiOperationSupport(order = 8)
	@ApiOperation(value = "删除", notes = "传入ids")
	public R remove(@ApiParam(value = "主键集合", required = true) @RequestParam String ids) {
		return R.status(statDataService.removeByIds(Func.toIntList(ids)));
	}

	/**
	 * 学校下拉
	 */
	@GetMapping("/schools")
	@ApiOperationSupport(order = 4)
	@ApiOperation(value = "新增", notes = "传入statData")
	public R schools(DeptDTO dept) {
		QueryWrapper<Dept> queryWrapper = new QueryWrapper<Dept>();
		queryWrapper.lambda()
				.eq(Dept::getType, "1")
				.orderByAsc(Dept::getSort);

		List<Dept> list = deptService.list(queryWrapper);
		return R.data(DeptWrapper.build().listVO(list));

	}


	/**
	 * 班级下拉
	 */
	@GetMapping("/classes")
	@ApiOperationSupport(order = 4)
	@ApiOperation(value = "新增", notes = "传入statData")
	public R classes(CourseDTO course) {

		QueryWrapper<Course> queryWrapper = new QueryWrapper<>();
		queryWrapper.lambda().eq(StringUtils.isNotBlank(course.getGrade()),Course::getGrade, course.getGrade())
				.eq(course.getSchoolId()!=null,Course::getSchoolId, course.getSchoolId())
				.eq(Course::getSellType, "PACKAGE")
				.orderByAsc(Course::getSort);

		List<Course> list = courseService.list(queryWrapper);
		return R.data(CourseWrapper.build().listVO(list));

	}
}
