//package org.springcrazy.modules.lesson.controller;
//
//
//import com.baomidou.mybatisplus.core.metadata.IPage;
//import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
//import com.baomidou.mybatisplus.core.toolkit.StringUtils;
//import org.springcrazy.core.annotation.Log;
//import org.springcrazy.core.annotation.PreventMultiSubmit;
//import com.hzb.erp.api.base.service.StaffAuthService;
//import org.springcrazy.modules.clazz.pojo.ClassStudentParamDTO;
//import org.springcrazy.modules.clazz.pojo.ClassStudentSignVO;
//import org.springcrazy.modules.clazz.service.ClassStudentService;
//import org.springcrazy.modules.lesson.pojo.*;
//import org.springcrazy.modules.lesson.service.LessonService;
//import org.springcrazy.modules.lesson.service.TeachEvaluationService;
//import com.hzb.erp.common.enums.SignStateEnum;
//import org.springcrazy.common.pojo.PaginationVO;
//import com.hzb.erp.common.service.SettingService;
//import com.hzb.erp.sysservice.enums.SettingNameEnum;
//import com.hzb.erp.utils.*;
//import io.swagger.annotations.Api;
//import io.swagger.annotations.ApiOperation;
//import org.apache.commons.lang3.BooleanUtils;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.format.annotation.DateTimeFormat;
//import org.springframework.validation.BindingResult;
//import org.springframework.web.bind.annotation.*;
//
//import javax.annotation.Resource;
//import javax.validation.Valid;
//import java.time.LocalDate;
//import java.util.List;
//
///**
// * <p>
// * 课次表 前端控制器
// * </p>
// *
// * @author 541720500@qq.com
// */
//@RestController
//@RequestMapping("/common/lesson")
//@Api(value = "课次管理", tags = "课次管理")
//public class LessonController {
//
//    @Autowired
//    private LessonService lessonService;
//    @Autowired
//    private ClassStudentService classStudentService;
//    @Autowired
//    private TeachEvaluationService teachEvaluationService;
//    @Resource
//    private ISettingService settingService;
//
//    @ApiOperation("课次信息")
//    @GetMapping("/info")
//    public LessonVO info(@RequestParam("id") Long id) {
//        return lessonService.getInfo(id);
//    }
//
//    @ApiOperation("课表")
//    @GetMapping("/list")
//    public Object list(@RequestParam(value = "page", defaultValue = "") Integer page, // 若为null则是查全部
//                       @RequestParam(value = "pageSize", defaultValue = "30") Integer pageSize,
//                       @RequestParam(value = "onTrial", defaultValue = "") Integer onTrial,
//                       @RequestParam(value = "courseIds", defaultValue = "") Long[] courseIds,
//                       @RequestParam(value = "classIds", defaultValue = "") Long[] classIds,
//                       @RequestParam(value = "teacherIds", defaultValue = "") Long[] teacherIds,
//                       @RequestParam(value = "studentIds", defaultValue = "") Long[] studentIds,
//                       @RequestParam(value = "subjectId", defaultValue = "") Long subjectId,
//                       @RequestParam(value = "roomId", defaultValue = "") Long roomId,
//                       @RequestParam(value = "dateRange", defaultValue = "") String dateRange,
//                       @RequestParam(value = "ids", required = false) Long[] ids,
//                       @RequestParam(value = "export", defaultValue = "false") Boolean export,
//                       @RequestParam(value = "startDate", defaultValue = "") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
//                       @RequestParam(value = "endDate", defaultValue = "") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
//                       @RequestParam(value = "studentId", defaultValue = "") Long studentId) {
//        LessonParamDTO param = new LessonParamDTO();
//        param.setPage(page);
//        param.setPageSize(pageSize);
//        param.setStudentId(studentId);
//        param.setCourseIds(courseIds);
//        param.setClassIds(classIds);
//        param.setTeacherIds(teacherIds);
//        param.setStudentIds(studentIds);
//        param.setSubjectId(subjectId);
//        param.setRoomId(roomId);
//        param.setStartDate(startDate);
//        param.setEndDate(endDate);
//        if (StringUtils.isNotBlank(dateRange) && startDate == null && endDate == null) {
//            LocalDate[] dates = DateTool.startAndEndDateByRange(dateRange);
//            param.setStartDate(dates[0]);
//            param.setEndDate(dates[1]);
//        }
//        if (ids != null && ids.length > 0) {
//            param.setIds(ids);
//        }
//        if (onTrial != null) {
//            param.setOnTrial(onTrial);
//        }
//        if (export != null && export) {
//            lessonService.exportLessonData(param);
//            return null;
//        }
//        return page != null && page > 0 ?
//                JsonResponseUtil.paginate(lessonService.getList(param)) :
//                lessonService.getAll(param);
//    }
//
//    @ApiOperation("创建和修改课次")
//    @Log(description = "创建和修改课次", type = "课次管理")
//    @PostMapping("/save")
//    @PreventMultiSubmit
//    public JsonResponse save(@Valid @RequestBody LessonSaveDTO dto, BindingResult result) {
//        CommonUtil.handleValidMessage(result);
//        Integer res = lessonService.saveOrUpdateByDTO(dto);
//        if (res > 0) {
//            return JsonResponseUtil.success(dto.getId() == null ? "完成排课" + res + "次" : "已更新");
//        } else {
//            return JsonResponseUtil.error("操作失败");
//        }
//    }
//
//    @ApiOperation("重复创建和修改课次")
//    @Log(description = "重复创建和修改课次", type = "课次管理")
//    @PostMapping("/batchSave")
//    @PreventMultiSubmit
//    public JsonResponse batchSave(@Valid @RequestBody LessonBatchSaveDTO dto, BindingResult result) {
//        CommonUtil.handleValidMessage(result);
//        Integer res = lessonService.batchSaveByDTO(dto);
//        if (res > 0) {
//            return JsonResponseUtil.success("完成排课" + res + "次");
//        } else {
//            return JsonResponseUtil.error("操作失败");
//        }
//    }
//
//    @ApiOperation("快速添加课次")
//    @Log(description = "快速添加课次", type = "课次管理")
//    @PostMapping("/saveQuick")
//    @PreventMultiSubmit
//    public JsonResponse saveQuick(@Valid @RequestBody LessonSaveQuicklyDTO dto, BindingResult result) {
//        CommonUtil.handleValidMessage(result);
//        Integer res = lessonService.createQuickly(dto, StaffAuthService.getCurrentUserId());
//        if (res > 0) {
//            return JsonResponseUtil.success("完成排课" + res + "次");
//        } else {
//            return JsonResponseUtil.error("操作失败");
//        }
//    }
//
//    @ApiOperation("批量修改课次")
//    @Log(description = "批量修改课次", type = "课次管理")
//    @PostMapping("/updateLessonBatch")
//    @PreventMultiSubmit
//    public JsonResponse updateLessonBatch(@RequestBody LessonSaveDTO dto) {
//        if (lessonService.updateLessonBatch(dto)) {
//            return JsonResponseUtil.success("已修改");
//        } else {
//            return JsonResponseUtil.error("操作失败");
//        }
//    }
//
//    @ApiOperation("课表日历")
//    @GetMapping("/calendar")
//    public List<LessonVO> calendar(
//            @RequestParam(value = "self", defaultValue = "false") Boolean self,
//            @RequestParam(value = "courseIds", defaultValue = "") Long[] courseIds,
//            @RequestParam(value = "classIds", defaultValue = "") Long[] classIds,
//            @RequestParam(value = "teacherIds", defaultValue = "") Long[] teacherIds,
//            @RequestParam(value = "subjectId", defaultValue = "") Long subjectId,
//            @RequestParam(value = "roomId", defaultValue = "") Long roomId,
//            @RequestParam(value = "startDate", defaultValue = "") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
//            @RequestParam(value = "endDate", defaultValue = "") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
//
//        LessonParamDTO param = new LessonParamDTO();
//        param.setCourseIds(courseIds);
//        param.setClassIds(classIds);
//        param.setTeacherIds(teacherIds);
//        param.setSubjectId(subjectId);
//        param.setRoomId(roomId);
//        param.setStartDate(startDate);
//        param.setEndDate(endDate);
//        if (self != null && self) {
//            param.setTeacherId(StaffAuthService.getCurrentUserId());
//        }
//
//        return lessonService.getAll(param);
//    }
//
//    @ApiOperation("月课时费统计")
//    @GetMapping("/statisByTeachers")
//    public PaginationVO statisByTeachers(
//            @RequestParam(value = "page", defaultValue = "") Integer page,
//            @RequestParam(value = "pageSize", defaultValue = "30") Integer pageSize,
//            @RequestParam(value = "teacherId", defaultValue = "") Long teacherId,
//            @RequestParam(value = "export", defaultValue = "false") Boolean export,
//            @RequestParam(value = "startDate", defaultValue = "") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
//            @RequestParam(value = "endDate", defaultValue = "") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
//
//        LessonParamDTO param = new LessonParamDTO();
//        param.setPage(page);
//        param.setPageSize(pageSize);
//        param.setTeacherId(teacherId);
//        param.setStartDate(startDate);
//        param.setEndDate(endDate);
//        if (export != null && export) {
//            lessonService.exportStatisData(param);
//            return null;
//        }
//        return JsonResponseUtil.paginate(lessonService.statsByTeachers(param));
//    }
//
//    @ApiOperation("消课费统计")
//    @GetMapping("/statisDecLesson")
//    public PaginationVO statisDecLesson(
//            @RequestParam(value = "page", defaultValue = "") Integer page,
//            @RequestParam(value = "pageSize", defaultValue = "30") Integer pageSize,
//            @RequestParam(value = "teacherId", defaultValue = "") Long teacherId,
//            @RequestParam(value = "export", defaultValue = "false") Boolean export,
//            @RequestParam(value = "startDate", defaultValue = "") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
//            @RequestParam(value = "endDate", defaultValue = "") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
//        LessonParamDTO param = new LessonParamDTO();
//        param.setPage(page);
//        param.setPageSize(pageSize);
//        param.setTeacherId(teacherId);
//        param.setStartDate(startDate);
//        param.setEndDate(endDate);
//        if (export != null && export) {
//            lessonService.exportStatisDecLesson(param);
//            return null;
//        }
//        return JsonResponseUtil.paginate(lessonService.statisDecLesson(param));
//    }
//
//    @ApiOperation("删除排课生成的课表")
//    @Log(description = "删除排课生成的课表", type = "课次管理")
//    @PostMapping("/deleteBySchedule")
//    public JsonResponse deleteBySchedule(@RequestBody List<Long> ids) {
//        if (lessonService.deleteBySchedule(ids)) {
//            return JsonResponseUtil.success();
//        } else {
//            return JsonResponseUtil.error("操作失败");
//        }
//    }
//
//    @ApiOperation("删除课次")
//    @Log(description = "删除课次", type = "课次管理")
//    @PostMapping("/delete")
//    public JsonResponse delete(@RequestBody List<Long> ids) {
//        if (lessonService.deleteLesson(ids)) {
//            return JsonResponseUtil.success();
//        } else {
//            return JsonResponseUtil.error("操作失败");
//        }
//    }
//
//    @ApiOperation("停课")
//    @Log(description = "停课", type = "课次管理")
//    @PostMapping("/stop")
//    public JsonResponse stop(@RequestBody List<Long> ids) {
//        if (lessonService.stopLesson(ids)) {
//            return JsonResponseUtil.success();
//        } else {
//            return JsonResponseUtil.error("操作失败");
//        }
//    }
//
//    @ApiOperation("恢复上课")
//    @Log(description = "恢复上课", type = "课次管理")
//    @PostMapping("/reopen")
//    public JsonResponse reopen(@RequestBody List<Long> ids) {
//        if (lessonService.reopenLesson(ids)) {
//            return JsonResponseUtil.success();
//        } else {
//            return JsonResponseUtil.error("操作失败");
//        }
//    }
//
//    @ApiOperation("关闭预约")
//    @Log(description = "关闭预约", type = "课次管理")
//    @PostMapping("/stopBooking")
//    public JsonResponse stopBooking(@RequestBody List<Long> ids) {
//        if (lessonService.handleBooking(ids, false)) {
//            return JsonResponseUtil.success();
//        } else {
//            return JsonResponseUtil.error("操作失败");
//        }
//    }
//
//    @ApiOperation("开启预约")
//    @Log(description = "开启预约", type = "课次管理")
//    @PostMapping("/openBooking")
//    public JsonResponse openBooking(@RequestBody List<Long> ids) {
//        if (lessonService.handleBooking(ids, true)) {
//            return JsonResponseUtil.success();
//        } else {
//            return JsonResponseUtil.error("操作失败");
//        }
//    }
//
//    @ApiOperation("课堂点名签到学员列表")
//    @GetMapping("/rollCallStudent")
//    public List<ClassStudentSignVO> rollCallStudent(
//            @RequestParam(value = "lessonId", defaultValue = "") Long lessonId) {
//        ClassStudentParamDTO param = new ClassStudentParamDTO();
//        param.setLessonId(lessonId);
//        List<ClassStudentSignVO> records = classStudentService.signRecord(param);
//        Boolean marker = settingService.boolValue(SettingNameEnum.STUDENT_MOBILE_MARKER_SWITCH.getCode());
//        if(BooleanUtils.isNotFalse(marker) && CollectionUtils.isNotEmpty(records)) {
//            for (ClassStudentSignVO vo: records) {
//                vo.setMobile(CommonUtil.markMobile(vo.getMobile()));
//            }
//        }
//        return records;
//    }
//
//    @ApiOperation("点名")
//    @Log(description = "点名", type = "课次管理")
//    @PreventMultiSubmit
//    @PostMapping("/rollCallBatch")
//    public JsonResponse rollCallBatch(@Valid @RequestBody List<LessonSignSaveDTO> dtoList, BindingResult result) {
//
//        CommonUtil.handleValidMessage(result);
//
//        for (LessonSignSaveDTO item : dtoList) {
//            SignStateEnum byCode = EnumTools.getByCode(item.getState(), SignStateEnum.class);
//            if (byCode == null) {
//                return JsonResponseUtil.error("点名状态有误");
//            }
//        }
//        Long teacherId = StaffAuthService.getCurrentUserId();
//        lessonService.rollCallBatch(teacherId, dtoList);
//        return JsonResponseUtil.success();
//    }
//
//    @ApiOperation("学评教")
//    @GetMapping("/teachEvaluate")
//    public PaginationVO teachEvaluate(@RequestParam(value = "page", defaultValue = "") Integer page, // 若为null则是查全部
//                                      @RequestParam(value = "pageSize", defaultValue = "30") Integer pageSize,
//                                      @RequestParam(value = "teacherId", defaultValue = "") Long teacherId,
//                                      @RequestParam(value = "lessonId", defaultValue = "") Long lessonId,
//                                      @RequestParam(value = "studentId", defaultValue = "") Long studentId,
//                                      @RequestParam(value = "startDate", defaultValue = "") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
//                                      @RequestParam(value = "endDate", defaultValue = "") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
//        LessonParamDTO param = new LessonParamDTO();
//        param.setPage(page);
//        param.setPageSize(pageSize);
//        param.setStudentId(studentId);
//        param.setLessonId(lessonId);
//        param.setTeacherId(teacherId);
//        param.setStartDate(startDate);
//        param.setEndDate(endDate);
//
//        IPage<TeachEvaluationVO> records = teachEvaluationService.getList(param);
//        for (TeachEvaluationVO vo : records.getRecords()) {
//            if (vo.getAnonymity() != null && vo.getAnonymity()) {
//                vo.setStudentName("***");
//            }
//        }
//        return JsonResponseUtil.paginate(records);
//    }
//
//    @ApiOperation("添加随课生")
//    @Log(description = "添加随课生", type = "课次管理")
//    @PostMapping("/addStudents")
//    public JsonResponse addStudents(@RequestBody LessonStudentAddDTO dto) {
//        if (lessonService.addStudents(dto, StaffAuthService.getCurrentUserId())) {
//            return JsonResponseUtil.success();
//        } else {
//            return JsonResponseUtil.error();
//        }
//    }
//}
