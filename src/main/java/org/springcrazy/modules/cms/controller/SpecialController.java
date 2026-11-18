
package org.springcrazy.modules.cms.controller;

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
import org.springcrazy.modules.cms.entity.Article;
import org.springcrazy.modules.cms.entity.Special;
import org.springcrazy.modules.cms.service.IArticleService;
import org.springcrazy.modules.cms.service.ISpecialService;
import org.springcrazy.modules.cms.vo.ArticleVO;
import org.springcrazy.modules.cms.vo.SpecialVO;
import org.springcrazy.modules.cms.wrapper.ArticleWrapper;
import org.springcrazy.modules.cms.wrapper.SpecialWrapper;
import org.springcrazy.modules.readtask.entity.Movement;
import org.springcrazy.modules.readtask.wrapper.MovementWrapper;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * 文章信息表 控制器
 *
 * @author TongZhou
 * @since 2020-02-28
 */
@RestController
@AllArgsConstructor
@RequestMapping("/cms/special")
@Api(value = "专题信息表", tags = "文章信息表接口")
public class SpecialController extends CrazyController {

	private ISpecialService specialService;

	/**
	 * 详情
	 */
	@GetMapping("/detail")
	@ApiOperationSupport(order = 1)
	@ApiOperation(value = "详情", notes = "传入article")
	public R<SpecialVO> detail(Special special) {
		Special detail = specialService.getOne(Condition.getQueryWrapper(special));
		return R.data(SpecialWrapper.build().entityVO(detail));
	}

	/**
	 * 分页 文章信息表
	 */
	@GetMapping("/list")
	@ApiOperationSupport(order = 2)
	@ApiOperation(value = "分页", notes = "传入article")
	public R<IPage<SpecialVO>> list(@ApiIgnore @RequestParam Map<String, Object> special, Query query) {
		IPage<Special> pages = specialService.page(Condition.getPage(query), Condition.getQueryWrapper(special, Special.class).lambda().orderByDesc(Special::getSort));
		return R.data(SpecialWrapper.build().pageVO(pages));
	}

	@ApiOperation("搜索")
	@GetMapping("/search")
	public Object search(@ApiIgnore @RequestParam Map<String, Object> bookParamDTO, Query query) {

		QueryWrapper<Special> movementQueryWrapper  = new QueryWrapper<>();
//		movementQueryWrapper.lambda().like(Special::getTitle,"%"+bookParamDTO.get("key")+"%")
//				.or(e->{
//					e.like(Special::getContent,"%"+bookParamDTO.get("key")+"%");
//				}).eq(Special::getType,"readtask").orderByAsc(Special::getCreateTime);

		List<Special> list = specialService.list(movementQueryWrapper);
		return R.data(SpecialWrapper.build().listVO(list));
	}
	/**
	 * 自定义分页 文章信息表
	 */
	@GetMapping("/page")
	@ApiOperationSupport(order = 3)
	@ApiOperation(value = "分页", notes = "传入article")
	public R<IPage<SpecialVO>> page(SpecialVO article, Query query) {
		IPage<SpecialVO> pages = specialService.selectSpecialPage(Condition.getPage(query), article);
		return R.data(pages);
	}

	/**
	 * 新增 文章信息表
	 */
	@PostMapping("/save")
	@ApiOperationSupport(order = 4)
	@ApiOperation(value = "新增", notes = "传入article")
	public R save(@Valid @RequestBody Special article) {
		return R.status(specialService.save(article));
	}

	/**
	 * 修改 文章信息表
	 */
	@PostMapping("/update")
	@ApiOperationSupport(order = 5)
	@ApiOperation(value = "修改", notes = "传入article")
	public R update(@Valid @RequestBody Special article) {
		return R.status(specialService.updateById(article));
	}

	/**
	 * 新增或修改 文章信息表
	 */
	@PostMapping("/submit")
	@ApiOperationSupport(order = 6)
	@ApiOperation(value = "新增或修改", notes = "传入article")
	public R submit(@Valid @RequestBody Special article) {
		return R.status(specialService.saveOrUpdate(article));
	}


	/**
	 * 删除 文章信息表
	 */
	@PostMapping("/remove")
	@ApiOperationSupport(order = 7)
	@ApiOperation(value = "逻辑删除", notes = "传入ids")
	public R remove(@ApiParam(value = "主键集合", required = true) @RequestParam String ids) {
		return R.status(specialService.deleteLogic(Func.toIntList(ids)));
	}


}
