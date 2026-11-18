
package org.springcrazy.modules.cms.vo;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springcrazy.modules.cms.entity.Article;
import org.springcrazy.modules.cms.entity.Special;
import org.springcrazy.modules.edu.entity.Course;

import java.util.List;

/**
 * 文章信息表视图实体类
 *
 * @author TongZhou
 * @since 2020-02-28
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "SpecialVO对象", description = "专题信息表")
public class SpecialVO extends Special {
	private static final long serialVersionUID = 1L;

	private String subjectName;

	private List<Course> courseList;

	private List<Article> articleList;

}
