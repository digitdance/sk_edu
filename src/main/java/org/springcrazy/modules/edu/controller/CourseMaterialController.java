
package org.springcrazy.modules.edu.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.google.common.collect.Maps;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import lombok.AllArgsConstructor;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import lombok.Data;
import org.springcrazy.common.tool.CossUtil;
import org.springcrazy.core.boot.file.CrazyFile;
import org.springcrazy.core.launch.props.CrazyProperties;
import org.springcrazy.core.mp.support.Condition;
import org.springcrazy.core.mp.support.Query;
import org.springcrazy.core.secure.utils.SecureUtil;
import org.springcrazy.core.tool.api.R;
import org.springcrazy.core.tool.constant.SystemConstant;
import org.springcrazy.core.tool.utils.FileUtil;
import org.springcrazy.core.tool.utils.Func;
import org.springcrazy.core.tool.utils.StringUtil;
import org.springcrazy.modules.file.entity.SysOss;
import org.springcrazy.modules.file.service.OssService;
import org.springcrazy.modules.file.vo.SysOssUploadVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springcrazy.modules.edu.entity.CourseMaterial;
import org.springcrazy.modules.edu.vo.CourseMaterialVO;
import org.springcrazy.modules.edu.service.ICourseMaterialService;
import org.springcrazy.core.boot.ctrl.CrazyController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * 章节资料信息 控制器
 *
 * @author TongZhou
 * @since 2020-10-14
 */
@RestController
@AllArgsConstructor
@RequestMapping("/edu/coursematerial")
@Api(value = "章节资料信息", tags = "章节资料信息接口")
public class CourseMaterialController extends CrazyController {

	private ICourseMaterialService courseMaterialService;
	private CrazyProperties crazyProperties;

	/**
	 * 详情
	 */
	@GetMapping("/detail")
	@ApiOperationSupport(order = 1)
	@ApiOperation(value = "详情", notes = "传入courseMaterial")
	public R<CourseMaterial> detail(CourseMaterial courseMaterial) {
		CourseMaterial detail = courseMaterialService.getOne(Condition.getQueryWrapper(courseMaterial));
		return R.data(detail);
	}

	/**
	 * 分页 章节资料信息
	 */
	@GetMapping("/list")
	@ApiOperationSupport(order = 2)
	@ApiOperation(value = "分页", notes = "传入courseMaterial")
	public R<IPage<CourseMaterial>> list(CourseMaterialVO courseMaterial, Query query) {


		Map<String,Object> params = Maps.newHashMap();
		if(courseMaterial.getKpointId()!=null){
			params.put("kpointId_like",courseMaterial.getKpointId());
		}
		if(StringUtil.isNotBlank(courseMaterial.getName())){
			params.put("name_like",courseMaterial.getName());
		}
		QueryWrapper<CourseMaterial> queryWrapper = Condition.getQueryWrapper(params, CourseMaterial.class);
		queryWrapper.lambda().like(Func.isNotBlank(courseMaterial.getName()),CourseMaterial::getName,courseMaterial.getName());
		if(StringUtil.isNotBlank(courseMaterial.getOnlyCourse())){
			queryWrapper.lambda().isNull(CourseMaterial::getKpointId);
			queryWrapper.lambda().eq(CourseMaterial::getCourseId,courseMaterial.getCourseId());
		}

		//queryWrapper.lambda().eq(CourseMaterial::getCreaterId, SecureUtil.getUserId()).or(f->f.eq(CourseMaterial::getIsOpen,1));

		IPage<CourseMaterial> pages = courseMaterialService.page(Condition.getPage(query), queryWrapper.lambda().orderByDesc(CourseMaterial::getCreateTime));
		return R.data(pages);
	}

	/**
	 * 分页 章节资料信息
	 */
	@GetMapping("/myPage")
	@ApiOperationSupport(order = 2)
	@ApiOperation(value = "分页", notes = "传入courseMaterial")
	public R<IPage<CourseMaterial>> myPage(CourseMaterialVO courseMaterial, Query query) {


		Map<String,Object> params = Maps.newHashMap();
		if(courseMaterial.getKpointId()!=null){
			params.put("kpointId_like",courseMaterial.getKpointId());
		}
		if(StringUtil.isNotBlank(courseMaterial.getName())){
			params.put("name_like",courseMaterial.getName());
		}
		QueryWrapper<CourseMaterial> queryWrapper = Condition.getQueryWrapper(params, CourseMaterial.class);

		queryWrapper.lambda()
				.eq(courseMaterial.getCourseId()!=null,CourseMaterial::getCourseId,courseMaterial.getCourseId())
				.eq(courseMaterial.getClazzId()!=null,CourseMaterial::getClazzId,courseMaterial.getClazzId())
				.eq(courseMaterial.getSubjectId()!=null,CourseMaterial::getSubjectId,courseMaterial.getSubjectId())
				.eq(StringUtil.isNotBlank(courseMaterial.getCreaterName()),CourseMaterial::getCreaterName,courseMaterial.getCreaterName());


		queryWrapper.lambda().like(Func.isNotBlank(courseMaterial.getName()),CourseMaterial::getName,courseMaterial.getName());
		if(StringUtil.isNotBlank(courseMaterial.getOnlyCourse())){
			queryWrapper.lambda().isNull(CourseMaterial::getKpointId);
			queryWrapper.lambda().eq(CourseMaterial::getCourseId,courseMaterial.getCourseId());
		}

		if(!SecureUtil.getUserRole().equals("administrator")){
			queryWrapper.lambda().and(qand->
					qand.eq(CourseMaterial::getCreaterId, SecureUtil.getUserId()).or(f->f.eq(CourseMaterial::getIsOpen,"Y"))
					);

		}

		IPage<CourseMaterial> pages = courseMaterialService.page(Condition.getPage(query), queryWrapper.lambda().orderByDesc(CourseMaterial::getCreateTime));
		return R.data(pages);
	}

	/**
	 * 自定义分页 章节资料信息
	 */
	@GetMapping("/page")
	@ApiOperationSupport(order = 2)
	@ApiOperation(value = "分页", notes = "传入courseMaterial")
	public R<IPage<CourseMaterialVO>> page(CourseMaterialVO courseMaterial, Query query) {
		IPage<CourseMaterialVO> pages = courseMaterialService.getCourseMeterialPage(Condition.getPage(query), courseMaterial);
		return R.data(pages);
	}

	/**
	 * 新增 章节资料信息
	 */
	@PostMapping("/save")
	@ApiOperationSupport(order = 4)
	@ApiOperation(value = "新增", notes = "传入courseMaterial")
	public R save(@Valid @RequestBody CourseMaterial courseMaterial) {


		String[] urls = 	courseMaterial.getFileUrl().split(" ");
		String[] fNames = 	courseMaterial.getFileName().split(" ");

		AtomicInteger index=new AtomicInteger(0);
		Arrays.stream(urls).map(item->{
			courseMaterial.setFileUrl(item);
			String fName = fNames[index.getAndIncrement()];
			courseMaterial.setFileName(fName);
			courseMaterial.setName(fName);
			courseMaterial.setCreaterId(SecureUtil.getUserId());
			courseMaterial.setCreaterName(SecureUtil.getUserName());
			courseMaterial.setCreateTime(LocalDateTime.now());
			courseMaterialService.save(courseMaterial);
            return item;
		}).collect(Collectors.toSet());


//		courseMaterial.setCreaterId(SecureUtil.getUserId());
//		courseMaterial.setCreaterName(SecureUtil.getUserName());
//		courseMaterial.setCreateTime(LocalDateTime.now());
		return R.status(true);
	}

	/**
	 * 修改 章节资料信息
	 */
	@PostMapping("/update")
	@ApiOperationSupport(order = 5)
	@ApiOperation(value = "修改", notes = "传入courseMaterial")
	public R update(@Valid @RequestBody CourseMaterial courseMaterial) {
		return R.status(courseMaterialService.updateById(courseMaterial));
	}

	/**
	 * 新增或修改 章节资料信息
	 */
	@PostMapping("/submit")
	@ApiOperationSupport(order = 6)
	@ApiOperation(value = "新增或修改", notes = "传入courseMaterial")
	public R submit(@Valid @RequestBody CourseMaterial courseMaterial) {
		return R.status(courseMaterialService.saveOrUpdate(courseMaterial));
	}


	/**
	 * 删除 章节资料信息
	 */
	@PostMapping("/remove")
	@ApiOperationSupport(order = 8)
	@ApiOperation(value = "删除", notes = "传入ids")
	public R remove(@ApiParam(value = "主键集合", required = true) @RequestParam String ids) {
		return R.status(courseMaterialService.removeByIds(Func.toIntList(ids)));
	}
	@Autowired
	CossUtil cossUtil;

	@Autowired
	private OssService ossService;

	/**
	 * 资料上传请求
	 */
	@PostMapping("/fileUpload")
	@ApiOperation(value = "资料上传", notes = "资料上传")
	public R uploadIcon(MultipartFile file, HttpServletRequest httpServletRequest){

		SystemConstant me = SystemConstant.me();

		String fileName = file.getOriginalFilename();
		String frontPath = crazyProperties.get("front-icon-path");
		CrazyFile crazyFile = getFile(file,"courseMaterial");
		crazyFile.transfer();
//		try {
//			FileUtil.copy(new File(crazyFile.getUploadPath()),new File(frontPath));
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
		String url = "";
		String tenant  = httpServletRequest.getHeader("tenant");
		try {
			SysOss oss =  ossService.upload(crazyFile);

			SysOssUploadVo uploadVo = new SysOssUploadVo();
			uploadVo.setUrl(oss.getUrl());
			uploadVo.setFileName(oss.getOriginalName());
			uploadVo.setOssId(oss.getOssId().toString());
			uploadVo.setFileType(oss.getFileType());

			return R.data(uploadVo);
			//url =  cossUtil.upload(crazyFile,tenant);
		} catch (Exception e) {
			e.printStackTrace();
			return R.fail("上传失败");
		}
//		//上传文件
//		Map<String,String> result = Maps.newHashMap();
//		//result.put("url", me.getDomain() + crazyFile.getUploadVirtualPath());
//		result.put("url", url);
//		result.put("fileName" , fileName);
//		result.put("fileType" , crazyFile.getFile().getContentType());

	}
}
