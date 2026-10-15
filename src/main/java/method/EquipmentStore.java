package method;

import java.security.KeyPair;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class EquipmentStore {
    private Map<String, Equipment> store = new HashMap<>();

    public void add(Equipment e) {
        store.put(e.getCode(), e);
    }

    public Optional<Equipment> findByCode(String code) {
        return Optional.ofNullable(store.get(code));
    }

    public void addFromLine(String line) {
        String[] parts = line.split(",");
        if (parts.length != 3) {
            throw new IllegalArgumentException("格式错误: " + line);
        } else {
            Equipment e = new Equipment(parts[0], parts[1], parts[2]);
            add(e);
        }
    }


}
