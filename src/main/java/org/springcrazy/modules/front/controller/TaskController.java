package org.springcrazy.modules.front.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springcrazy.core.mp.support.Query;
import org.springcrazy.core.tool.api.R;
import org.springcrazy.modules.readtask.entity.ReadTask;
import org.springcrazy.modules.readtask.pojo.ReadTaskParamDTO;
import org.springcrazy.modules.readtask.service.ReadTaskService;
import org.springcrazy.modules.user.entity.Student;
import org.springcrazy.modules.user.service.IStudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@Controller
@RequestMapping("/front/readTask")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Api(value = "第三方登录接口", tags = "第三方登录接口")
public class TaskController {

    private ReadTaskService readTaskService;

    private IStudentService studentService;
    @ApiOperation("作业列表")
    @GetMapping("/list")
    public R list(ReadTask readTask, Query query) {
        QueryWrapper<ReadTask> readTaskQueryWrapper = new QueryWrapper<>();
        readTaskQueryWrapper.lambda().gt(ReadTask::getTaskDateStart,readTask.getTaskDateStart())
                .lt(ReadTask::getTaskDateEnd,readTask.getTaskDateEnd());
        return R.data(readTaskService.list(readTaskQueryWrapper));
    }


    @ApiOperation("进行中的任务")
    @GetMapping("/myTasking")
    public R myTasking(ReadTaskParamDTO readTask, Query query) {

        Student student = studentService.getById(readTask.getStudentId());
        QueryWrapper<ReadTask> readTaskQueryWrapper = new QueryWrapper<>();
        readTaskQueryWrapper.lambda().eq(ReadTask::getStatus,1)
                .eq(ReadTask::getClassId,student.getClassId());
        return R.data(readTaskService.list(readTaskQueryWrapper));
    }


}
