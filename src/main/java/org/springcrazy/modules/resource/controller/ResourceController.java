
package org.springcrazy.modules.resource.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.*;
import lombok.AllArgsConstructor;
import org.springcrazy.core.boot.ctrl.CrazyController;
import org.springcrazy.core.mp.support.Condition;
import org.springcrazy.core.mp.support.Query;
import org.springcrazy.core.secure.CrazyUser;
import org.springcrazy.core.tool.api.R;
import org.springcrazy.core.tool.constant.CrazyConstant;
import org.springcrazy.core.tool.utils.Func;
import org.springcrazy.modules.edu.entity.CourseMaterial;
import org.springcrazy.modules.edu.entity.Subject;
import org.springcrazy.modules.edu.entity.Videosource;
import org.springcrazy.modules.edu.service.ICourseMaterialService;
import org.springcrazy.modules.edu.service.ISubjectService;
import org.springcrazy.modules.edu.service.impl.CourseMaterialServiceImpl;
import org.springcrazy.modules.edu.vo.CourseMaterialVO;
import org.springcrazy.modules.edu.vo.SubjectVO;
import org.springcrazy.modules.edu.vo.VideosourceVO;
import org.springcrazy.modules.edu.wrapper.SubjectWrapper;
import org.springcrazy.modules.edu.wrapper.VideosourceWrapper;
import org.springcrazy.modules.material.entity.Material;
import org.springcrazy.modules.material.pojo.MaterialVO;
import org.springcrazy.modules.material.service.MaterialService;
import org.springcrazy.modules.material.wrapper.MaterialWrapper;
import org.springcrazy.modules.system.entity.Tenant;
import org.springcrazy.modules.system.service.ITenantService;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.Valid;
import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * 控制器
 *
 * @author TongZhou
 */
@RestController
@AllArgsConstructor
@RequestMapping("/resource")
@ApiIgnore
@Api(value = "资源管理", tags = "接口")
public class ResourceController extends CrazyController {

	private ISubjectService subjectService;

    private ICourseMaterialService courseMaterialService;

	/**
	 * 详情
	 */

    @GetMapping("/resourceTree")
    @ApiOperation(value = "详情", notes = "传入tenant")
    public R<Object> resourceTypeTree(Tenant tenant) {
        List<SubjectVO> tree = subjectService.resourceTypeTree();
        return R.data(tree);
    }

    /**
     * 详情
     */
    @GetMapping("/detail")
    @ApiOperationSupport(order = 1)
    @ApiOperation(value = "详情", notes = "传入material")
    public R<CourseMaterial> detail(CourseMaterial material) {
        CourseMaterial detail = courseMaterialService.getOne(Condition.getQueryWrapper(material));
        return R.data(detail);
    }



//
//	/**
//	 * 分页
//	 */
//	@GetMapping("/list")
//	@ApiImplicitParams({
//		@ApiImplicitParam(name = "tenantId", value = "参数名称", paramType = "query", dataType = "string"),
//		@ApiImplicitParam(name = "tenantName", value = "角色别名", paramType = "query", dataType = "string"),
//		@ApiImplicitParam(name = "contactNumber", value = "联系电话", paramType = "query", dataType = "string")
//	})
//	@ApiOperation(value = "分页", notes = "传入tenant")
//	public R<IPage<Tenant>> list(@ApiIgnore @RequestParam Map<String, Object> tenant, Query query, CrazyUser bladeUser) {
//		QueryWrapper<Tenant> queryWrapper = Condition.getQueryWrapper(tenant, Tenant.class);
//		IPage<Tenant> pages = tenantService.page(Condition.getPage(query), (!bladeUser.getTenantId().equals(CrazyConstant.ADMIN_TENANT_ID)) ? queryWrapper.lambda().eq(Tenant::getTenantId, bladeUser.getTenantId()) : queryWrapper);
//		return R.data(pages);
//	}
//
//	@GetMapping("/listPath")
//	@ApiImplicitParams({
//			@ApiImplicitParam(name = "tenantId", value = "参数名称", paramType = "query", dataType = "string"),
//			@ApiImplicitParam(name = "tenantName", value = "角色别名", paramType = "query", dataType = "string"),
//			@ApiImplicitParam(name = "contactNumber", value = "联系电话", paramType = "query", dataType = "string")
//	})
//	@ApiOperation(value = "分页", notes = "传入tenant")
//	public R<List<Tenant>> listPath(@ApiIgnore @RequestParam Map<String, Object> resource, Query query, CrazyUser bladeUser) {
//
//
//		//创建File对象，指定路径文件
//		File file1=new File(resource.get("rootPath").toString());
//
//		//判断是否有目录
//		if(file1.isDirectory()) {
//			//获取目录中的所有文件名称
//			String[] fileName=file1.list();
//			//对指定路径下的文件或目录进行遍历
//			Arrays.stream(fileName)
//					.forEach(c->
//
//							System.out.println(c)
//
//					);
//		}
//		System.out.println("----------------------------");
//
//		//创建File对象，指定路径文件
//		File file2=new File("D:\\综合文件\\研究生资料\\财务处资料");
//
//		//判断是否有目录
//		if(file2.isDirectory()) {
//			//使用Lambda表达式过滤目录中所有以docx的文件--遍历指定类型的目录文件
//			String[] fileName=file2.list(
//					(dir,name)->name.endsWith(".docx"));
//
//			//对指定路径下的文件或目录进行遍历
//			Arrays.stream(fileName)
//					.forEach(c->System.out.println(c));
//		}
//
////		QueryWrapper<Tenant> queryWrapper = Condition.getQueryWrapper(tenant, Tenant.class);
////		IPage<Tenant> pages = tenantService.page(Condition.getPage(query), (!bladeUser.getTenantId().equals(CrazyConstant.ADMIN_TENANT_ID)) ? queryWrapper.lambda().eq(Tenant::getTenantId, bladeUser.getTenantId()) : queryWrapper);
////		return R.data(pages);
//	}
//
//	/**
//	 * 下拉数据源
//	 */
//	@GetMapping("/select")
//	@ApiOperation(value = "下拉数据源", notes = "传入tenant")
//	public R<List<Tenant>> select(Tenant tenant, CrazyUser bladeUser) {
//		QueryWrapper<Tenant> queryWrapper = Condition.getQueryWrapper(tenant);
//		List<Tenant> list = tenantService.list((!bladeUser.getTenantId().equals(CrazyConstant.ADMIN_TENANT_ID)) ? queryWrapper.lambda().eq(Tenant::getTenantId, bladeUser.getTenantId()) : queryWrapper);
//		return R.data(list);
//	}
//
//	/**
//	 * 自定义分页
//	 */
//	@GetMapping("/page")
//	@ApiOperation(value = "分页", notes = "传入tenant")
//	public R<IPage<Tenant>> page(Tenant tenant, Query query) {
//		IPage<Tenant> pages = tenantService.selectTenantPage(Condition.getPage(query), tenant);
//		return R.data(pages);
//	}
//
//
//	/**
//	 * 新增或修改
//	 */
//	@PostMapping("/submit")
//	@ApiOperation(value = "新增或修改", notes = "传入tenant")
//	public R submit(@Valid @RequestBody Tenant tenant) {
//		return R.status(tenantService.saveTenant(tenant));
//	}
//
//
//	/**
//	 * 删除
//	 */
//	@PostMapping("/remove")
//	@ApiOperation(value = "逻辑删除", notes = "传入ids")
//	public R remove(@ApiParam(value = "主键集合", required = true) @RequestParam String ids) {
//		return R.status(tenantService.deleteLogic(Func.toIntList(ids)));
//	}


}
