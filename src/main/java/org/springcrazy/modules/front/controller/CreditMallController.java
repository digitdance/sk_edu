package org.springcrazy.modules.front.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import javafx.application.Application;
import javafx.stage.Stage;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springcrazy.common.pojo.PaginationVO;
import org.springcrazy.common.tool.JsonResponseUtil;
import org.springcrazy.core.boot.ctrl.CrazyController;
import org.springcrazy.core.mp.support.Condition;
import org.springcrazy.core.mp.support.Query;
import org.springcrazy.core.tool.api.R;
import org.springcrazy.core.tool.utils.StringUtil;
import org.springcrazy.modules.creditMall.entity.CreditMall;
import org.springcrazy.modules.creditMall.pojo.CreditMallParamDTO;
import org.springcrazy.modules.creditMall.pojo.CreditMallVO;
import org.springcrazy.modules.creditMall.service.CreditMallService;
import org.springcrazy.modules.creditMall.wrapper.CreditMallWrapper;
import org.springcrazy.modules.edu.service.ISubjectService;
import org.springcrazy.modules.edu.vo.SubjectVO;
import org.springcrazy.modules.readtask.entity.ReadTask;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/front/credit")
@Api(value = "前端用户相关接口", tags = "前端接口")
public class CreditMallController extends CrazyController {

    private ISubjectService subjectService;
    CreditMallService creditMallService;

    @ApiOperation("积分商品列表")
    @GetMapping("/gifts")
    public R list(CreditMallParamDTO param, Query query) {

        QueryWrapper<CreditMall> queryWrapper   = new QueryWrapper<>();
        queryWrapper.lambda().eq(StringUtil.isNotBlank(param.getType()),CreditMall::getType,param.getType());
        if(StringUtil.isNotBlank(param.getSearchKey())){
            queryWrapper.lambda().like(CreditMall::getName,param.getSearchKey()).or(
                    e->e.like(CreditMall::getContent,param.getSearchKey())
            ).orderByDesc(CreditMall::getSort);
        }
        IPage<CreditMall> pages = creditMallService.page(Condition.getPage(query), queryWrapper);
       return R.data(CreditMallWrapper.build().pageVO(pages));

        //return JsonResponseUtil.paginate(creditMallService.getList(param));
    }


    @ApiOperation("积分商品分类列表")
    @GetMapping("/shopTypeList")
    public R shopTypeList() {
        List<SubjectVO> tree = subjectService.creditMallTypeTree();
        return R.data(tree);
    }




    @ApiOperation("积分商品列表")
    @GetMapping("/giftInfo")
    public R giftInfo(CreditMallParamDTO param) {

        CreditMall creditMall =  creditMallService.getById(param.getId());
        return R.data(CreditMallWrapper.build().entityVO(creditMall));

    }

}
