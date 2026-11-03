package method.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import method.dto.PageResult;
import method.dto.RepairOrderVO;
import method.mapper.RepairOrderMapper;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor //Lombok 的注解，给所有 final 字段生成构造方法。
public class RepairOrderService {

    private static final long MAX_SIZE = 100;

    private final RepairOrderMapper repairOrderMapper;
    private static final Set<String> ALLOWED_STATUS = Set.of("OPEN", "RESOLVED");
    public PageResult<RepairOrderVO> page(long current, long size, String status, String equipmentCode) {
        //页码至少 1，size 夹到 1..100
        long safeCurrent = Math.max(current, 1);
        long safeSize = Math.min(Math.max(size, 1), MAX_SIZE);
        if (status != null && !ALLOWED_STATUS.contains(status)) {
            throw new IllegalArgumentException("状态只能是 " + ALLOWED_STATUS);
        }
        // 交给 MP
        Page<RepairOrderVO> pageParam = new Page<>(safeCurrent, safeSize);
        IPage<RepairOrderVO> result = repairOrderMapper.selectPageView(pageParam, status, equipmentCode);

        // 转成我的响应对象
        return new PageResult<>(
                result.getRecords(),
                result.getTotal(),
                result.getPages(),
                result.getCurrent(),
                result.getSize()
        );
    }

}
