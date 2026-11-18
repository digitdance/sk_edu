//package org.springcrazy.modules.lesson.controller;
//
//
//import org.springcrazy.core.annotation.Log;
//import org.springcrazy.core.annotation.PreventMultiSubmit;
//import com.hzb.erp.api.base.service.StaffAuthService;
//import org.springcrazy.modules.lesson.entity.LessonStudent;
//import org.springcrazy.modules.lesson.pojo.LessonEvaluateSaveDTO;
//import org.springcrazy.modules.lesson.pojo.LessonStudentParamDTO;
//import org.springcrazy.modules.lesson.service.LessonStudentService;
//import org.springcrazy.modules.student.pojo.StudentLessonLogParamDTO;
//import org.springcrazy.modules.student.service.StudentLessonCountLogService;
//import org.springcrazy.common.pojo.PaginationVO;
//import org.springcrazy.common.tool.CommonUtil;
//import org.springcrazy.common.tool.JsonResponse;
//import org.springcrazy.common.tool.JsonResponseUtil;
//import io.swagger.annotations.Api;
//import io.swagger.annotations.ApiOperation;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.format.annotation.DateTimeFormat;
//import org.springframework.validation.BindingResult;
//import org.springframework.web.bind.annotation.*;
//
//import javax.validation.Valid;
//import java.time.LocalDate;
//import java.util.List;
//
///**
// * <p>
// * 课时学员关联表 前端控制器
// * </p>
// *
// * @author Ryan
// */
//@RestController
//@RequestMapping("/common/lessonStudent")
//@Api(value = "课时学员关联", tags = "课时学员关联")
//public class LessonStudentController {
//
//    @Autowired
//    private LessonStudentService lessonStudentService;
//
//    @Autowired
//    private StudentLessonCountLogService studentLessonCountLogService;
//
//    @ApiOperation("消课记录")
//    @GetMapping("/lessonCountLog")
//    public PaginationVO lessonCountLog(@RequestParam(value = "page", defaultValue = "1") Integer page, // 若为null则是查全部
//                                       @RequestParam(value = "pageSize", defaultValue = "30") Integer pageSize,
//                                       @RequestParam(value = "studentId", defaultValue = "") Long studentId) {
//
//        StudentLessonLogParamDTO param = new StudentLessonLogParamDTO();
//        param.setPage(page);
//        param.setPageSize(pageSize);
//        param.setStudentId(studentId);
//
//        return JsonResponseUtil.paginate(studentLessonCountLogService.getList(param));
//    }
//
//    @ApiOperation("点评记录")
//    @GetMapping("/evaluateLog")
//    public Object evaluateLog(@RequestParam(value = "page", defaultValue = "") Integer page,
//                              @RequestParam(value = "pageSize", defaultValue = "30") Integer pageSize,
//                              @RequestParam(value = "keyword", defaultValue = "") String keyword,
//                              @RequestParam(value = "lessonId", defaultValue = "") Long lessonId,
//                              @RequestParam(value = "teacherId", defaultValue = "") Long teacherId,
//                              @RequestParam(value = "studentId", defaultValue = "") Long studentId,
//                              @RequestParam(value = "onlyEvaluate", defaultValue = "true") Boolean onlyEvaluate,
//                              @RequestParam(value = "startDate", defaultValue = "") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
//                              @RequestParam(value = "endDate", defaultValue = "") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
//        LessonStudentParamDTO param = new LessonStudentParamDTO();
//        param.setPage(page);
//        param.setPageSize(pageSize);
//        param.setLessonId(lessonId);
//        param.setStudentId(studentId);
//        param.setEvaluateTeacherId(teacherId);
//        param.setStartDate(startDate);
//        param.setEndDate(endDate);
//        param.setKeyword(keyword);
//        param.setOnlyEvaluate(onlyEvaluate);
//        return page != null && page > 0 ?
//                JsonResponseUtil.paginate(lessonStudentService.getList(param)) :
//                lessonStudentService.getAll(param);
//    }
//
//    @ApiOperation("课后点评")
//    @Log(description = "课后点评", type = "课次管理")
//    @PostMapping("/evaluation")
//    @PreventMultiSubmit
//    public JsonResponse evaluation(@Valid @RequestBody LessonEvaluateSaveDTO dto, BindingResult result) {
//        CommonUtil.handleValidMessage(result);
//        if (lessonStudentService.evaluation(dto, StaffAuthService.getCurrentUserId())) {
//            return JsonResponseUtil.success();
//        } else {
//            return JsonResponseUtil.error("操作失败");
//        }
//    }
//
//    @ApiOperation("消课返还还原")
//    @Log(description = "消课返还还原", type = "课次管理")
//    @PostMapping("/rollbackCourseNum")
//    public JsonResponse rollbackCourseNum(@RequestBody List<Long> ids) {
//        if (lessonStudentService.rollbackCourseNum(ids, StaffAuthService.getCurrentUserId())) {
//            return JsonResponseUtil.success();
//        } else {
//            return JsonResponseUtil.error("操作失败");
//        }
//    }
//
//    @ApiOperation("删除上课记录")
//    @Log(description = "删除上课记录", type = "课次管理")
//    @PostMapping("/deleteLessonStudent")
//    public JsonResponse deleteLessonStudent(@RequestBody List<Long> ids) {
//        if (lessonStudentService.deleteLessonStudent(ids)) {
//            return JsonResponseUtil.success();
//        } else {
//            return JsonResponseUtil.error("操作失败");
//        }
//    }
//
//    @ApiOperation("调整消课数")
//    @Log(description = "调整消课数", type = "课次管理")
//    @PostMapping("/changeLessonCount")
//    public JsonResponse changeLessonCount(@RequestBody LessonStudent dto) {
//        if (lessonStudentService.changeLessonCount(dto)) {
//            return JsonResponseUtil.success();
//        } else {
//            return JsonResponseUtil.error("操作失败");
//        }
//    }
//}
