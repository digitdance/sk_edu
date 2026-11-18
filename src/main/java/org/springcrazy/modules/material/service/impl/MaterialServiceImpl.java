package org.springcrazy.modules.material.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
//import com.hzb.erp.common.enums.SwitchEnum;
//import com.hzb.erp.common.enums.SwitchEnum;
import com.beust.jcommander.internal.Nullable;
import org.springcrazy.common.enums.SwitchEnum;
import com.hzb.erp.common.service.DictItemService;
import com.hzb.erp.exception.BizException;

import org.apache.commons.lang3.BooleanUtils;
import org.springcrazy.common.enums.MaterialRecordTypeEnum;
import org.springcrazy.common.tool.EnumTools;
import org.springcrazy.core.mp.base.BaseServiceImpl;
import org.springcrazy.modules.creditMall.entity.CreditMall;
import org.springcrazy.modules.material.entity.Material;
import org.springcrazy.modules.material.entity.MaterialRecord;
import org.springcrazy.modules.material.mapper.MaterialMapper;
import org.springcrazy.modules.material.mapper.MaterialRecordMapper;
import org.springcrazy.modules.material.pojo.MaterialParamDTO;
import org.springcrazy.modules.material.pojo.MaterialStorageDTO;
import org.springcrazy.modules.material.pojo.MaterialVO;
import org.springcrazy.modules.material.service.MaterialService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.util.List;

/**
 * 物料
 */
@Service
public class MaterialServiceImpl extends BaseServiceImpl<MaterialMapper, Material> implements MaterialService {


    private DictItemService dictItemService;


    private MaterialRecordMapper materialRecordMapper;

    @Override
    public IPage<MaterialVO> getList(MaterialParamDTO param) {
        param.setState(EnumTools.getCodeByDist(param.getStateText(), SwitchEnum.class));
        return this.baseMapper.getList(new Page<>(param.getPage(), param.getPageSize()), param);
    }

    @Override
    public List<MaterialVO> getAll(MaterialParamDTO param) {
        return this.baseMapper.getList(param);
    }

    @Override
    public Boolean saveOrUpdateByDTO(Material dto) {
        Material item = new Material();
        BeanUtils.copyProperties(dto, item);
        //item.setCategoryName(dictItemService.getNameById(item.getCategoryId()));
        return this.saveOrUpdate(item);
    }

    @Override
    public MaterialVO getInfo(Long id) {
        return baseMapper.getInfo(id);
    }

    @Override
    public Material getByCreditMall(CreditMall creditMall) {
        if(creditMall.getMaterialId() == null) {
            return null;
        }
        return baseMapper.selectById(creditMall.getMaterialId());
    }

    @Override
    public Boolean switchState(List<Long> ids, SwitchEnum switchEnum) {
        List<Material> list = this.listByIds(ids);
        for (Material item : list) {
            item.setState(switchEnum);
        }
        return this.updateBatchById(list);
    }

    @Override
    public boolean handleStorage(MaterialStorageDTO dto) {

        Material material = this.baseMapper.selectById(dto.getId());
        if(material == null) {
            throw new BizException("未知物料");
        }
        int changeNum;
        MaterialRecordTypeEnum changeType;
        if( BooleanUtils.isTrue(dto.getInStorage())) {
            changeNum = Math.abs(dto.getStorage());
            changeType = MaterialRecordTypeEnum.IN;
        } else {
            changeNum = - Math.abs(dto.getStorage());
            changeType = MaterialRecordTypeEnum.OUT;
        }

        if(changeNum == 0) {
            throw new BizException("数量不能为0");
        }

        // 减少库存
        material.setStorage(material.getStorage() + changeNum);
        this.baseMapper.updateById(material);

        // 记录物资日志
        MaterialRecord materialRecord = new MaterialRecord();
        materialRecord.setMaterialId(material.getId());
        materialRecord.setAmount(changeNum);
        materialRecord.setReason(dto.getReason());
        materialRecord.setChangeType(changeType);
        materialRecord.setSchoolId(material.getSchoolId());
        materialRecordMapper.insert(materialRecord);

        return true;
    }
}




