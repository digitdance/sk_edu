package org.springcrazy.modules.creditMall.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import org.springcrazy.common.pojo.AutoFillEntity;
import com.hzb.erp.common.enums.VerifyStateEnum;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springcrazy.core.mp.base.BaseEntity;

import java.time.LocalDateTime;

/**
 * 积分商城兑换记录
 *
 * @TableName credit_exchange
 */
@TableName(value = "credit_exchange")
@Data
public class CreditExchange extends BaseEntity {

    @ApiModelProperty(value = "主键")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "礼品id")
    @TableField(value = "credit_mall_id")
    private Long creditMallId;

    @ApiModelProperty(value = "学生id")
    @TableField(value = "student_id")
    private Integer studentId;

    @ApiModelProperty(value = "学生id")
    @TableField(value = "school_id")
    private Long schoolId;

    @ApiModelProperty(value = "学生账号id")
    @TableField(value = "user_id")
    private Integer userId;

    @ApiModelProperty(value = "兑换数量")
    @TableField(value = "num")
    private Integer num;

    @ApiModelProperty(value = "花费积分")
    @TableField(value = "credit")
    private Integer credit;

    @ApiModelProperty(value = "审核确认状态")
    private VerifyStateEnum verifyState;

    @ApiModelProperty(value = "审核人")
    private Long verifyStaff;

    @ApiModelProperty(value = "审核时间")
    private LocalDateTime verifyTime;

    @ApiModelProperty(value = "审核备注")
    private String verifyRemark;

}