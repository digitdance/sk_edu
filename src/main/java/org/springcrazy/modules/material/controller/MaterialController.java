package org.springcrazy.modules.material.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.hzb.erp.annotation.Log;
import com.hzb.erp.annotation.PreventMultiSubmit;
import lombok.AllArgsConstructor;
import org.springcrazy.common.enums.SwitchEnum;
import org.springcrazy.common.pojo.PaginationVO;
import org.springcrazy.common.tool.CommonUtil;
import org.springcrazy.common.tool.JsonResponse;
import org.springcrazy.common.tool.JsonResponseUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springcrazy.core.boot.ctrl.CrazyController;
import org.springcrazy.core.mp.support.Condition;
import org.springcrazy.core.mp.support.Query;
import org.springcrazy.core.secure.utils.SecureUtil;
import org.springcrazy.core.tool.api.R;
import org.springcrazy.modules.book.entity.Book;
import org.springcrazy.modules.book.wrapper.BookWrapper;
import org.springcrazy.modules.material.entity.Material;
import org.springcrazy.modules.material.pojo.MaterialParamDTO;
import org.springcrazy.modules.material.pojo.MaterialRecordParamDTO;
import org.springcrazy.modules.material.pojo.MaterialStorageDTO;
import org.springcrazy.modules.material.service.MaterialRecordService;
import org.springcrazy.modules.material.service.MaterialService;
import org.springcrazy.modules.material.wrapper.MaterialWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 物料 前端控制器
 * </p>
 *
 * @author Ryan
 */
@RestController
@RequestMapping("/material")
@AllArgsConstructor
@Api(value = "物料管理", tags = "物料管理")
public class MaterialController extends CrazyController {


    private MaterialService materialService;


    private MaterialRecordService materialRecordService;

//    @ApiOperation("物料列表")
//    @GetMapping("/list")
//    public PaginationVO list(MaterialParamDTO param) {
//        return JsonResponseUtil.paginate(materialService.getList(param));
//    }
    @ApiOperation("图书列表")
    @GetMapping("/list")
    public Object list(@ApiIgnore @RequestParam Map<String, Object> bookParamDTO, Query query) {

        IPage<Material> pages = materialService.page(Condition.getPage(query), Condition.getQueryWrapper(bookParamDTO, Material.class).lambda());
        return R.data(MaterialWrapper.build().pageVO(pages));
    }
    @ApiOperation("创建和修改物料")
    @Log(description = "创建和修改物料", type = "物料管理")
    @PostMapping("/submit")
    @PreventMultiSubmit
    public JsonResponse save(@Valid @RequestBody Material dto, BindingResult result) {

        if(dto.getSchoolId() == null) {
            dto.setSchoolId(Long.parseLong(SecureUtil.getTenantId()));
        }
        CommonUtil.handleValidMessage(result);

        if (materialService.saveOrUpdateByDTO(dto)) {
            return JsonResponseUtil.success();
        } else {
            return JsonResponseUtil.error();
        }
    }

    @ApiOperation("删除物料")
    @Log(description = "删除物料", type = "物料管理")
    @PostMapping("/delete")
    public JsonResponse delete(@RequestBody List<Long> ids) {
        if (materialService.removeByIds(ids)) {
            return JsonResponseUtil.success();
        } else {
            return JsonResponseUtil.error("删除失败");
        }
    }

    @ApiOperation("启用物料")
    @Log(description = "启用物料", type = "物料管理")
    @PostMapping("/open")
    public JsonResponse open(@RequestBody List<Long> ids) {
        if (materialService.switchState(ids, SwitchEnum.YES)) {
            return JsonResponseUtil.success();
        } else {
            return JsonResponseUtil.error("操作失败");
        }
    }

    @ApiOperation("禁用物料")
    @Log(description = "禁用物料", type = "物料管理")
    @PostMapping("/close")
    public JsonResponse close(@RequestBody List<Long> ids) {
        if (materialService.switchState(ids, SwitchEnum.NO)) {
            return JsonResponseUtil.success();
        } else {
            return JsonResponseUtil.error("操作失败");
        }
    }

    @ApiOperation("物料入库")
    @Log(description = "物料入库", type = "物料管理")
    @PostMapping("/handleIn")
    public JsonResponse handleIn(@Valid @RequestBody MaterialStorageDTO dto, BindingResult result) {
        CommonUtil.handleValidMessage(result);
        dto.setInStorage(true);
        if (materialService.handleStorage(dto)) {
            return JsonResponseUtil.success();
        } else {
            return JsonResponseUtil.error("操作失败");
        }
    }

    @ApiOperation("物料出库")
    @Log(description = "物料出库", type = "物料管理")
    @PostMapping("/handleOut")
    public JsonResponse handleOut(@Valid @RequestBody MaterialStorageDTO dto, BindingResult result) {
        CommonUtil.handleValidMessage(result);
        dto.setInStorage(false);
        if (materialService.handleStorage(dto)) {
            return JsonResponseUtil.success();
        } else {
            return JsonResponseUtil.error("操作失败");
        }
    }

    @ApiOperation("出入库记录")
    @GetMapping("/storageRecord")
    public PaginationVO creditLog(MaterialRecordParamDTO param) {
        return JsonResponseUtil.paginate(materialRecordService.getList(param));
    }

}
