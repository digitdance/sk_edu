
package org.springcrazy.modules.edu.vo;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springcrazy.modules.edu.entity.CourseExam;
import org.springcrazy.modules.edu.entity.CourseFavorites;

import java.math.BigDecimal;

/**
 * 收藏视图实体类
 *
 * @author TongZhou
 * @since 2020-04-25
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "CourseFavoritesVO对象", description = "收藏")
public class CourseExamVO extends CourseExam {
	private static final long serialVersionUID = 1L;
	//课程名称
	private String courseName;

}
