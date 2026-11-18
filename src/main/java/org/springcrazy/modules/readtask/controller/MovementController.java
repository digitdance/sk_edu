package org.springcrazy.modules.readtask.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.hzb.erp.annotation.Log;
import com.hzb.erp.annotation.PreventMultiSubmit;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springcrazy.common.pojo.PaginationVO;
import org.springcrazy.common.tool.CommonUtil;
import org.springcrazy.common.tool.JsonResponse;
import org.springcrazy.common.tool.JsonResponseUtil;
import org.springcrazy.core.boot.ctrl.CrazyController;
import org.springcrazy.core.mp.support.Condition;
import org.springcrazy.core.mp.support.Query;
import org.springcrazy.core.secure.utils.SecureUtil;
import org.springcrazy.core.tool.api.R;
import org.springcrazy.modules.readtask.entity.Movement;
import org.springcrazy.modules.readtask.entity.ReadTask;
import org.springcrazy.modules.readtask.pojo.ReadTaskParamDTO;
import org.springcrazy.modules.readtask.pojo.ReadTaskRecordCommentSaveDTO;
import org.springcrazy.modules.readtask.service.MovementService;
import org.springcrazy.modules.readtask.service.ReadTaskRecordService;
import org.springcrazy.modules.readtask.service.ReadTaskService;
import org.springcrazy.modules.readtask.wrapper.MovementWrapper;
import org.springcrazy.modules.readtask.wrapper.ReadTaskWrapper;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 作业 前端控制器
 * </p>
 *
 * @author Ryan
 */
@RestController
@AllArgsConstructor
@RequestMapping("/movement")
@Api(value = "作业", tags = "作业")
public class MovementController extends CrazyController {


    private MovementService movementService;



    @ApiOperation("任务列表")
    @GetMapping("/list")
    public Object list(@ApiIgnore @RequestParam Map<String, Object> bookParamDTO, Query query) {

        IPage<Movement> pages = movementService.page(Condition.getPage(query), Condition.getQueryWrapper(bookParamDTO, Movement.class).lambda());
        return R.data(MovementWrapper.build().pageVO(pages));
    }


    @ApiOperation("搜索")
    @GetMapping("/search")
    public Object search(@ApiIgnore @RequestParam Map<String, Object> bookParamDTO, Query query) {

        QueryWrapper<Movement> movementQueryWrapper  = new QueryWrapper<>();
        movementQueryWrapper.lambda().like(Movement::getTitle,bookParamDTO.get("key"))
                .or(e->{
                    e.like(Movement::getContent,bookParamDTO.get("key"));
                }).orderByAsc(Movement::getCreateTime);

        List<Movement> list = movementService.list(movementQueryWrapper);
        return R.data(MovementWrapper.build().listVO(list));
    }

//    }

    @ApiOperation("创建和修改作业")
    @Log(description = "创建和修改作业", type = "作业管理")
    @PostMapping("/submit")
    @PreventMultiSubmit
    public JsonResponse save(@RequestBody Movement dto) {

        movementService.saveOrUpdate(dto);
        return JsonResponseUtil.success();
    }

    @ApiOperation("删除作业")
    @Log(description = "删除作业", type = "作业管理")
    @PostMapping("/delete")
    public JsonResponse delete(@RequestBody List<Long> ids) {
        if (movementService.removeByIds(ids)) {
            return JsonResponseUtil.success();
        } else {
            return JsonResponseUtil.error("删除失败");
        }
    }


}
