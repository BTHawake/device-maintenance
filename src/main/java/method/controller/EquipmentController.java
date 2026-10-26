package method.controller;

import method.Equipment;
import method.EquipmentStore;
import method.dto.RepairOrderResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@RestController
public class EquipmentController {
    private final EquipmentStore store = new EquipmentStore();

    public EquipmentController() {
        Equipment e1 = new Equipment("EQ-A001", "SIM-P100", "ACTIVE");
        Equipment e2 = new Equipment("EQ-A002", "SIM-P100", "ACTIVE");
        store.add(e1);
        store.add(e2);
    }

    @GetMapping("/api/equipment/{id}")
    public Equipment getEquipment(@PathVariable String id) {
        Optional<Equipment> e = store.findByCode(id);
        if (e.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "设备不存在");
        } else {
            return e.get();
        }
    }



}



