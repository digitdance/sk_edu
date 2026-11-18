
package org.springcrazy.modules.edu.vo;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springcrazy.modules.edu.entity.CourseHomework;
import org.springcrazy.modules.edu.entity.CourseMaterial;

/**
 * 章节资料信息视图实体类
 *
 * @author TongZhou
 * @since 2020-10-14
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "CourseHomeworkVO对象", description = "章节作业信息")
public class CourseHomeworkVO extends CourseHomework {
	private static final long serialVersionUID = 1L;

}
