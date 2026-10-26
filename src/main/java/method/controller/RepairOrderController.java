package method.controller;

import jakarta.validation.Valid;
import method.EquipmentStore;
import method.dto.RepairOrderRequest;
import method.dto.RepairOrderResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@RestController
public class RepairOrderController {
    private static final DateTimeFormatter FMT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private final Map<String, RepairOrderResponse> store = new HashMap<>();
    private final EquipmentStore equipmentStore;
    private int countId = 1001;

    public RepairOrderController(EquipmentStore equipmentStore) {
        this.equipmentStore = equipmentStore;
    }

    //建单
    @PostMapping("/api/repair-orders")
    public ResponseEntity<RepairOrderResponse> addRepairOrdersRequest(@Valid @RequestBody RepairOrderRequest request) {

        if (equipmentStore.findByCode(request.equipment_code()).isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "设备不存在: " + request.equipment_code());
        }

        String id = "RO-" + countId++;

        String created_at = LocalDateTime.now().format(FMT);
        RepairOrderResponse body = new RepairOrderResponse(id, request.equipment_code(), "OPEN", request.priority().name(), created_at);
        store.put(id, body);

        return ResponseEntity                                      // ← ⑦
                .status(HttpStatus.CREATED)
                .header("Location", "/api/repair-orders/" + id)
                .body(body);
    }

    //按照id查单
    @GetMapping("/api/repair-orders/{id}")
    public RepairOrderResponse get(@PathVariable String id) {
        RepairOrderResponse order = store.get(id);
        if (order == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "工单不存在");
        }
        return order;
    }




}
/*

① 请求
   POST /api/repair-orders
   请求体：{
    "equipment_code" : "EQ-A001" ,
    "site_id" : "SITE-A" ,
    "issue_summary" : "模拟状态页出现黄灯" ,
    "priority" : "NORMAL"
   }

② 成功
   状态码：201
   Location 头：/api/repair-orders/RO-1001
   响应体：{
    "id": "RO-1001",
    "equipment_code": "EQ-A001",
    "status": "OPEN",
    "priority": "NORMAL",
    "created_at": "2026-01-20 09:00:00"       ← 服务器补上哪几个？
   }

③ 失败
   400 什么时候？请求出错
   404 什么时候？找不到，返回的时候出错
   409 什么时候？重复了

*/
