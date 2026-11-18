//package org.springcrazy.modules.lesson.controller;
//
//
//import org.springcrazy.core.annotation.Log;
//import org.springcrazy.modules.lesson.pojo.AppointmentParamDTO;
//import org.springcrazy.modules.lesson.pojo.AppointmentVO;
//import org.springcrazy.modules.lesson.service.AppointmentService;
//import org.springcrazy.common.pojo.PaginationVO;
//import com.hzb.erp.security.Util.UserAuthUtil;
//import org.springcrazy.common.tool.JsonResponse;
//import org.springcrazy.common.tool.JsonResponseUtil;
//import io.swagger.annotations.Api;
//import io.swagger.annotations.ApiOperation;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.format.annotation.DateTimeFormat;
//import org.springframework.web.bind.annotation.*;
//
//import java.time.LocalDate;
//import java.util.List;
//
///**
// * <p>
// * 试听预约记录 前端控制器
// * </p>
// *
// * @author Ryan
// */
//@RestController
//@RequestMapping("/common/appointment")
//@Api(value = "预约记录", tags = "预约记录")
//public class AppointmentController {
//
//    @Autowired
//    private AppointmentService appointmentService;
//
//    @ApiOperation("预约列表/试听申请记录")
//    @GetMapping("/list")
//    public PaginationVO list(@RequestParam(value = "page", defaultValue = "1") Integer page,
//                             @RequestParam(value = "pageSize", defaultValue = "30") Integer pageSize,
//                             @RequestParam(value = "studentId", defaultValue = "") Long studentId) {
//        AppointmentParamDTO param = new AppointmentParamDTO();
//        param.setPage(page);
//        param.setPageSize(pageSize);
//        param.setStudentId(studentId);
//        return JsonResponseUtil.paginate(appointmentService.getList(param));
//    }
//
//    @ApiOperation("确认预约")
//    @Log(description = "确认预约", type = "预约管理")
//    @PostMapping("/approve")
//    public JsonResponse approve(@RequestBody List<Long> ids) {
//        if (appointmentService.handleAudit(ids, true, UserAuthUtil.getCurrentUserId())) {
//            return JsonResponseUtil.success();
//        } else {
//            return JsonResponseUtil.error("操作失败");
//        }
//    }
//
//    @ApiOperation("取消预约")
//    @Log(description = "取消预约", type = "预约管理")
//    @PostMapping("/cancel")
//    public JsonResponse cancel(@RequestBody List<Long> ids) {
//        if (appointmentService.handleAudit(ids, false, UserAuthUtil.getCurrentUserId())) {
//            return JsonResponseUtil.success();
//        } else {
//            return JsonResponseUtil.error("操作失败");
//        }
//    }
//
//    @ApiOperation("预约日历")
//    @GetMapping("/calendar")
//    public List<AppointmentVO> calendar(
////            @RequestParam(value = "self", defaultValue = "false") Boolean self,
////            @RequestParam(value = "classIds", defaultValue = "") Long[] classIds,
////            @RequestParam(value = "teacherIds", defaultValue = "") Long[] teacherIds,
////            @RequestParam(value = "subjectId", defaultValue = "") Long subjectId,
////            @RequestParam(value = "roomId", defaultValue = "") Long roomId,
//            @RequestParam(value = "courseIds", defaultValue = "") Long[] courseIds,
//            @RequestParam(value = "startDate", defaultValue = "") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
//            @RequestParam(value = "endDate", defaultValue = "") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
//
//        AppointmentParamDTO param = new AppointmentParamDTO();
//        param.setCourseIds(courseIds);
//        param.setStartDate(startDate);
//        param.setEndDate(endDate);
//        return appointmentService.getAll(param);
//    }
//
//}
