package org.springcrazy.modules.material.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hzb.erp.utils.EnumTools;
import org.springcrazy.common.enums.MaterialRecordTypeEnum;
import org.springcrazy.core.mp.base.BaseServiceImpl;
import org.springcrazy.modules.material.entity.MaterialRecord;
import org.springcrazy.modules.material.mapper.MaterialRecordMapper;
import org.springcrazy.modules.material.pojo.MaterialRecordParamDTO;
import org.springcrazy.modules.material.pojo.MaterialRecordVO;
import org.springcrazy.modules.material.service.MaterialRecordService;
import org.springframework.stereotype.Service;

/**
 * 物料出入库记录
 */
@Service
public class MaterialRecordServiceImpl extends BaseServiceImpl<MaterialRecordMapper, MaterialRecord> implements MaterialRecordService {
    @Override
    public IPage<MaterialRecordVO> getList(MaterialRecordParamDTO param) {
        //param.setChangeType(EnumTools.getCodeByDist(param.getChangeTypeText(), MaterialRecordTypeEnum.class));
        return this.baseMapper.getList(new Page<>(param.getPage(), param.getPageSize()), param);
    }
}




