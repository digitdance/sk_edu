//package org.springcrazy.modules.lesson.entity;
//
//import com.baomidou.mybatisplus.annotation.FieldFill;
//import com.baomidou.mybatisplus.annotation.IdType;
//import com.baomidou.mybatisplus.annotation.TableField;
//import com.baomidou.mybatisplus.annotation.TableId;
//import org.springcrazy.common.pojo.AutoFillEntity;
//import io.swagger.annotations.ApiModel;
//import io.swagger.annotations.ApiModelProperty;
//import lombok.Data;
//import lombok.EqualsAndHashCode;
//
///**
// * <p>
// * 科目表
// * </p>
// *
// * @author Ryan
// */
//@Data
//@EqualsAndHashCode(callSuper = false)
//@ApiModel(value = "Subject对象", description = "科目表")
//public class Subject extends AutoFillEntity {
//
//    @TableId(value = "id", type = IdType.AUTO)
//    private Long id;
//
//    @ApiModelProperty(value = "名称")
//    private String name;
//
//    @ApiModelProperty(value = "简介")
//    private String info;
//
//    @ApiModelProperty(value = "排序权重")
//    private Integer sortNum;
//
//    @ApiModelProperty(value = "创建者所属机构")
//    @TableField(fill = FieldFill.INSERT)
//    private Long orgId;
//}
