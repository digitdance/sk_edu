package org.springcrazy.modules.readtask.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hzb.erp.annotation.DataScoped;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springcrazy.modules.readtask.entity.Movement;
import org.springcrazy.modules.readtask.entity.ReadTask;
import org.springcrazy.modules.readtask.pojo.MovementParamDTO;
import org.springcrazy.modules.readtask.pojo.MovementVO;
import org.springcrazy.modules.readtask.pojo.ReadTaskParamDTO;
import org.springcrazy.modules.readtask.pojo.ReadTaskVO;

import java.util.List;

/**
 * <p>
 * 作业 Mapper 接口
 * </p>
 *
 * @author Ryan
 */
@Mapper
public interface MovementMapper extends BaseMapper<Movement> {

    @DataScoped
    IPage<MovementVO> getList(Page<?> page, MovementParamDTO param);

    @DataScoped
    List<MovementVO> getList(@Param("param") MovementParamDTO param);


    MovementVO getInfo(Long id);

}
