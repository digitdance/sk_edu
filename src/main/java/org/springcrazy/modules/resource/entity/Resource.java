
package org.springcrazy.modules.resource.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springcrazy.core.mp.base.BaseEntity;

/**
 * 实体类
 *
 * @author TongZhou
 */
@Data
@TableName("tb_resource")
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "资源对象", description = "资源对象")
public class Resource extends BaseEntity {

	private static final long serialVersionUID = 1L;

	/**
	 * 主键id
	 */
	@TableId(value = "id", type = IdType.AUTO)
	@ApiModelProperty(value = "主键id")
	private Integer id;

	@TableId(value = "key")
	@ApiModelProperty(value = "")
	private String key;

	@TableId(value = "path")
	@ApiModelProperty(value = "")
	private String path;

	@TableId(value = "filename")
	@ApiModelProperty(value = "")
	private String filename;

	@TableId(value = "root_path")
	@ApiModelProperty(value = "")
	private String rootPath;

}
