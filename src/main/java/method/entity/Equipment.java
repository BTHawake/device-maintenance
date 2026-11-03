package method.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
public class Equipment {
    private String code;
    private String model;
    private String state;
    private Long id;
    public void archive() {
        if ("ACTIVE".equals(state)) {
            setState("ARCHIVED");
        } else if ("ARCHIVED".equals(state)) {
            throw new IllegalStateException("设备已归档，不能重复归档");
        } else {
            throw new IllegalStateException("设备未激活");
        }
    }
}
