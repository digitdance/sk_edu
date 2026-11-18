
package org.springcrazy.modules.user.vo;

import io.swagger.annotations.ApiModelProperty;
import org.springcrazy.modules.edu.entity.Course;
import org.springcrazy.modules.user.entity.Student;
import lombok.Data;
import lombok.EqualsAndHashCode;
import io.swagger.annotations.ApiModel;

import java.math.BigDecimal;

/**
 * 学员表视图实体类
 *
 * @author TongZhou
 * @since 2020-04-28
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "StudentVO对象", description = "学员表")
public class StudentVO extends Student {
	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "机构")
	private String deptName;

	@ApiModelProperty(value = "班级")
	private Course cls;

	@ApiModelProperty(value = "班级名称")
	private String className;

	@ApiModelProperty(value = "是否签到")
	private Boolean isClock;


	private Integer courseId;

	private BigDecimal classScore;

}
