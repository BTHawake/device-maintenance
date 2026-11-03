package method.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import method.dto.RepairOrderVO;
import method.entity.RepairOrder;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface RepairOrderMapper extends BaseMapper<RepairOrder> {
    IPage<RepairOrderVO> selectPageView(IPage<RepairOrderVO> page, @Param("status") String status, @Param("equipmentCode") String equipmentCode);
}
