
package org.springcrazy.modules.edu.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 勋章表实体类
 *
 * @author TongZhou
 * @since 2020-04-25
 */
@Data
@TableName("edu_medal_receive")
@ApiModel(value = "medal对象", description = "勋章")
public class MedalReceive implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 勋章主键
     */
    @ApiModelProperty(value = "勋章主键")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    /**
     * 勋章名称
     */
    @ApiModelProperty(value = "勋章")
    private Integer medalId;


    @ApiModelProperty(value = "")
    private Integer awardTo;

    @ApiModelProperty(value = "")
    private Integer awardFrom;


    /**
     * 1上架2下架3删除
     */
    @ApiModelProperty(value = "1上架2下架3删除")
    private Integer isAvaliable;

    /**
     * 添加时间
     */
    @ApiModelProperty(value = "添加时间")
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;


    /**
     * 最后更新时间
     */
    @ApiModelProperty(value = "最后更新时间")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /**
     * 序列
     */
    @ApiModelProperty(value = "序列")
    private Integer sort;

}
