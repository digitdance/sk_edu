package org.springcrazy.modules.readtask.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springcrazy.core.mp.base.BaseServiceImpl;
import org.springcrazy.modules.readtask.entity.Movement;
import org.springcrazy.modules.readtask.entity.ReadTask;
import org.springcrazy.modules.readtask.mapper.MovementMapper;
import org.springcrazy.modules.readtask.mapper.ReadTaskMapper;
import org.springcrazy.modules.readtask.pojo.MovementParamDTO;
import org.springcrazy.modules.readtask.pojo.MovementVO;
import org.springcrazy.modules.readtask.pojo.ReadTaskParamDTO;
import org.springcrazy.modules.readtask.pojo.ReadTaskVO;
import org.springcrazy.modules.readtask.service.MovementService;
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
public class MovementServiceImpl extends BaseServiceImpl<MovementMapper, Movement> implements MovementService {


    @Override
    public IPage<MovementVO> getList(MovementParamDTO param) {
        return this.baseMapper.getList(new Page<>(param.getPage(), param.getPageSize()), param);
    }

    @Override
    public List<MovementVO> getAll(MovementParamDTO param) {
        return this.baseMapper.getList(param);
    }

    @Override
    public Boolean saveOrUpdateByDTO(MovementParamDTO dto) {
        Movement item = new Movement();
        BeanUtils.copyProperties(dto, item);
        return this.saveOrUpdate(item);
    }

    @Override
    public MovementVO getInfo(Long id) {
        return baseMapper.getInfo(id);
    }

}
