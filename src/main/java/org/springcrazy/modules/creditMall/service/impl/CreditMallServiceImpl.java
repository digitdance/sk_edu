package org.springcrazy.modules.creditMall.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hzb.erp.common.entity.DictItem;

import com.hzb.erp.common.enums.SwitchEnum;
import com.hzb.erp.common.service.DictItemService;
import com.hzb.erp.common.service.DictService;
import com.hzb.erp.exception.BizException;
import com.hzb.erp.sysservice.enums.DictCodeEnum;
import com.hzb.erp.utils.EnumTools;


import org.springcrazy.common.enums.MaterialRecordTypeEnum;
import org.springcrazy.common.enums.StudentCreditChangeTypeEnum;
import org.springcrazy.core.mp.base.BaseServiceImpl;
import org.springcrazy.modules.creditMall.entity.CreditExchange;
import org.springcrazy.modules.creditMall.entity.CreditMall;
import org.springcrazy.modules.creditMall.mapper.CreditExchangeMapper;
import org.springcrazy.modules.creditMall.mapper.CreditMallMapper;
import org.springcrazy.modules.creditMall.pojo.CreditExchangeDTO;
import org.springcrazy.modules.creditMall.pojo.CreditMallParamDTO;
import org.springcrazy.modules.creditMall.pojo.CreditMallVO;
import org.springcrazy.modules.creditMall.service.CreditMallService;
import org.springcrazy.modules.material.entity.Material;
import org.springcrazy.modules.material.entity.MaterialRecord;
import org.springcrazy.modules.material.mapper.MaterialRecordMapper;
import org.springcrazy.modules.material.service.MaterialService;
import org.springcrazy.modules.student.entity.StudentCreditLog;
import org.springcrazy.modules.system.service.IDictService;
import org.springcrazy.modules.user.entity.Student;
import org.springcrazy.modules.user.service.IStudentService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 积分商城礼品
 */
@Service
public class CreditMallServiceImpl extends BaseServiceImpl<CreditMallMapper, CreditMall> implements CreditMallService {


    private DictService dictService;


    private DictItemService dictItemService;


    private MaterialService materialService;


    private IStudentService studentService;


    private CreditExchangeMapper creditExchangeMapper;


    private MaterialRecordMapper materialRecordMapper;


    @Override
    public IPage<CreditMallVO> getList(CreditMallParamDTO param) {
        param.setState(EnumTools.getCodeByDist(param.getStateText(), SwitchEnum.class));
        return this.baseMapper.getList(new Page<>(param.getPage(), param.getPageSize()), param);
    }

    @Override
    public List<Map<String, Object>> getAll(String keyword) {
        List<DictItem> categories = dictService.listItemByCode(DictCodeEnum.CREDIT_MALL_CATEGORY.getCode());

        List<Map<String, Object>> res = new ArrayList<>();
        for (DictItem cate : categories) {
            CreditMallParamDTO param = new CreditMallParamDTO();
            param.setCategoryId(cate.getId());
            param.setName(keyword);
            param.setCanExchange(true);
            List<CreditMallVO> giftList = this.baseMapper.getList(param);
            if(!CollectionUtils.isEmpty(giftList)) {
                Map<String, Object> map = new HashMap<>();
                map.put("category", cate.getName());
                map.put("list", giftList);
                res.add(map);
            }
        }
        return res;
    }

    @Override
    public Boolean saveOrUpdateByDTO(CreditMall dto) {
        CreditMall item = new CreditMall();
        BeanUtils.copyProperties(dto, item);
//
//        Material material = materialService.getById(item.getMaterialId());
//        item.setSchoolId(material.getSchoolId());
//        item.setCover(material.getCover());
//        item.setCategoryName(dictItemService.getNameById(item.getCategoryId()));
        return this.saveOrUpdate(item);
    }

    @Override
    public CreditMallVO getInfo(Long id) {
        return baseMapper.getInfo(id);
    }

    @Override
    public int addViewNum(Long id) {
        CreditMall item = this.baseMapper.selectById(id);
        item.setViewNum(item.getViewNum() + 1);
        return this.baseMapper.updateById(item);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean exchange(CreditExchangeDTO dto) {

        CreditMall creditMall = this.baseMapper.selectById(dto.getId());
        Material material = materialService.getByCreditMall(creditMall);
        if(material == null) {
            throw new BizException("物资无记录");
        } else if (!SwitchEnum.YES.equals(material.getState())) {
            throw new BizException("物资已下架");
        } else if (material.getStorage() == null || material.getStorage() < 1) {
            throw new BizException("缺少库存");
        } else if(material.getStorage() < dto.getNum()) {
            throw new BizException("库存不足");
        }

        int credit = creditMall.getCredit() * dto.getNum();

        Student student = studentService.getById(dto.getStudentId());
        if(credit > student.getCredit()) {
            throw new BizException("学生积分不足");
        }

        // 减少学生积分
        StudentCreditLog creditLog = new StudentCreditLog();
        creditLog.setStudentId(dto.getStudentId());
        creditLog.setCredit(credit);
        creditLog.setChangeType(StudentCreditChangeTypeEnum.CREDIT_EXCHANGE);
        creditLog.setSourceId(dto.getId());
        creditLog.setSchoolId(student.getSchoolId());
        creditLog.setRemark("兑换礼品:"+creditMall.getName() + ",数量" + dto.getNum());
        studentService.decCredit(creditLog);

        // 兑换记录
        CreditExchange exchangeData = new CreditExchange();
        exchangeData.setCreditMallId(creditMall.getId());
        exchangeData.setStudentId(dto.getStudentId());
        exchangeData.setUserId(dto.getUserId());
        exchangeData.setSchoolId(student.getSchoolId());
        exchangeData.setNum(dto.getNum());
        exchangeData.setCredit(credit);
        int res = creditExchangeMapper.insert(exchangeData);

        // 减少库存
        material.setStorage(material.getStorage() - dto.getNum());
        materialService.updateById(material);

        // 记录物资日志
        MaterialRecord materialRecord = new MaterialRecord();
        materialRecord.setMaterialId(material.getId());
        materialRecord.setAmount(-dto.getNum());
        materialRecord.setReason("积分兑换礼品");
        materialRecord.setChangeType(MaterialRecordTypeEnum.CREDIT_MALL);
        materialRecord.setChangeTargetId(exchangeData.getId());
        materialRecord.setStudentId(dto.getStudentId());
        materialRecord.setSchoolId(material.getSchoolId());
        materialRecordMapper.insert(materialRecord);

        return res > 0;
    }

    @Override
    public Boolean switchState(List<Long> ids, Integer switchEnum) {
        List<CreditMall> list = this.listByIds(ids);
        for (CreditMall item : list) {
            item.setState(switchEnum);
        }
        return this.updateBatchById(list);
    }
}




