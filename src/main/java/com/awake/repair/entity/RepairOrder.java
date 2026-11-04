package com.awake.repair.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("repair_order")
public class RepairOrder {
    @TableId(type = IdType.INPUT)//设为主键
    String id;

    Long equipmentId;
    String siteId;
    String issueSummary;
    String priority;
    String status;
    LocalDateTime createdAt;

    @TableLogic
    private Integer deleted;
}
