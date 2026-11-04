package com.awake.repair.controller;

import com.awake.repair.entity.Equipment;
import com.awake.repair.mapper.EquipmentMapper;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@RestController
public class EquipmentController {
    private final EquipmentMapper equipmentMapper;
    public EquipmentController(EquipmentMapper equipmentMapper) {
        this.equipmentMapper=equipmentMapper;
    }
    //给路由
    @GetMapping("/api/equipment/{id}")
    public Equipment getEquipment(@PathVariable String id) {
        Optional<Equipment> e = equipmentMapper.findByCode(id);
        if (e.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "设备不存在");
        } else {
            return e.get();
        }
    }



}



