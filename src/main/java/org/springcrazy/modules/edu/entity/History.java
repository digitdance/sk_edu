
package org.springcrazy.modules.edu.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 课程播放记录(学习记录)实体类
 *
 * @author TongZhou
 * @since 2020-05-06
 */
@Data
@TableName("edu_history")
@ApiModel(value = "", description = "历史(学习记录)")
public class History implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    /**
     * 用户id
     */
    @ApiModelProperty(value = "用户id")
    private Integer userId;
    /**
     * 课程id
     */
    @ApiModelProperty(value = "课程id")
    private Integer objId;

    /**
     * 课程类型 book_read 阅读历史 post 发布文章
     */
    @ApiModelProperty(value = "类型")
    private String type;

    @ApiModelProperty(value = "进度")
    private String progress;

    @ApiModelProperty(value = "其他参数")
    private String params;

    @ApiModelProperty(value = "学生id")
    private Integer studentId;
}
