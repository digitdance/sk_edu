package org.springcrazy.modules.creditMall.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hzb.erp.common.enums.VerifyStateEnum;
import com.hzb.erp.common.service.SettingService;
import com.hzb.erp.utils.EnumTools;
import org.apache.commons.lang3.BooleanUtils;
import org.springcrazy.common.enums.SettingNameEnum;
import org.springcrazy.common.enums.StudentCreditChangeTypeEnum;

import org.springcrazy.common.pojo.AuditParamDTO;
import org.springcrazy.common.tool.CommonUtil;

import org.springcrazy.core.mp.base.BaseServiceImpl;
import org.springcrazy.modules.creditMall.entity.CreditExchange;
import org.springcrazy.modules.creditMall.mapper.CreditExchangeMapper;
import org.springcrazy.modules.creditMall.pojo.CreditExchangeParamDTO;
import org.springcrazy.modules.creditMall.pojo.CreditExchangeVO;
import org.springcrazy.modules.creditMall.service.CreditExchangeService;
import org.springcrazy.modules.student.entity.StudentCreditLog;
import org.springcrazy.modules.user.entity.Student;
import org.springcrazy.modules.user.service.IStudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 积分商城兑换记录
 */
@Service
public class CreditExchangeServiceImpl extends BaseServiceImpl<CreditExchangeMapper, CreditExchange> implements CreditExchangeService {

    private IStudentService studentService;


    private SettingService settingService;

    @Override
    public IPage<CreditExchangeVO> getList(CreditExchangeParamDTO param) {
        param.setVerifyState(EnumTools.getCodeByDist(param.getVerifyStateText(), VerifyStateEnum.class));

        IPage<CreditExchangeVO> list = this.baseMapper.getList(new Page<>(param.getPage(), param.getPageSize()), param);
//        Boolean marker = settingService.boolValue(SettingNameEnum.STUDENT_MOBILE_MARKER_SWITCH.getCode());
//        if(BooleanUtils.isNotFalse(marker) && CollectionUtils.isNotEmpty(list.getRecords())) {
//            for (CreditExchangeVO vo: list.getRecords()) {
//                vo.setMobile(CommonUtil.markMobile(vo.getMobile()));
//            }
//        }

        return list;
    }

    @Override
    public boolean exchangeAudit(AuditParamDTO auditDTO) {
        List<CreditExchange> list = this.listByIds(auditDTO.getIds());
        for (CreditExchange item : list) {
            if(!VerifyStateEnum.APPLY.equals(item.getVerifyState()) ) {
                continue;
            }
            item.setVerifyState(EnumTools.getByCode(auditDTO.getVerifyState(), VerifyStateEnum.class));
            item.setVerifyStaff(auditDTO.getStaffId());
            item.setVerifyTime(LocalDateTime.now());
            item.setVerifyRemark(auditDTO.getVerifyRemark());

            if(VerifyStateEnum.REJECT.getCode() == auditDTO.getVerifyState()) {
                Student student = studentService.getById(item.getStudentId());
                // 返还积分
                StudentCreditLog creditLog = new StudentCreditLog();
                creditLog.setStudentId(item.getStudentId());
                creditLog.setCredit(item.getCredit());
                creditLog.setChangeType(StudentCreditChangeTypeEnum.CREDIT_EXCHANGE_FAIL);
                creditLog.setSourceId(item.getId());
                creditLog.setSchoolId(student.getSchoolId());
                studentService.incCredit(creditLog);
            }
        }
        return this.updateBatchById(list);
    }

}
