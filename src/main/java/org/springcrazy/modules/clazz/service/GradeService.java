package org.springcrazy.modules.clazz.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springcrazy.modules.clazz.entity.Grade;
import org.springcrazy.modules.clazz.pojo.GradeParamDTO;
import org.springcrazy.modules.clazz.pojo.GradeSaveDTO;
import org.springcrazy.modules.clazz.pojo.GradeVO;

import java.util.List;

/**
 * <p>
 * 成绩单 服务类
 * </p>
 *
 * @author Ryan
 */
public interface GradeService extends IService<Grade> {
    IPage<GradeVO> getList(GradeParamDTO param);

    List<GradeVO> getAll(GradeParamDTO param);

    Boolean saveOrUpdateByDTO(GradeSaveDTO dto);
}
