//package org.springcrazy.modules.lesson.entity;
//
//import com.baomidou.mybatisplus.annotation.FieldFill;
//import com.baomidou.mybatisplus.annotation.IdType;
//import com.baomidou.mybatisplus.annotation.TableField;
//import com.baomidou.mybatisplus.annotation.TableId;
//import com.fasterxml.jackson.annotation.JsonFormat;
//import com.hzb.erp.common.enums.VerifyStateEnum;
//import io.swagger.annotations.ApiModel;
//import io.swagger.annotations.ApiModelProperty;
//import lombok.Data;
//import lombok.EqualsAndHashCode;
//
//import java.io.Serializable;
//import java.time.LocalDateTime;
//
///**
// * <p>
// * 试听预约记录
// * </p>
// *
// * @author Ryan
// */
//@Data
//@EqualsAndHashCode(callSuper = false)
//@ApiModel(value = "Appointment对象", description = "试听预约记录")
//public class Appointment implements Serializable {
//
//    private static final long serialVersionUID = 1L;
//
//    @TableId(value = "id", type = IdType.AUTO)
//    private Long id;
//
//    @ApiModelProperty(value = "试听课次")
//    private Long lessonId;
//
//    @ApiModelProperty(value = "所属课程")
//    private Long courseId;
//
//    @ApiModelProperty(value = "学员id")
//    private Long studentId;
//
//    @ApiModelProperty(value = "学员顾问id")
//    private Long counselor;
//
//    @ApiModelProperty(value = "添加时间")
//    @TableField(fill = FieldFill.INSERT)
//    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
//    private LocalDateTime addTime;
//
//    @ApiModelProperty(value = "备注")
//    private String remark;
//
//    @ApiModelProperty(value = "审核时间")
//    private LocalDateTime verifyTime;
//
//    @ApiModelProperty(value = "审核状态")
//    private VerifyStateEnum verifyState;
//
//    @ApiModelProperty(value = "审核人")
//    private Long verifyStaff;
//
//    @ApiModelProperty(value = "审核内容")
//    private String verifyRemark;
//
//    @ApiModelProperty(value = "学生所属学校ID")
//    private Long schoolId;
//
//    @ApiModelProperty(value = "试听卡id")
//    private Long trialId;
//
//    @ApiModelProperty(value = "消费试听卡领取记录id")
//    private Long trialRecordId;
//}
