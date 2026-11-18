package org.springcrazy.modules.clazz.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springcrazy.modules.clazz.entity.Classroom;
import org.springcrazy.modules.clazz.pojo.ClassroomParamDTO;
import org.springcrazy.modules.clazz.pojo.ClassroomSaveDTO;
import org.springcrazy.modules.clazz.pojo.ClassroomVO;

/**
 * <p>
 * 教室 服务类
 * </p>
 *
 * @author Ryan
 */
public interface ClassroomService extends IService<Classroom> {

    IPage<ClassroomVO> getList(ClassroomParamDTO param);

    Boolean saveOrUpdateByDTO(ClassroomSaveDTO dto);
}
