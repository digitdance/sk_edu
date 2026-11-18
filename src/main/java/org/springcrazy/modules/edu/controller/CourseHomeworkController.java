
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
import org.springcrazy.modules.edu.entity.CourseHomework;
import org.springcrazy.modules.edu.entity.CourseMaterial;
import org.springcrazy.modules.edu.service.ICourseHomeworkService;
import org.springcrazy.modules.edu.service.ICourseMaterialService;
import org.springcrazy.modules.edu.vo.CourseHomeworkVO;
import org.springcrazy.modules.edu.vo.CourseMaterialVO;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.File;
import java.io.IOException;
import java.util.Map;

/**
 * 章节资料信息 控制器
 *
 * @author TongZhou
 * @since 2020-10-14
 */
@RestController
@AllArgsConstructor
@RequestMapping("/edu/coursehomework")
@Api(value = "章节作业信息", tags = "章节作业接口")
public class CourseHomeworkController extends CrazyController {

	private ICourseHomeworkService courseHomeworkService;
	private CrazyProperties crazyProperties;

	/**
	 * 详情
	 */
	@GetMapping("/detail")
	@ApiOperationSupport(order = 1)
	@ApiOperation(value = "详情", notes = "传入courseMaterial")
	public R<CourseHomework> detail(CourseHomework courseMaterial) {
		CourseHomework detail = courseHomeworkService.getOne(Condition.getQueryWrapper(courseMaterial));
		return R.data(detail);
	}

	/**
	 * 分页 章节资料信息
	 */
	@GetMapping("/list")
	@ApiOperationSupport(order = 2)
	@ApiOperation(value = "分页", notes = "传入courseHomework")
	public R<IPage<CourseHomework>> list(CourseHomework courseHomework, Query query) {
		Map<String,Object> params = Maps.newHashMap();
		params.put("kpointId_like",courseHomework.getKpointId());
		params.put("name_like",courseHomework.getName());
		QueryWrapper<CourseHomework> queryWrapper = Condition.getQueryWrapper(params, CourseHomework.class);
		queryWrapper.lambda().like(Func.isNotBlank(courseHomework.getName()),CourseHomework::getName,courseHomework.getName());
		IPage<CourseHomework> pages = courseHomeworkService.page(Condition.getPage(query), queryWrapper.lambda().orderByDesc(CourseHomework::getSort));
		return R.data(pages);
	}

	/**
	 * 自定义分页 章节资料信息
	 */
	@GetMapping("/page")
	@ApiOperationSupport(order = 3)
	@ApiOperation(value = "分页", notes = "传入courseMaterial")
	public R<IPage<CourseHomeworkVO>> page(CourseHomeworkVO courseMaterial, Query query) {
		IPage<CourseHomeworkVO> pages = courseHomeworkService.selectCourseHomeworkPage(Condition.getPage(query), courseMaterial);
		return R.data(pages);
	}

	/**
	 * 新增 章节资料信息
	 */
	@PostMapping("/save")
	@ApiOperationSupport(order = 4)
	@ApiOperation(value = "新增", notes = "传入courseMaterial")
	public R save(@Valid @RequestBody CourseHomework courseMaterial) {
		return R.status(courseHomeworkService.save(courseMaterial));
	}

	/**
	 * 修改 章节资料信息
	 */
	@PostMapping("/update")
	@ApiOperationSupport(order = 5)
	@ApiOperation(value = "修改", notes = "传入courseMaterial")
	public R update(@Valid @RequestBody CourseHomework courseMaterial) {
		return R.status(courseHomeworkService.updateById(courseMaterial));
	}

	/**
	 * 新增或修改 章节资料信息
	 */
	@PostMapping("/submit")
	@ApiOperationSupport(order = 6)
	@ApiOperation(value = "新增或修改", notes = "传入courseMaterial")
	public R submit(@Valid @RequestBody CourseHomework courseMaterial) {
		return R.status(courseHomeworkService.saveOrUpdate(courseMaterial));
	}


	/**
	 * 删除 章节资料信息
	 */
	@PostMapping("/remove")
	@ApiOperationSupport(order = 8)
	@ApiOperation(value = "删除", notes = "传入ids")
	public R remove(@ApiParam(value = "主键集合", required = true) @RequestParam String ids) {
		return R.status(courseHomeworkService.removeByIds(Func.toIntList(ids)));
	}

	/**
	 * 资料上传请求
	 */
	@PostMapping("/fileUpload")
	@ApiOperation(value = "作业资料上传", notes = "资料上传")
	public R uploadIcon(MultipartFile file){

		SystemConstant me = SystemConstant.me();

		String fileName = file.getOriginalFilename();
		String frontPath = crazyProperties.get("front-icon-path");
		CrazyFile crazyFile = getFile(file,"courseMaterial");
		crazyFile.transfer();
		try {
			FileUtil.copy(new File(crazyFile.getUploadPath()),new File(frontPath));
		} catch (IOException e) {
			e.printStackTrace();
		}
		//上传文件
		Map<String,String> result = Maps.newHashMap();
		result.put("url", me.getDomain() + crazyFile.getUploadVirtualPath());
		result.put("fileName" , fileName);
		return R.data(result);
	}

	/**
	 * 资料上传请求
	 */
	@PostMapping("/attachUpload")
	@ApiOperation(value = "作业资料上传", notes = "资料上传")
	public R attachUpload(MultipartFile file){

		SystemConstant me = SystemConstant.me();

		String fileName = file.getOriginalFilename();
		String frontPath = crazyProperties.get("front-icon-path");
		CrazyFile crazyFile = getFile(file,"courseMaterial");
		crazyFile.transfer();
		try {
			FileUtil.copy(new File(crazyFile.getUploadPath()),new File(frontPath));
		} catch (IOException e) {
			e.printStackTrace();
		}
		//上传文件
		Map<String,String> result = Maps.newHashMap();
		result.put("url", me.getDomain() + crazyFile.getUploadVirtualPath());
		result.put("fileName" , fileName);
		return R.data(result);
	}

}
