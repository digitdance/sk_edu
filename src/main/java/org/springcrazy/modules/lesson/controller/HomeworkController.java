package org.springcrazy.modules.lesson.controller;

import com.hzb.erp.annotation.Log;
import com.hzb.erp.annotation.PreventMultiSubmit;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springcrazy.common.tool.CommonUtil;
import org.springcrazy.common.tool.JsonResponse;
import org.springcrazy.common.tool.JsonResponseUtil;
import org.springcrazy.core.secure.utils.SecureUtil;
import org.springcrazy.modules.lesson.entity.Homework;
import org.springcrazy.modules.lesson.pojo.HomeworkParamDTO;
import org.springcrazy.modules.lesson.pojo.HomeworkRecordCommentSaveDTO;
import org.springcrazy.modules.lesson.pojo.HomeworkSaveDTO;
import org.springcrazy.modules.lesson.service.HomeworkRecordService;
import org.springcrazy.modules.lesson.service.HomeworkService;
import org.springcrazy.modules.clazz.entity.Clazz;
import org.springcrazy.modules.clazz.service.ClazzService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/** Teacher endpoints for class homework and grading. */
@RestController
@RequestMapping("/common/homework")
@Api(value = "作业", tags = "作业")
public class HomeworkController {

    @Autowired
    private HomeworkService homeworkService;
    @Autowired
    private HomeworkRecordService homeworkRecordService;
    @Autowired
    private ClazzService clazzService;

    @ApiOperation("作业列表")
    @GetMapping("/list")
    public Object list(@RequestParam(value = "page", required = false) Integer page,
                       @RequestParam(value = "pageSize", defaultValue = "30") Integer pageSize,
                       @RequestParam(value = "title", required = false) String title,
                       @RequestParam(value = "classId", required = false) Long classId) {
        HomeworkParamDTO param = new HomeworkParamDTO();
        param.setPage(page);
        param.setPageSize(pageSize);
        param.setClassId(classId);
        param.setTeacherId(Long.parseLong(SecureUtil.getUserId().toString()));
        param.setTitle(title);
        return page != null && page > 0
                ? JsonResponseUtil.paginate(homeworkService.getList(param))
                : homeworkService.getAll(param);
    }

    @ApiOperation("创建和修改作业")
    @Log(description = "创建和修改作业", type = "作业管理")
    @PostMapping("/save")
    @PreventMultiSubmit
    public JsonResponse save(@Valid @RequestBody HomeworkSaveDTO dto, BindingResult result) {
        CommonUtil.handleValidMessage(result);
        Long teacherId = Long.parseLong(SecureUtil.getUserId().toString());
        Clazz clazz = clazzService.getById(dto.getClassId());
        if (clazz == null || !teacherId.equals(clazz.getTeacherId())) {
            return JsonResponseUtil.error("班级不存在或无权布置作业");
        }
        if (dto.getId() != null) {
            Homework existing = homeworkService.getById(dto.getId());
            if (existing == null || !teacherId.equals(existing.getCreator())) {
                return JsonResponseUtil.error("作业不存在或无权修改");
            }
        }
        return homeworkService.saveOrUpdateByDTO(dto)
                ? JsonResponseUtil.success() : JsonResponseUtil.error("保存失败");
    }

    @ApiOperation("删除作业")
    @Log(description = "删除作业", type = "作业管理")
    @PostMapping("/delete")
    public JsonResponse delete(@RequestBody List<Long> ids) {
        if (ids == null || ids.isEmpty()) return JsonResponseUtil.error("请选择作业");
        Long teacherId = Long.parseLong(SecureUtil.getUserId().toString());
        for (Long id : ids) {
            Homework homework = homeworkService.getById(id);
            if (homework == null || !teacherId.equals(homework.getCreator())) {
                return JsonResponseUtil.error("作业不存在或无权删除");
            }
        }
        return homeworkService.removeByIds(ids)
                ? JsonResponseUtil.success() : JsonResponseUtil.error("删除失败");
    }

    @ApiOperation("作业提交列表")
    @GetMapping("/recordsList")
    public Object recordsList(@RequestParam(value = "page", defaultValue = "1") Integer page,
                              @RequestParam(value = "pageSize", defaultValue = "30") Integer pageSize,
                              @RequestParam("homeworkId") Long homeworkId) {
        Homework homework = homeworkService.getById(homeworkId);
        if (homework == null || !Long.valueOf(SecureUtil.getUserId().toString()).equals(homework.getCreator())) {
            return JsonResponseUtil.error("作业不存在或无权查看");
        }
        HomeworkParamDTO param = new HomeworkParamDTO();
        param.setPage(page);
        param.setPageSize(pageSize);
        param.setHomeworkId(homeworkId);
        return JsonResponseUtil.paginate(homeworkRecordService.getList(param));
    }

    @ApiOperation("作业评价")
    @Log(description = "作业评价", type = "作业管理")
    @PostMapping("/recordComment")
    public JsonResponse recordComment(@Valid @RequestBody HomeworkRecordCommentSaveDTO dto, BindingResult result) {
        CommonUtil.handleValidMessage(result);
        Homework homework = homeworkService.getById(dto.getHomeworkId());
        if (homework == null || !Long.valueOf(SecureUtil.getUserId().toString()).equals(homework.getCreator())) {
            return JsonResponseUtil.error("作业不存在或无权批阅");
        }
        if (homeworkRecordService.comment(dto, Long.parseLong(SecureUtil.getUserId().toString()))) {
            return JsonResponseUtil.success();
        }
        return JsonResponseUtil.error("操作失败");
    }
}
