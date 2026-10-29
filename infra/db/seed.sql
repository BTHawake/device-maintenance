USE device_maintenance;
INSERT INTO equipment (code, name, model, site_id, state, version)
VALUES ('EQ-A001', '模拟循环泵一号', 'SIM-P100', 'SITE-A', 'ACTIVE', 1),
       ('EQ-A002', '模拟循环泵二号', 'SIM-P100', 'SITE-A', 'ACTIVE', 1),
       ('EQ-A003', '模拟显示屏', 'SIM-D200', 'SITE-A', 'ACTIVE', 1),
       ('EQ-A004', '旧模拟泵', 'SIM-P100', 'SITE-A', 'ARCHIVED', 2),
       ('EQ-B001', 'B站模拟泵', 'SIM-P100', 'SITE-B', 'ACTIVE', 1);


INSERT INTO repair_order(id, equipment_id, site_id, issue_summary, priority, status, created_at)
VALUES ('RO-1001', (SELECT id FROM equipment WHERE code = 'EQ-A001'), 'SITE-A', '模拟状态页出现黄灯', 'NORMAL', 'OPEN', '2026-01-14 09:00:00'),
       ('RO-1002', (SELECT id FROM equipment WHERE code = 'EQ-A001'), 'SITE-A', '模拟编号标签需补录', 'LOW', 'RESOLVED', '2026-01-10 09:00:00'),
       ('RO-2001', (SELECT id FROM equipment WHERE code = 'EQ-B001'), 'SITE-B', 'B站内部模拟记录', 'NORMAL', 'OPEN', '2026-01-12 09:00:00');