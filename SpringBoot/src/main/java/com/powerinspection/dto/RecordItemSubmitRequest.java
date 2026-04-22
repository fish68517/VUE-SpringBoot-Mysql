package com.powerinspection.dto;

import lombok.Data;

@Data
public class RecordItemSubmitRequest {
    private Long itemId;
    private String checkedValue;
    private String itemResult;
    private String itemRemark;
}
