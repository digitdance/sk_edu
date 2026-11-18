package org.springcrazy.modules.clazz.controller;

import com.hzb.erp.annotation.Log;
import com.hzb.erp.annotation.PreventMultiSubmit;
import org.springcrazy.common.pojo.PaginationVO;
import org.springcrazy.common.tool.CommonUtil;
import org.springcrazy.common.tool.JsonResponse;
import org.springcrazy.common.tool.JsonResponseUtil;



import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springcrazy.core.secure.utils.SecureUtil;
import org.springcrazy.core.tool.api.R;
import org.springcrazy.core.tool.utils.Func;
import org.springcrazy.modules.clazz.entity.ClassGrade;
import org.springcrazy.modules.clazz.pojo.ClassGradeParamDTO;
import org.springcrazy.modules.clazz.service.ClassGradeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * <p>
 * 年级管理 前端控制器
 * </p>
 *
 */
@RestController
@RequestMapping("/common/classgrade")
@Api(value = "年级管理", tags = "年级管理")
public class ClazzGradeController {


    @Autowired
    private ClassGradeService classGradeService;

    @ApiOperation("年级列表")
    @GetMapping("/list")
    public PaginationVO list(@RequestParam(value = "page", defaultValue = "1") Integer page,
                             @RequestParam(value = "pageSize", defaultValue = "30") Integer pageSize,
                             @RequestParam(value = "name", defaultValue = "") String name,
                             @RequestParam(value = "year", defaultValue = "") Integer year) {
        ClassGradeParamDTO param = new ClassGradeParamDTO();
        param.setPage(page);
        param.setPageSize(pageSize);
        param.setName(name);
        param.setYear(year);


        return JsonResponseUtil.paginate(classGradeService.getList(param));
    }

    @ApiOperation("创建和修改年级")
    @Log(description = "创建和修改年级", type = "年级管理")
    @PostMapping("/submit")
    @PreventMultiSubmit
    public JsonResponse save(@Valid @RequestBody ClassGrade dto, BindingResult result) {
        CommonUtil.handleValidMessage(result);
        if (classGradeService.saveOrUpdateByDTO(dto)) {
            return JsonResponseUtil.success();
        } else {
            return JsonResponseUtil.error("提交失败");
        }
    }

    @ApiOperation("删除年级")
    @Log(description = "删除年级", type = "年级管理")
    @PostMapping("/remove")
    public JsonResponse delete(String ids) {

        List<Integer> list = Func.toIntList(ids);
        if (classGradeService.removeByIds(list)) {
            return JsonResponseUtil.success();
        } else {
            return JsonResponseUtil.error("删除失败");
        }
    }

//    @ApiOperation("年级升级")
//    @Log(description = "年级升级", type = "年级管理")
//    @PostMapping("/upgrade")
//    public JsonResponse upgrade(@RequestBody List<String> ids) {
//        if (classGradeService.upgrade(ids, Long.parseLong(SecureUtil.getUserId()+""))) {
//            return JsonResponseUtil.success();
//        } else {
//            return JsonResponseUtil.error("删除失败");
//        }
//    }

}
