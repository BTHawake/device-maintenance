package com.awake.repair.dto;

import lombok.Data;

import java.time.LocalDateTime;

//	装一页里的一行，把两个表拼接为返回前端用来看的请求体
@Data
public class RepairOrderVO {
    private String id;
    private String equipmentCode;      // 来自 JOIN 的 e.code
    private String siteId;
    private String issueSummary;
    private String priority;
    private String status;
    private LocalDateTime createdAt;
}
