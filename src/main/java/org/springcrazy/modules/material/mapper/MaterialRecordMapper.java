package org.springcrazy.modules.material.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import com.hzb.erp.annotation.DataScoped;
import org.apache.ibatis.annotations.Mapper;
import org.springcrazy.modules.material.entity.MaterialRecord;
import org.springcrazy.modules.material.pojo.MaterialRecordParamDTO;
import org.springcrazy.modules.material.pojo.MaterialRecordVO;

/**
 * @Entity com.hzb.erp.api.adminCenter.material.entity.MaterialRecord
 */
@Mapper
public interface MaterialRecordMapper extends BaseMapper<MaterialRecord> {
    @DataScoped
    IPage<MaterialRecordVO> getList(Page<Object> objectPage, MaterialRecordParamDTO param);
}




