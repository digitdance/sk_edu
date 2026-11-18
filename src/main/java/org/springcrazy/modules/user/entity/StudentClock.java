
package org.springcrazy.modules.user.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springcrazy.common.enums.StudentStageEnum;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;


/**
 * 学员表实体类
 *
 * @author TongZhou
 * @since 2020-04-28
 */
@Data
@TableName("student_clock")
@ApiModel(value = "Student对象", description = "学员签到表")
public class StudentClock implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 学员id
     */
    @ApiModelProperty(value = "学员id")
    @TableId(value = "id", type = IdType.AUTO)
    protected Integer id;


    protected Integer userId;
    protected Integer studentId;

    @DateTimeFormat(
            pattern = "yyyy-MM-dd"
    )
    @JsonFormat(
            pattern = "yyyy-MM-dd"
    )
   private Date clockDay;//生日

}
