package org.springcrazy.modules.creditMall.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springcrazy.common.pojo.AuditParamDTO;
import org.springcrazy.core.mp.base.BaseService;
import org.springcrazy.modules.creditMall.entity.CreditExchange;
import org.springcrazy.modules.creditMall.pojo.CreditExchangeParamDTO;
import org.springcrazy.modules.creditMall.pojo.CreditExchangeVO;

/**
 *
 */
public interface CreditExchangeService extends BaseService<CreditExchange> {

    /**
     * 分页
     * */
    IPage<CreditExchangeVO> getList(CreditExchangeParamDTO param);


    /**
     * 审核
     * */
    boolean exchangeAudit(AuditParamDTO auditDTO);
}
