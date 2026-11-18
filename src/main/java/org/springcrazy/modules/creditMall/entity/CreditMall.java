package org.springcrazy.modules.creditMall.entity;

import com.baomidou.mybatisplus.annotation.*;
import org.springcrazy.common.pojo.AutoFillEntity;
import com.hzb.erp.common.enums.SwitchEnum;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springcrazy.core.mp.base.BaseEntity;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * 积分商城礼品表
 * @TableName credit_mall
 */
@TableName(value ="credit_mall")
@Data
public class CreditMall extends BaseEntity {

    @ApiModelProperty(value = "主键")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "所属分校")
    @TableField(value = "school_id")
    private Long schoolId;

    @ApiModelProperty(value = "物料id")
    @TableField(value = "material_id")
    private Long materialId;

    @ApiModelProperty(value = "分类id")
    @TableField(value = "category_id")
    private Long categoryId;

    @ApiModelProperty(value = "类型")
    private String type;

    @ApiModelProperty(value = "分类名称")
    @TableField(value = "category_name")
    private String categoryName;

    @ApiModelProperty(value = "礼品名称")
    @TableField(value = "name")
    private String name;

    @ApiModelProperty(value = "兑换所需积分")
    @TableField(value = "credit")
    private Integer credit;

    @ApiModelProperty(value = "库存数量")
    @TableField(value = "stock")
    private Integer stock;

    @ApiModelProperty(value = "历史兑换数量")
    @TableField(value = "sale_num")
    private Integer saleNum;

    @ApiModelProperty(value = "预览次数")
    @TableField(value = "view_num")
    private Integer viewNum;

    @ApiModelProperty(value = "礼品图")
    @TableField(value = "cover")
    private String cover;

    @ApiModelProperty(value = "详情内容")
    @TableField(value = "content")
    private String content;

    @ApiModelProperty(value = "是否可以兑换")
    @TableField(value = "state")
    private Integer state;

    @ApiModelProperty(value = "创建者所属机构")
    @TableField(fill = FieldFill.INSERT)
    private Long orgId;

    @ApiModelProperty(value = "排序")
    private Integer sort;

    @ApiModelProperty(value = "负库存")
    private Integer banExangeWhen0;

}