package method.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import method.dto.PageResult;
import method.dto.RepairOrderRequest;
import method.dto.RepairOrderResponse;
import method.dto.RepairOrderVO;
import method.entity.Equipment;
import method.entity.RepairOrder;
import method.mapper.EquipmentMapper;
import method.mapper.RepairOrderMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor //Lombok 的注解，给所有 final 字段生成构造方法。
public class RepairOrderService {
    private static final DateTimeFormatter FMT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final long MAX_SIZE = 100;
    private static final Set<String> ALLOWED_STATUS = Set.of("OPEN", "RESOLVED");
    private final RepairOrderMapper repairOrderMapper;
    private final EquipmentMapper equipmentMapper;

    public PageResult<RepairOrderVO> page(long current, long size, String status, String equipmentCode) {
        //页码至少 1，size 夹到 1..100
        long safeCurrent = Math.max(current, 1);
        long safeSize = Math.min(Math.max(size, 1), MAX_SIZE);
        if (status != null && !ALLOWED_STATUS.contains(status)) {
            throw new IllegalArgumentException("状态只能是 " + ALLOWED_STATUS);
        }
        // 交给 MP
        Page<RepairOrderVO> pageParam = new Page<>(safeCurrent, safeSize);
        IPage<RepairOrderVO> result = repairOrderMapper.selectPageView(pageParam, status, equipmentCode);

        // 转成我的响应对象
        return new PageResult<>(
                result.getRecords(),
                result.getTotal(),
                result.getPages(),
                result.getCurrent(),
                result.getSize()
        );
    }

    public RepairOrderResponse create(RepairOrderRequest request) {
        Equipment eq = equipmentMapper.findByCode(request.equipment_code())
                .orElseThrow(() -> new IllegalArgumentException("设备不存在: " + request.equipment_code()));

        RepairOrder order = new RepairOrder();
        order.setId("RO-" + IdWorker.getIdStr());
        order.setEquipmentId(eq.getId());
        order.setSiteId(request.site_id());
        order.setIssueSummary(request.issue_summary());
        order.setPriority(request.priority().name());
        order.setStatus("OPEN");
        order.setCreatedAt(LocalDateTime.now());      // ← 只调一次（原来调了两次，可能差几毫秒）

        repairOrderMapper.insert(order);

        return toResponse(order, eq.getCode());       // ← 复用
    }

    public RepairOrderResponse findById(String id) {
        RepairOrder order = repairOrderMapper.selectById(id);
        if (order == null) {                                          // ← 补上判空
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "工单不存在: " + id);
        }
        String code = equipmentMapper.findById(order.getEquipmentId())
                .map(Equipment::getCode)
                .orElse(null);
        return toResponse(order, code);
    }
    private RepairOrderResponse toResponse(RepairOrder order, String equipmentCode) {
        return new RepairOrderResponse(
                order.getId(),
                equipmentCode,
                order.getStatus(),                        // ← 不再写死 "OPEN"
                order.getPriority(),                      // ← 不再错位
                order.getCreatedAt().format(FMT)          // ← 不再用 now()
        );
    }


}
