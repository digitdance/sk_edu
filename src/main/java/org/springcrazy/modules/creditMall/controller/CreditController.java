package org.springcrazy.modules.creditMall.controller;

//import org.springcrazy.core.annotation.Log;
//import org.springcrazy.core.annotation.PreventMultiSubmit;
//import com.hzb.erp.common.enums.SwitchEnum.java;
//import com.hzb.erp.common.pojo.AuditParamDTO;
//import org.springcrazy.common.pojo.PaginationVO;
//import org.springcrazy.common.tool.CommonUtil;
//import org.springcrazy.common.tool.JsonResponse;
//import org.springcrazy.common.tool.JsonResponseUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.hzb.erp.annotation.Log;
import com.hzb.erp.annotation.PreventMultiSubmit;
import com.hzb.erp.common.enums.SwitchEnum;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import lombok.AllArgsConstructor;
import org.springcrazy.common.pojo.AuditParamDTO;
import org.springcrazy.common.pojo.PaginationVO;
import org.springcrazy.common.tool.CommonUtil;
import org.springcrazy.common.tool.JsonResponse;
import org.springcrazy.common.tool.JsonResponseUtil;
import org.springcrazy.core.boot.ctrl.CrazyController;
import org.springcrazy.core.mp.support.Condition;
import org.springcrazy.core.mp.support.Query;
import org.springcrazy.core.tool.api.R;
import org.springcrazy.core.tool.utils.StringUtil;
import org.springcrazy.modules.cms.entity.Article;
import org.springcrazy.modules.cms.wrapper.ArticleWrapper;
import org.springcrazy.modules.creditMall.entity.CreditMall;
import org.springcrazy.modules.creditMall.pojo.CreditExchangeParamDTO;
import org.springcrazy.modules.creditMall.pojo.CreditMallParamDTO;
import org.springcrazy.modules.creditMall.service.CreditExchangeService;
import org.springcrazy.modules.creditMall.service.CreditMallService;
import org.springcrazy.modules.creditMall.wrapper.CreditMallWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 积分商城 前端控制器
 * </p>
 *
 * @author 541720500@qq.com
 */
@RestController
@AllArgsConstructor
@RequestMapping("/creditMall")
@Api(value = "积分商城", tags = "积分商城")
public class CreditController extends CrazyController {
    private CreditMallService creditMallService;
    private CreditExchangeService creditExchangeService;

    @ApiOperation("积分商城积分礼品列表")
    @GetMapping("/list")
    public R gifts(CreditMallParamDTO param, Query query) {

        QueryWrapper<CreditMall> queryWrapper   = new QueryWrapper<>();
        if(StringUtil.isNotBlank(param.getSearchKey())){
            queryWrapper.lambda().like(CreditMall::getName,param.getSearchKey()).or(
                    e->e.like(CreditMall::getContent,param.getSearchKey())
            ).orderByDesc(CreditMall::getSort);
        }
        IPage<CreditMall> pages = creditMallService.page(Condition.getPage(query), queryWrapper);
        return R.data(CreditMallWrapper.build().pageVO(pages));

    }

    @ApiOperation("创建和修改积分礼品")
    @Log(description = "创建和修改积分礼品", type = "积分礼品管理")
    @PostMapping("/submit")
    @PreventMultiSubmit
    public JsonResponse save(@Valid @RequestBody CreditMall saveDTO, BindingResult result) {
        CommonUtil.handleValidMessage(result);
        if (creditMallService.saveOrUpdateByDTO(saveDTO)) {
            return JsonResponseUtil.success();
        } else {
            return JsonResponseUtil.error("提交失败");
        }
    }

    @ApiOperation("删除积分礼品")
    @Log(description = "删除积分礼品", type = "积分礼品管理")
    @PostMapping("/delete")
    public JsonResponse delete(@RequestBody List<Long> ids) {
        if (creditMallService.removeByIds(ids)) {
            return JsonResponseUtil.success();
        } else {
            return JsonResponseUtil.error("删除失败");
        }
    }

    @ApiOperation("启用积分礼品")
    @Log(description = "启用积分礼品", type = "积分礼品管理")
    @PostMapping("/open")
    public JsonResponse open(@RequestBody List<Long> ids) {
        if (creditMallService.switchState(ids, SwitchEnum.YES.getCode())) {
            return JsonResponseUtil.success();
        } else {
            return JsonResponseUtil.error("操作失败");
        }
    }

    @ApiOperation("禁用积分礼品")
    @Log(description = "禁用积分礼品", type = "积分礼品管理")
    @PostMapping("/close")
    public JsonResponse close(@RequestBody List<Long> ids) {
        if (creditMallService.switchState(ids, SwitchEnum.NO.getCode())) {
            return JsonResponseUtil.success();
        } else {
            return JsonResponseUtil.error("操作失败");
        }
    }

    @ApiOperation("积分礼品兑换记录")
    @GetMapping("/exchangeLog")
    public PaginationVO exchangeLog(CreditExchangeParamDTO param) {
        return JsonResponseUtil.paginate(creditExchangeService.getList(param));
    }

    @ApiOperation("积分兑换礼品审核")
    @Log(description = "积分兑换礼品审核", type = "积分礼品管理")
    @PostMapping("/exchangeAudit")
    @PreventMultiSubmit
    public JsonResponse exchangeAudit(@Valid @RequestBody AuditParamDTO auditDTO, BindingResult result) {
        CommonUtil.handleValidMessage(result);
        //auditDTO.setStaffId(StaffAuthService.getCurrentUserId());
        if (creditExchangeService.exchangeAudit(auditDTO)) {
            return JsonResponseUtil.success();
        } else {
            return JsonResponseUtil.error("审核失败");
        }
    }
}
