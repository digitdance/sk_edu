package org.springcrazy.modules.creditMall.pojo;

import org.springcrazy.common.pojo.PaginateDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class CreditMallParamDTO extends PaginateDTO {
    private String name;
    private Long id;
    private Long categoryId;
    @ApiModelProperty(value = "是否只显示可兑换的")
    private Boolean canExchange;
    private String stateText;
    private Integer state;
    private String searchKey;
    private String type;
}
