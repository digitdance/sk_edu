package org.springcrazy.modules.clazz.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.springcrazy.modules.clazz.entity.ClassStudent;
import org.springcrazy.modules.clazz.pojo.ClassStudentAddDTO;
import org.springcrazy.modules.clazz.pojo.ClassStudentParamDTO;
import org.springcrazy.modules.clazz.pojo.ClassStudentSignVO;
import org.springcrazy.modules.user.entity.Student;


import java.util.List;

/**
 * <p>
 * 班级学员表 服务类
 * </p>
 *
 * @author 541720500@qq.com
 */
public interface ClassStudentService extends IService<ClassStudent> {

//    IPage<ClassStudentVO> getList(ClassStudentParamDTO param);
//
//    List<ClassStudentVO> getAll(ClassStudentParamDTO param);
    /**
     * 查询班级学员签到信息、消课名称等
     */
    List<ClassStudentSignVO> signRecord(ClassStudentParamDTO param);
    /**
    * 删除学员
    * */
    Boolean deleteClassStudent(Long classId, Long studentId);

    /**
     * 删除学员 根据学生id列表删除
     * */
    int deleteClassStudent(List<Long> studentIds);

    /**
     * 从班级移除学员
     * @param ids classStudent的id
     * @return Boolean
     */
    Boolean deleteClassStudentByIds(List<Long> ids);

    /**
     * 批量调班
     * @param ids classStudent的id
     * @return Boolean
     */
    Boolean changeClassByIds(List<Long> ids, Long newClassId, Long staffId);

    Boolean addClassStudent(Long classId, Long studentId, Long staffId);

    void addClassStudents(ClassStudentAddDTO dto, Long staffId);

    List<Student> getStudentsByClassId(Long classId);

    boolean changeConsumeCourse(Long classStudentId, Long consumeCourseId);

    /**
     * 一键添加学员，通过班级相关的课程全部添加学员
     * */
    boolean addStudentsOneKey(Long classId, Long currentUserId);
}
