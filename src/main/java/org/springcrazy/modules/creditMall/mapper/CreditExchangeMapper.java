package org.springcrazy.modules.creditMall.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import com.hzb.erp.annotation.DataScoped;
import org.apache.ibatis.annotations.Mapper;
import org.springcrazy.modules.creditMall.entity.CreditExchange;
import org.springcrazy.modules.creditMall.pojo.CreditExchangeParamDTO;
import org.springcrazy.modules.creditMall.pojo.CreditExchangeVO;

/**
 * 积分兑换记录
 * @Entity com.hzb.erp.api.adminCenter.creditMall.entity.CreditExchange
 */
@Mapper
public interface CreditExchangeMapper extends BaseMapper<CreditExchange> {

    @DataScoped
    IPage<CreditExchangeVO> getList(Page<Object> objectPage, CreditExchangeParamDTO param);
}




