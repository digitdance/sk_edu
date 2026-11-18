
package org.springcrazy.modules.edu.vo;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springcrazy.modules.edu.entity.Medal;
import org.springcrazy.modules.edu.entity.MedalReceive;

/**
 * 勋章表视图实体类
 *
 * @author TongZhou
 * @since 2020-04-25
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "CourseVO对象", description = "勋章表")
public class MedalReceiveVO extends MedalReceive {
	private static final long serialVersionUID = 1L;


}
