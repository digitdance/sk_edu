package org.springcrazy.modules.clazz.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springcrazy.modules.clazz.entity.Clazz;
import org.springcrazy.modules.clazz.pojo.ClassParamDTO;
import org.springcrazy.modules.clazz.pojo.ClassSaveDTO;
import org.springcrazy.modules.clazz.pojo.ClassVO;
import org.springcrazy.modules.edu.entity.Course;
import org.springcrazy.modules.user.entity.Student;

import java.util.List;

/**
 * <p>
 * 班级 服务类
 * </p>
 *
 * @author 541720500@qq.com
 */
public interface ClazzService extends IService<Clazz> {
    IPage<ClassVO> getList(ClassParamDTO param);

    List<ClassVO> getAll(ClassParamDTO param);

    ClassVO getInfo(Long id);

    Boolean saveOrUpdateByDTO(ClassSaveDTO classSaveDTO);

    /**
     * 结业
     */
    Boolean over(List<Long> ids, Long operatorId);

    /**
     * 1对1自动生成班级
     */
    Clazz autoCreateOne2One(Student student, Course course, Long teacherId);
}
