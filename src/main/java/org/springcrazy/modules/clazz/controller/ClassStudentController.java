package org.springcrazy.modules.clazz.controller;



import com.hzb.erp.annotation.Log;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springcrazy.common.tool.JsonResponse;
import org.springcrazy.common.tool.JsonResponseUtil;

import org.springcrazy.core.boot.ctrl.CrazyController;
import org.springcrazy.core.secure.utils.SecureUtil;
import org.springcrazy.modules.clazz.pojo.ClassStudentAddDTO;
import org.springcrazy.modules.clazz.service.ClassStudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 班级学员表 前端控制器
 * </p>
 *
 * @author 541720500@qq.com
 */
@RestController
@RequestMapping("/common/classStudent")
@Api(value = "班级学员管理", tags = "班级学员管理")
public class ClassStudentController extends CrazyController {


    private ClassStudentService classStudentService;
//    @Autowired
//    private LessonService lessonService;

    @ApiOperation("班级添加单个学员")
    @Log(description = "班级添加单个学员")
    @GetMapping("/addStudent")
    public JsonResponse addStudent(@RequestParam(value = "classId") Long classId,
                                   @RequestParam(value = "studentId") Long studentId) {

        if (classStudentService.addClassStudent(classId, studentId, Long.parseLong(SecureUtil.getUserId()+""))) {
            return JsonResponseUtil.success();
        } else {
            return JsonResponseUtil.error("添加失败");
        }
    }

    @ApiOperation("班级批量添加学员")
    @Log(description = "班级批量添加学员", type = "学员管理")
    @PostMapping("/addStudents")
    public JsonResponse addStudents(@RequestBody ClassStudentAddDTO dto) {
        classStudentService.addClassStudents(dto, Long.parseLong(SecureUtil.getUserId()+""));
        return JsonResponseUtil.success();
    }

    @ApiOperation("班级移除单个学员")
    @Log(description = "班级移除单个学员", type = "班级学员管理")
    @GetMapping("/deleteClassStudent")
    public JsonResponse deleteClassStudent(@RequestParam(value = "classId") Long classId,
                                           @RequestParam(value = "studentId") Long studentId) {

        if (classStudentService.deleteClassStudent(classId, studentId)) {
            return JsonResponseUtil.success();
        } else {
            return JsonResponseUtil.error("提交失败");
        }
    }

    @ApiOperation("班级批量移除学员")
    @Log(description = "班级批量移除学员", type = "班级学员管理")
    @PostMapping("/deleteByIds")
    public JsonResponse deleteByIds(@RequestBody List<Long> ids) {
        if (classStudentService.deleteClassStudentByIds(ids)) {
            return JsonResponseUtil.success();
        } else {
            return JsonResponseUtil.error("删除失败");
        }
    }
    @ApiOperation("批量调班")
    @Log(description = "批量调班", type = "班级学员管理")
    @PostMapping("/changeClassByIds/{newClassId}")
    public JsonResponse changeClassByIds(@RequestBody List<Long> ids, @PathVariable Long newClassId) {
        if (classStudentService.changeClassByIds(ids, newClassId, Long.parseLong(SecureUtil.getUserId()+""))) {
            return JsonResponseUtil.success("已调班,请到新班级里确认学生的消费课程");
        } else {
            return JsonResponseUtil.error("删除失败");
        }
    }

    @ApiOperation("更换默认消费课程")
    @Log(description = "更换默认消费课程", type = "班级学员管理")
    @GetMapping("/changeConsumeCourse")
    public JsonResponse changeConsumeCourse(@RequestParam("consumeCourseId") Long consumeCourseId,
                                            @RequestParam("classStudentId") Long classStudentId) {
        if (classStudentService.changeConsumeCourse(classStudentId, consumeCourseId)) {
            return JsonResponseUtil.success();
        } else {
            return JsonResponseUtil.error("提交失败");
        }
    }

    @ApiOperation("修改签到消课课程")
    @Log(description = "修改签到消课课程", type = "班级学员管理")
    @GetMapping("/changeCourseAtSign")
    public JsonResponse changeCourseAtSign(@RequestParam("lessonId") Long lessonId,
                                           @RequestParam("studentId") Long studentId,
                                           @RequestParam("courseId") Long courseId) {
//        if (lessonService.changeCourseAtSign(lessonId, studentId, courseId)) {
//            return JsonResponseUtil.success();
//        } else {
//            return JsonResponseUtil.error("提交失败");
//        }
        return JsonResponseUtil.error("提交失败");
    }

}
