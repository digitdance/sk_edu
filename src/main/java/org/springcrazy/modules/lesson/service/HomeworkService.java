package org.springcrazy.modules.lesson.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springcrazy.modules.lesson.entity.Homework;
import org.springcrazy.modules.lesson.pojo.HomeworkParamDTO;
import org.springcrazy.modules.lesson.pojo.HomeworkSaveDTO;
import org.springcrazy.modules.lesson.pojo.HomeworkVO;

import java.util.List;

/**
 * <p>
 * 作业 服务类
 * </p>
 *
 * @author Ryan
 */
public interface HomeworkService extends IService<Homework> {

    IPage<HomeworkVO> getList(HomeworkParamDTO param);

    List<HomeworkVO> getAll(HomeworkParamDTO param);

    Boolean saveOrUpdateByDTO(HomeworkSaveDTO dto);

    HomeworkVO getInfo(Long id, Long studentId);
}
