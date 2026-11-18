package org.springcrazy.modules.readtask.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springcrazy.core.mp.base.BaseService;
import org.springcrazy.modules.lesson.entity.Homework;
import org.springcrazy.modules.lesson.pojo.HomeworkParamDTO;
import org.springcrazy.modules.lesson.pojo.HomeworkSaveDTO;
import org.springcrazy.modules.lesson.pojo.HomeworkVO;
import org.springcrazy.modules.readtask.entity.ReadTask;
import org.springcrazy.modules.readtask.pojo.ReadTaskParamDTO;
import org.springcrazy.modules.readtask.pojo.ReadTaskVO;

import java.util.List;

/**
 * <p>
 * 作业 服务类
 * </p>
 *
 * @author Ryan
 */
public interface ReadTaskService extends BaseService<ReadTask> {

    IPage<ReadTaskVO> getList(ReadTaskParamDTO param);

    List<ReadTaskVO> getAll(ReadTaskParamDTO param);

    Boolean saveOrUpdateByDTO(ReadTaskParamDTO dto);

    ReadTaskVO getInfo(Long id, Long studentId);
}
