//package org.springcrazy.common.service;
//
//
//import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
//import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
//import com.hzb.erp.common.entity.DataPermission;
//import com.hzb.erp.common.entity.DataPermissionCustom;
//import com.hzb.erp.common.enums.DataScopeTypeEnum;
//import com.hzb.erp.exception.BizException;
//import com.hzb.erp.common.mapper.DataPermissionCustomMapper;
//import com.hzb.erp.common.mapper.DataPermissionMapper;
//import com.hzb.erp.common.pojo.DataPermissionSaveDTO;
//import org.apache.commons.lang3.StringUtils;
//import org.springcrazy.common.enums.DataScopeEntityEnum;
//import org.springframework.beans.BeanUtils;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import javax.annotation.Resource;
//import java.util.List;
//
///**
// * 数据权限
// */
//@Service
//public class DataPermissionServiceImpl extends ServiceImpl<DataPermissionMapper, DataPermission> implements DataPermissionService {
//
//
//    private DataPermissionCustomMapper dataPermissionCustomMapper;
//
//
//    public List<DataPermission> listByPositionId(Long positionId) {
//
//        QueryWrapper<DataPermission> qw = new QueryWrapper<>();
//        qw.eq("position_id", positionId);
//        List<DataPermission> list = list(qw);
//        for (DataPermission dp : list) {
//            QueryWrapper<DataPermissionCustom> qw1 = new QueryWrapper<>();
//            qw1.eq("permission_id", dp.getId());
//            dp.setCustomList(dataPermissionCustomMapper.selectList(qw1));
//        }
//        return list;
//    }
//
//    @Override
//    @Transactional(rollbackFor = Exception.class)
//    public boolean saveOrUpdateByDTO(DataPermissionSaveDTO dto) {
//        if (DataScopeTypeEnum.CUSTOM.equals(dto.getScopeType())) {
//            if (dto.getOrgIds().size() == 0) {
//                throw new BizException("自定义模式下未选择机构");
//            }
//        }
//
//        DataPermission item = new DataPermission();
//        BeanUtils.copyProperties(dto, item);
//
//        DataScopeEntityEnum enumItem = null;
//        for (DataScopeEntityEnum entityEnum : DataScopeEntityEnum.values()) {
//            if (entityEnum.getCode().equals(item.getEntityName())) {
//                enumItem = entityEnum;
//                break;
//            }
//        }
//        if(enumItem == null) {
//            throw new BizException("未知数据实体");
//        }
//        item.setInfo(enumItem.getDist());
//
//        if (StringUtils.isBlank(item.getOwnerField())) {
//            item.setOwnerField(enumItem.getDefaultField());
//        }
//
//        if (StringUtils.isBlank(item.getOwnerOrgField())) {
//            item.setOwnerOrgField(enumItem.getDefaultOrgField());
//        }
//
//        if(StringUtils.isBlank(item.getOwnerField()) && enumItem.getDefaultField() == null) {
//            throw new BizException("该默认数据负责人字段不支持设置为自己的数据");
//        }
//
////        if(StringUtils.isBlank(item.getOwnerField())
////                && "school_id".equals(item.getOwnerOrgField())
////                && !(DataScopeTypeEnum.ALL.equals(dto.getScopeType())
////                || DataScopeTypeEnum.GROUP.equals(dto.getScopeType()) || DataScopeTypeEnum.COM.equals(dto.getScopeType()))) {
////            throw new BizException("该数据实体仅支持按'集团数据'和'本校数据'");
////        }
//
//        boolean res = saveOrUpdate(item);
//        if (DataScopeTypeEnum.CUSTOM.equals(dto.getScopeType())) {
//            QueryWrapper<DataPermissionCustom> qw = new QueryWrapper<>();
//            qw.eq("permission_id", item.getId());
//            dataPermissionCustomMapper.delete(qw);
//            for (Long orgId : dto.getOrgIds()) {
//                DataPermissionCustom dpc = new DataPermissionCustom();
//                dpc.setPermissionId(item.getId());
//                dpc.setPositionId(item.getPositionId());
//                dpc.setOrgId(orgId);
//                dataPermissionCustomMapper.insert(dpc);
//            }
//        }
//
//        return res;
//    }
//
//    @Override
//    @Transactional(rollbackFor = Exception.class)
//    public boolean handleDel(List<Long> ids) {
//        for (Long pid : ids) {
//            QueryWrapper<DataPermissionCustom> qw = new QueryWrapper<>();
//            qw.eq("permission_id", pid);
//            dataPermissionCustomMapper.delete(qw);
//        }
//        return removeByIds(ids);
//    }
//
//    @Override
//    public DataPermission getInfo(Long id) {
//        DataPermission item = getById(id);
//        QueryWrapper<DataPermissionCustom> qw = new QueryWrapper<>();
//        qw.eq("permission_id", id);
//        item.setCustomList(dataPermissionCustomMapper.selectList(qw));
//        return item;
//    }
//}
