package org.springcrazy.modules.cms.vo;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@ApiModel(value = "ArticleVO对象", description = "文章信息表")
public class Author {

    String name;
    String avatar;
}
