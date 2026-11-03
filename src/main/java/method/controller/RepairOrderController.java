package method.controller;

import jakarta.validation.Valid;
import method.dto.PageResult;
import method.dto.RepairOrderRequest;
import method.dto.RepairOrderResponse;
import method.dto.RepairOrderVO;
import method.service.RepairOrderService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.format.DateTimeFormatter;

@RestController
public class RepairOrderController {

    private final RepairOrderService repairOrderService;

    public RepairOrderController(RepairOrderService repairOrderService) {
        this.repairOrderService = repairOrderService;
    }

    //建单
    @PostMapping("/api/repair-orders")
    public ResponseEntity<RepairOrderResponse> addRepairOrdersRequest(@Valid @RequestBody RepairOrderRequest request) {

        RepairOrderResponse body = repairOrderService.create(request);
        return ResponseEntity                                      // ← ⑦
                .status(HttpStatus.CREATED)
                .header("Location", "/api/repair-orders/" + body.id())
                .body(body);
    }

    //按照id查单
    @GetMapping("/api/repair-orders/{id}")
    public RepairOrderResponse get(@PathVariable String id) {

        return repairOrderService.findById(id);
    }

    //
    @GetMapping("/api/repair-orders")
    public PageResult<RepairOrderVO> list(
            @RequestParam(defaultValue = "1") long page,
            @RequestParam(defaultValue = "10") long size,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String equipmentCode) {
        return repairOrderService.page(page, size, status, equipmentCode);
    }


}

