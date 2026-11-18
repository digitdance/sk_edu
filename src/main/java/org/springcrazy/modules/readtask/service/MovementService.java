package org.springcrazy.modules.readtask.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springcrazy.core.mp.base.BaseService;
import org.springcrazy.modules.readtask.entity.Movement;
import org.springcrazy.modules.readtask.entity.ReadTask;
import org.springcrazy.modules.readtask.pojo.MovementParamDTO;
import org.springcrazy.modules.readtask.pojo.MovementVO;
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
public interface MovementService extends BaseService<Movement> {
    IPage<MovementVO> getList(MovementParamDTO param);

    List<MovementVO> getAll(MovementParamDTO param);

    Boolean saveOrUpdateByDTO(MovementParamDTO dto);

    MovementVO getInfo(Long id);

}
