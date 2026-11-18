package org.springcrazy.modules.clazz.controller;


import com.alibaba.fastjson.JSONObject;
import com.hzb.erp.annotation.Log;
import com.hzb.erp.annotation.PreventMultiSubmit;
import org.springcrazy.common.pojo.PaginationVO;
import org.springcrazy.common.tool.CommonUtil;
import org.springcrazy.common.tool.JsonResponse;
import org.springcrazy.common.tool.JsonResponseUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springcrazy.core.secure.utils.SecureUtil;
import org.springcrazy.core.tool.api.R;
import org.springcrazy.modules.clazz.entity.Clazz;
import org.springcrazy.modules.clazz.pojo.ClassParamDTO;
import org.springcrazy.modules.clazz.pojo.ClassSaveDTO;
import org.springcrazy.modules.clazz.pojo.ClassVO;
import org.springcrazy.modules.clazz.service.ClassStudentService;
import org.springcrazy.modules.clazz.service.ClazzService;
import org.springcrazy.modules.edu.dto.CourseDTO;
import org.springcrazy.modules.edu.entity.Course;
import org.springcrazy.modules.edu.service.ICourseService;
import org.springcrazy.modules.edu.vo.CourseVO;
import org.springcrazy.modules.student.pojo.StudentParamDTO;
import org.springcrazy.modules.user.service.IStudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * <p>
 * 班级 前端控制器
 * </p>
 *
 * @author 541720500@qq.com
 */
@RestController
@RequestMapping("/common/clazz")
@Api(value = "班级管理", tags = "班级管理")
public class ClazzController {

    @Autowired
    private ClazzService clazzService;

    @Autowired
    private ICourseService courseService;

//    @Autowired
//    private IStudentService studentService;


    private ClassStudentService classStudentService;

    @ApiOperation("班级列表")
    @GetMapping("/list")
    public PaginationVO list(@RequestParam(value = "page", defaultValue = "1") Integer page,
                             @RequestParam(value = "pageSize", defaultValue = "30") Integer pageSize,
                             @RequestParam(value = "courseId", defaultValue = "") Long courseId,
                             @RequestParam(value = "studentId", defaultValue = "") Long studentId,
                             @RequestParam(value = "gradeId", defaultValue = "") Long gradeId,
                             @RequestParam(value = "name", defaultValue = "") String name,
                             @RequestParam(value = "over", defaultValue = "false") Boolean over,
                             @RequestParam(value = "teacherId", defaultValue = "") Long teacherId) {
        ClassParamDTO param = new ClassParamDTO();
        param.setPage(page);
        param.setPageSize(pageSize);
        param.setTeacherId(teacherId);
        param.setStudentId(studentId);
        param.setGradeId(gradeId);
        param.setCourseId(courseId);
        param.setClassName(name);
        param.setOver(over);

        return JsonResponseUtil.paginate(clazzService.getList(param));
    }

    @ApiOperation("班级信息")
    @GetMapping("/info")
    public ClassVO info(@RequestParam(value = "id") Long id) {
        return clazzService.getInfo(id);
    }

    @ApiOperation("创建和修改班级")
    @Log(description = "创建和修改班级", type = "班级管理")
    @PostMapping("/submit")
    @PreventMultiSubmit
    public JsonResponse save(@Valid @RequestBody ClassSaveDTO classSaveDTO, BindingResult result) {


//        String deptId =  SecureUtil.getDeptId();
//        classSaveDTO.set
        CommonUtil.handleValidMessage(result);
        if (clazzService.saveOrUpdateByDTO(classSaveDTO)) {
            return JsonResponseUtil.success();
        } else {
            return JsonResponseUtil.error("提交失败");
        }
    }

    @ApiOperation("分数设置")
    @Log(description = "分数设置", type = "分数设置")
    @GetMapping("/scoreSetting")
    public JsonResponse scoreSetting(@RequestParam Integer clazzId) {
//        String deptId =  SecureUtil.getDeptId();
//        classSaveDTO.set
        Course course =  courseService.getById(clazzId);
        return JsonResponseUtil.data(JSONObject.parse(course.getScoreSetting()));
    }

    @ApiOperation("分数设置")
    @Log(description = "分数设置", type = "分数设置")
    @PostMapping("/scoreSetting")
    public JsonResponse scoreSetting( @RequestBody ClassSaveDTO classSaveDTO, BindingResult result) {

        Course course =  courseService.getById(classSaveDTO.getId());
        course.setScoreSetting(classSaveDTO.getScoreSetting());
        courseService.updateById(course);
        CommonUtil.handleValidMessage(result);
        return JsonResponseUtil.success();

    }


    @ApiOperation("删除班级")
    @Log(description = "删除班级", type = "班级管理")
    @PostMapping("/delete")
    public JsonResponse delete(@RequestBody List<Long> ids) {
        if (clazzService.removeByIds(ids)) {
            return JsonResponseUtil.success();
        } else {
            return JsonResponseUtil.error("删除失败");
        }
    }

    @ApiOperation("结业班级")
    @Log(description = "结业班级", type = "班级管理")
    @PostMapping("/over")
    public JsonResponse over(@RequestBody List<Long> ids) {
        if (clazzService.over(ids, Long.parseLong(SecureUtil.getUserId()+""))) {
            return JsonResponseUtil.success();
        } else {
            return JsonResponseUtil.error("删除失败");
        }
    }

//    @ApiOperation("班级学员列表")
//    @GetMapping("/studentList")
//    public PaginationVO studentList(@RequestParam(value = "page", defaultValue = "1") Integer page,
//                                    @RequestParam(value = "pageSize", defaultValue = "30") Integer pageSize,
//                                    @RequestParam(value = "classId") Long classId,
//                                    @RequestParam(value = "keyword", defaultValue = "") String keyword) {
//        StudentParamDTO param = new StudentParamDTO();
//        param.setPage(page);
//        param.setPageSize(pageSize);
//        param.setKeyword(keyword);
//        param.setClassId(classId);
//        return JsonResponseUtil.paginate(studentService.getListByClassId(param));
//    }

    @ApiOperation("一键添加学员")
    @Log(description = "一键添加学员", type = "班级管理")
    @PostMapping("/addStudentsOneKey")
    public JsonResponse addStudentsOneKey(@RequestBody Clazz clazz) {
        if (classStudentService.addStudentsOneKey(clazz.getId(), Long.parseLong(SecureUtil.getUserId()+""))) {
            return JsonResponseUtil.success("已添加");
        } else {
            return JsonResponseUtil.error("添加失败");
        }
    }

}
