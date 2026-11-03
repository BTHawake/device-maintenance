package method.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class RepairOrder {
    @TableId(type = IdType.INPUT)//按照自己的规则递增，而不是按照默认的雪花算法自动生成
    String id;
    Long equipmentId;
    String siteId;
    String issueSummary;
    String priority;
    String status;
    LocalDateTime createdAt;
}
