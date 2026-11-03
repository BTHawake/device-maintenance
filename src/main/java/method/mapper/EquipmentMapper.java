package method.mapper;

import method.Equipment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Optional;

@Mapper
public interface EquipmentMapper
{
    @Select("SELECT id, code, model, state FROM equipment ORDER BY id")
    List<Equipment> findAll();

    @Select("SELECT id, code, model, state FROM equipment WHERE code = #{code}")
    Optional<Equipment> findByCode(String code);

}
