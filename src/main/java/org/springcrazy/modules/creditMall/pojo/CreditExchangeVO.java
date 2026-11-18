package org.springcrazy.modules.creditMall.pojo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springcrazy.modules.creditMall.entity.CreditExchange;

/**
 * 兑换记录
 */
@Data
public class CreditExchangeVO extends CreditExchange {

    @ApiModelProperty(value = "礼品名")
    private String creditMallName;

    @ApiModelProperty(value = "礼品图")
    private String cover;

    @ApiModelProperty(value = "学生姓名")
    private String studentName;

    @ApiModelProperty(value = "学校名")
    private String schoolName;

    @ApiModelProperty(value = "手机号")
    private String mobile;

    @ApiModelProperty(value = "审核人")
    private String verifyStaffName;

}
