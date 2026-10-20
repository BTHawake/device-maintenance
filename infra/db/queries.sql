-- 查询全部设备清单，并且按照编号升序
SELECT e.model, code, name, state, site_id
FROM equipment e
ORDER BY e.code;

-- 每台设备各有几条工单；没有工单的设备也要出现，显示 0
SELECT e.code, COUNT(o.equipment_code)
FROM equipment e
         LEFT JOIN repair_order o ON e.code = o.equipment_code
GROUP BY e.code;

-- 	每个优先级的工单数量
SELECT o.priority, COUNT(o.priority)
FROM repair_order o
GROUP BY o.priority;

-- 设备 + 工单明细，只保留有工单的
SELECT e.code, o.issue_summary
FROM equipment e
         JOIN repair_order o ON e.code = o.equipment_code;

-- EQ-A001 的工单，按创建时间倒序
SELECT o.issue_summary, created_at
FROM repair_order o
WHERE equipment_code = 'EQ-A001'
ORDER BY created_at DESC;

-- 工单分页：第 2 页，每页 2 条
SELECT *
FROM repair_order o ORDER BY created_at DESC
LIMIT 2 OFFSET 2 ;