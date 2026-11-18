package org.springcrazy.modules.material.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import com.hzb.erp.annotation.DataScoped;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springcrazy.modules.material.entity.Material;
import org.springcrazy.modules.material.pojo.MaterialParamDTO;
import org.springcrazy.modules.material.pojo.MaterialVO;

import java.util.List;

/**
 * @Entity com.hzb.erp.api.adminCenter.material.entity.Material
 */
@Mapper
public interface MaterialMapper extends BaseMapper<Material> {
    @DataScoped
    IPage<MaterialVO> getList(Page<?> page, MaterialParamDTO param);

    @DataScoped
    List<MaterialVO> getList(@Param("param") MaterialParamDTO param);

    MaterialVO getInfo(Long id);
}




