package com.awake.repair.service;

import lombok.RequiredArgsConstructor;
import com.awake.repair.entity.Equipment;
import com.awake.repair.mapper.EquipmentMapper;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
@RequiredArgsConstructor
public class EquipmentService {
    private final EquipmentMapper equipmentMapper;

    public Equipment findById(String id) {

        Optional<Equipment> e = equipmentMapper.findByCode(id);
        if (e.isEmpty()) {
            throw new IllegalArgumentException("设备不存在: ");
        }
        return e.get();
    }
}
