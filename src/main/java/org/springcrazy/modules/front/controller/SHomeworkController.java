package org.springcrazy.modules.front.controller;


import com.baomidou.mybatisplus.core.conditions.query.Query;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.hzb.erp.annotation.Log;
import org.apache.commons.lang3.StringUtils;
import org.springcrazy.common.pojo.PaginationVO;
import org.springcrazy.common.tool.CommonUtil;
import org.springcrazy.common.tool.JsonResponse;
import org.springcrazy.common.tool.JsonResponseUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springcrazy.core.mp.support.Condition;
import org.springcrazy.core.secure.utils.SecureUtil;
import org.springcrazy.core.tool.api.R;
import org.springcrazy.core.tool.utils.StringUtil;
import org.springcrazy.modules.exam.entity.PaperType;
import org.springcrazy.modules.lesson.pojo.HomeworkParamDTO;
import org.springcrazy.modules.lesson.pojo.HomeworkRecordSaveDTO;
import org.springcrazy.modules.lesson.pojo.HomeworkVO;
import org.springcrazy.modules.lesson.entity.Homework;
import org.springcrazy.modules.clazz.entity.ClassStudent;
import org.springcrazy.modules.clazz.mapper.ClassStudentMapper;
import org.springcrazy.modules.lesson.service.HomeworkRecordService;
import org.springcrazy.modules.lesson.service.HomeworkService;
import org.springcrazy.modules.readtask.entity.ReadTask;
import org.springcrazy.modules.readtask.entity.ReadTaskRecord;
import org.springcrazy.modules.readtask.mapper.ReadTaskMapper;
import org.springcrazy.modules.readtask.pojo.ReadTaskParamDTO;
import org.springcrazy.modules.readtask.pojo.ReadTaskVO;
import org.springcrazy.modules.readtask.service.ReadTaskRecordService;
import org.springcrazy.modules.readtask.service.ReadTaskService;
import org.springcrazy.modules.readtask.wrapper.ReadTaskWrapper;
import org.springcrazy.modules.user.entity.Student;
import org.springcrazy.modules.user.service.IStudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import java.util.List;

import static com.hzb.erp.constants.CommonConst.STUDENT_CENTER_URL_PREFIX;

/**
 * @author Ryan 541720500@qq.com
 * description
 */
@RestController
@RequestMapping(  "/front/homework")
@Api(value = "作业", tags = "作业")
public class SHomeworkController {

    @Autowired
    private HomeworkService homeworkService;
    @Autowired
    private IStudentService studentService;

    @Autowired
    private ReadTaskService readTaskService;
    @Autowired
    private ReadTaskMapper readTaskMapper;
    @Autowired
    private HomeworkRecordService homeworkRecordService;
    @Autowired
    private ClassStudentMapper classStudentMapper;
    @Autowired
    private ReadTaskRecordService readTaskRecordService;

    @ApiOperation("学生作业列表")
    @GetMapping("/coursework/list")
    public R courseworkList(@RequestParam(value = "current", defaultValue = "1") Integer current,
                            @RequestParam(value = "size", defaultValue = "20") Integer size,
                            @RequestParam(value = "title", required = false) String title) {
        Student student = studentService.getStudent(SecureUtil.getUserId());
        if (student == null) return R.fail("请先添加学生");
        HomeworkParamDTO param = new HomeworkParamDTO();
        param.setPage(current);
        param.setPageSize(size);
        param.setStudentId(Long.valueOf(student.getId()));
        param.setTitle(title);
        return R.data(homeworkService.getList(param));
    }

    @ApiOperation("学生作业详情")
    @GetMapping("/coursework/info/{id}")
    public R courseworkInfo(@PathVariable Long id) {
        Student student = studentService.getStudent(SecureUtil.getUserId());
        if (student == null) return R.fail("请先添加学生");
        Homework homework = homeworkService.getById(id);
        if (homework == null || !isClassMember(homework.getClassId(), Long.valueOf(student.getId()))) {
            return R.fail("作业不存在或无权查看");
        }
        return R.data(homeworkService.getInfo(id, Long.valueOf(student.getId())));
    }

    @ApiOperation("提交作业")
    @Log(description = "提交作业", type = "学生端", isStaff = false)
    @PostMapping("/coursework/submit")
    public JsonResponse submitCoursework(@Valid @RequestBody HomeworkRecordSaveDTO dto, BindingResult result) {
        CommonUtil.handleValidMessage(result);
        Student student = studentService.getStudent(SecureUtil.getUserId());
        if (student == null) return JsonResponseUtil.error("请先添加学生");
        Homework homework = homeworkService.getById(dto.getHomeworkId());
        Long studentId = Long.valueOf(student.getId());
        if (homework == null || !isClassMember(homework.getClassId(), studentId)) {
            return JsonResponseUtil.error("作业不存在或无权提交");
        }
        dto.setStudentId(studentId);
        return homeworkRecordService.saveRecord(dto)
                ? JsonResponseUtil.success() : JsonResponseUtil.error("提交失败");
    }

    private boolean isClassMember(Long classId, Long studentId) {
        return classStudentMapper.selectCount(new QueryWrapper<ClassStudent>().lambda()
                .eq(ClassStudent::getClassId, classId)
                .eq(ClassStudent::getStudentId, studentId)
                .eq(ClassStudent::getDeleted, false)) > 0;
    }

    @ApiOperation("作业信息")
    @GetMapping("/info")
    public R info(@RequestParam("id") Long id) {
//        Student student = StudentAuthService.getCurrentStudent();
//        if (student == null) {
//            return null;
//        }
        ReadTask task =  readTaskMapper.selectById(id);
        ReadTaskVO readTaskVO =  ReadTaskWrapper.build().entityVO(task);
        List<ReadTaskRecord> records =  readTaskRecordService.list(new QueryWrapper<ReadTaskRecord>().lambda().eq(ReadTaskRecord::getTaskId,task.getId()).eq(ReadTaskRecord::getUserId,SecureUtil.getUserId()));
        readTaskVO.setRecordList(records);

        return R.data(readTaskVO);


    }

    @ApiOperation("任务列表")
    @GetMapping("/list")
    public R list(ReadTask readTask, Query query) {
        List<ReadTask> List = readTaskService.list(Condition.getQueryWrapper(readTask).lambda().orderByDesc(ReadTask::getSort));
        return R.data(ReadTaskWrapper.build().listVO(List));
    }

    @ApiOperation("删除作业")
    @Log(description = "删除作业", type = "学生端", isStaff = false)
    @PostMapping("/deleteRecord/{id}")
    public JsonResponse deleteRecord(@PathVariable("id") Long id) {
        Student student =  studentService.getStudent(SecureUtil.getUserId());
        if (student == null) {
            return JsonResponseUtil.error("请先添加学生");
        }
        Long studentId = Long.parseLong(student.getId()+"");
        if (homeworkRecordService.deleteByStudentId(id, studentId)) {
            return JsonResponseUtil.success("已删除");
        } else {
            return JsonResponseUtil.error("删除失败");
        }
    }

    @ApiOperation("提交作业")
    @Log(description = "提交作业", type = "学生端", isStaff = false)
    @PostMapping("/saveRecord")
    public JsonResponse saveRecord(@Valid @RequestBody HomeworkRecordSaveDTO dto, BindingResult result) {
        CommonUtil.handleValidMessage(result);
        Student student =  studentService.getStudent(SecureUtil.getUserId());
        if (student == null) {
            return JsonResponseUtil.error("请先添加学生");
        }
        Long studentId = Long.parseLong(student.getId()+"");
        Homework homework = homeworkService.getById(dto.getHomeworkId());
        if (homework == null || !isClassMember(homework.getClassId(), studentId)) {
            return JsonResponseUtil.error("作业不存在或无权提交");
        }
        dto.setStudentId(studentId);
        if (homeworkRecordService.saveRecord(dto)) {
            return JsonResponseUtil.success();
        } else {
            return JsonResponseUtil.error("提交失败");
        }
    }

    @ApiOperation("获取当月阅读任务")
    @GetMapping("/getMonthTasks")
    public R selectMonthTask(ReadTaskParamDTO dto) {

        if(StringUtils.isBlank(dto.getClassIds())){
              return R.fail("请先添加学生");
        }
        List<ReadTask> taskList =  readTaskMapper.selectTaskByDay(dto);
        return R.data(taskList);
    }

}
