package org.springcrazy.modules.creditMall.pojo;

import org.springcrazy.common.pojo.PaginateDTO;
import lombok.Data;

@Data
public class CreditExchangeParamDTO extends PaginateDTO {

    private String giftName;
    private Long creditMallId;
    private Integer studentId;
    private String verifyStateText;
    private Integer verifyState;
    private String endDate;
    private String startDate;

}
