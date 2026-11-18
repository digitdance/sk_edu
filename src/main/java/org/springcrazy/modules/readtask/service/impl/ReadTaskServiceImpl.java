package org.springcrazy.modules.readtask.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springcrazy.core.mp.base.BaseService;
import org.springcrazy.core.mp.base.BaseServiceImpl;
import org.springcrazy.modules.lesson.entity.Homework;
import org.springcrazy.modules.lesson.mapper.HomeworkMapper;
import org.springcrazy.modules.lesson.pojo.HomeworkParamDTO;
import org.springcrazy.modules.lesson.pojo.HomeworkSaveDTO;
import org.springcrazy.modules.lesson.pojo.HomeworkVO;
import org.springcrazy.modules.readtask.entity.ReadTask;
import org.springcrazy.modules.readtask.mapper.ReadTaskMapper;
import org.springcrazy.modules.readtask.pojo.ReadTaskParamDTO;
import org.springcrazy.modules.readtask.pojo.ReadTaskVO;
import org.springcrazy.modules.readtask.service.ReadTaskService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 作业 服务实现类
 * </p>
 *
 * @author Ryan
 */
@Service
public class ReadTaskServiceImpl extends BaseServiceImpl<ReadTaskMapper, ReadTask> implements ReadTaskService {

    @Override
    public IPage<ReadTaskVO> getList(ReadTaskParamDTO param) {
        return this.baseMapper.getList(new Page<>(param.getPage(), param.getPageSize()), param);
    }

    @Override
    public List<ReadTaskVO> getAll(ReadTaskParamDTO param) {
        return this.baseMapper.getList(param);
    }

    @Override
    public Boolean saveOrUpdateByDTO(ReadTaskParamDTO dto) {
        ReadTask item = new ReadTask();
        BeanUtils.copyProperties(dto, item);
        return this.saveOrUpdate(item);
    }

    @Override
    public ReadTaskVO getInfo(Long id, Long studentId) {
        return baseMapper.getInfo(id, studentId);
    }

}
