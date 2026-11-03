package method.service;

import lombok.RequiredArgsConstructor;
import method.entity.Equipment;
import method.mapper.EquipmentMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

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
