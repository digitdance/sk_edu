
package org.springcrazy.modules.edu.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springcrazy.core.json.SerializerBigDecimal;
import org.springcrazy.modules.edu.entity.CoursePackage;
import org.springcrazy.modules.edu.entity.CoursePackageExam;

import java.math.BigDecimal;

/**
 * 视图实体类
 *
 * @author TongZhou
 * @since 2020-05-05
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "CoursePackageVO对象", description = "CoursePackageVO对象")
public class CoursePackageExamVO extends CoursePackageExam {
	private static final long serialVersionUID = 1L;
	//课程名称
	private String examName;
	private String userIds;

	private Integer examNum;


	private Integer examSubjectId;

	private Integer exampaperIsDelete;
	private Integer courseIsDelete;
	private Integer kpointIsDelete;

}
