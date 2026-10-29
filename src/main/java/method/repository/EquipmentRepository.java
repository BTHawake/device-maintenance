package method.repository;

import method.Equipment;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class EquipmentRepository {

    private final JdbcTemplate jdbc;

    public EquipmentRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    // 一行数据库记录 → 一个 Equipment 对象
    private static final RowMapper<Equipment> ROW_MAPPER = (rs, rowNum) ->
            new Equipment(
                    rs.getLong("id"),
                    rs.getString("code"),
                    rs.getString("model"),
                    rs.getString("state")
            );

    public List<Equipment> findAll() {
        return jdbc.query(
                "SELECT id, code, model, state FROM equipment ORDER BY id",
                ROW_MAPPER);
    }

    public Optional<Equipment> findByCode(String code) {
        List<Equipment> list = jdbc.query(
                "SELECT id, code, model, state FROM equipment WHERE code = ?",
                ROW_MAPPER,
                code);
        return list.stream().findFirst();
    }


}