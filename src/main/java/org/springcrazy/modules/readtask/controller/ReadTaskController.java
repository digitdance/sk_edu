package org.springcrazy.modules.readtask.controller;


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
import org.springcrazy.modules.book.entity.Book;
import org.springcrazy.modules.book.wrapper.BookWrapper;
import org.springcrazy.modules.lesson.pojo.HomeworkParamDTO;
import org.springcrazy.modules.lesson.pojo.HomeworkRecordCommentSaveDTO;
import org.springcrazy.modules.lesson.pojo.HomeworkSaveDTO;
import org.springcrazy.modules.lesson.service.HomeworkRecordService;
import org.springcrazy.modules.lesson.service.HomeworkService;
import org.springcrazy.modules.readtask.entity.ReadTask;
import org.springcrazy.modules.readtask.pojo.ReadTaskParamDTO;
import org.springcrazy.modules.readtask.pojo.ReadTaskRecordCommentSaveDTO;
import org.springcrazy.modules.readtask.pojo.ReadTaskSaveDTO;
import org.springcrazy.modules.readtask.service.ReadTaskRecordService;
import org.springcrazy.modules.readtask.service.ReadTaskService;
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
@RequestMapping("/readtask")
@Api(value = "作业", tags = "作业")
public class ReadTaskController extends CrazyController {


    private ReadTaskService readTaskService;


    private ReadTaskRecordService readTaskRecordService;

    @ApiOperation("任务列表")
    @GetMapping("/list")
    public Object list(@ApiIgnore @RequestParam Map<String, Object> bookParamDTO, Query query) {

        IPage<ReadTask> pages = readTaskService.page(Condition.getPage(query), Condition.getQueryWrapper(bookParamDTO, ReadTask.class).lambda());
        return R.data(ReadTaskWrapper.build().pageVO(pages));
    }

//    @ApiOperation("作业列表")
//    @GetMapping("/list")
//    public Object list(@RequestParam(value = "page", defaultValue = "") Integer page, // 若为null则是查全部
//                       @RequestParam(value = "pageSize", defaultValue = "30") Integer pageSize,
//                       @RequestParam(value = "title", defaultValue = "") String title,
//                       @RequestParam(value = "classId", defaultValue = "") Long classId,
//                       @RequestParam(value = "teacherId", defaultValue = "") Long teacherId) {
//        HomeworkParamDTO param = new HomeworkParamDTO();
//        param.setPage(page);
//        param.setPageSize(pageSize);
//        param.setClassId(classId);
//        param.setTeacherId(teacherId);
//        param.setTitle(title);
//
//        return page != null && page > 0 ?
//                JsonResponseUtil.paginate(homeworkService.getList(param)) :
//                homeworkService.getAll(param);
//    }

    @ApiOperation("创建和修改作业")
    @Log(description = "创建和修改作业", type = "作业管理")
    @PostMapping("/submit")
    @PreventMultiSubmit
    public JsonResponse save(@RequestBody ReadTaskParamDTO dto, BindingResult result) {
        CommonUtil.handleValidMessage(result);
        readTaskService.saveOrUpdateByDTO(dto);
        return JsonResponseUtil.success();
    }

    @ApiOperation("删除作业")
    @Log(description = "删除作业", type = "作业管理")
    @PostMapping("/delete")
    public JsonResponse delete(@RequestBody List<Long> ids) {
        if (readTaskService.removeByIds(ids)) {
            return JsonResponseUtil.success();
        } else {
            return JsonResponseUtil.error("删除失败");
        }
    }

    @ApiOperation("作业提交列表")
    @GetMapping("/recordsList")
    public PaginationVO recordsList(
            @RequestParam(value = "page", defaultValue = "") Integer page,
            @RequestParam(value = "pageSize", defaultValue = "30") Integer pageSize,
            @RequestParam(value = "homeworkId", defaultValue = "") Long homeworkId) {

        ReadTaskParamDTO param = new ReadTaskParamDTO();
        param.setPage(page);
        param.setPageSize(pageSize);
        //param.setTaskId(homeworkId);

        return JsonResponseUtil.paginate(readTaskRecordService.getList(param));
    }

    @ApiOperation("作业评价")
    @Log(description = "作业评价", type = "作业管理")
    @PostMapping("/recordComment")
    public JsonResponse recordComment(@Valid @RequestBody ReadTaskRecordCommentSaveDTO dto, BindingResult result) {
        CommonUtil.handleValidMessage(result);
        if (readTaskRecordService.comment(dto, Long.parseLong(SecureUtil.getUserId()+""))) {
            return JsonResponseUtil.success();
        } else {
            return JsonResponseUtil.error("操作失败");
        }
    }


}
