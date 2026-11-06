-- ============================================================
-- 002_equipment.sql  ·  设备表主键改造迁移
--
-- 从 W01 结构迁到 W02 结构：
--
--   旧：equipment(code PK)
--       repair_order(id PK, equipment_code → FK equipment.code)
--
--   新：equipment(id PK auto_increment, code UNIQUE + CHECK 无空白)
--       repair_order(id PK, equipment_id → FK equipment.id,
--                    deleted TINYINT 软删除标记)
--
-- 为什么要改：
--   `code` 是业务编号，会变（客户改规则 / 设备搬迁重排 / 录错要改）。
--   实测 `UPDATE equipment SET code='PUMP-001' WHERE code='EQ-A001'`
--   会被外键以 ERROR 1451 拒绝，于是"改编号"这个需求根本做不到。
--   拆成"不变的 id 给外键用 + 会变的 code 只做唯一约束"才解得开。
--
-- 说明：本项目实际是重建（schema.sql 里 DROP + CREATE），
--       数据是种子数据没有保留价值。但真实环境里表里有数据，
--       就必须按下面的顺序原地迁移 —— 这就是本文件的意义。
--
-- 执行：mysql --login-path=course --default-character-set=utf8mb4 \
--            -D device_maintenance -e "source 002_equipment.sql;"
-- ============================================================

USE device_maintenance;

-- ------------------------------------------------------------
-- 第 0 步：先摘掉旧外键 —— 这一步必须在最前面
--
-- 为什么：repair_order 的外键指向 equipment.code，而 code 是主键。
--   只要还有外键依赖这个索引，`ALTER TABLE equipment DROP PRIMARY KEY`
--   就会报 ERROR 1553: Cannot drop index 'PRIMARY': needed in a foreign key constraint。
--   所以顺序是：先拆依赖 → 再改父表。反过来一定失败。
-- ------------------------------------------------------------
ALTER TABLE repair_order
    DROP FOREIGN KEY fk_order_equipment;

-- ------------------------------------------------------------
-- 第一步：equipment 换成代理主键
-- ------------------------------------------------------------

-- 1.1 摘掉旧主键。删主键不会丢数据，也不会自动删掉 code 的非空约束。
ALTER TABLE equipment
    DROP PRIMARY KEY;

-- 1.2 加自增代理主键。放在第一列只是为了好看，位置不影响语义。
ALTER TABLE equipment
    ADD COLUMN id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY FIRST;

-- 1.3 code 从主键降级为唯一键 —— 业务规则还是要的，只是不再承担"身份"。
ALTER TABLE equipment
    ADD CONSTRAINT uk_equipment_code UNIQUE (code);

-- 1.4 补上空白处理。数据库不会自动 trim，
--     大小写倒是被 utf8mb4_0900_ai_ci 的 ci 管住了。
ALTER TABLE equipment
    ADD CONSTRAINT chk_equipment_code_no_space CHECK (code = TRIM(code));

-- ------------------------------------------------------------
-- 第二步：repair_order 换外键目标
--
-- 顺序很关键：先加列 → 再回填 → 才能改 NOT NULL → 才能加新外键
--            一切就绪后才允许删旧列。反过来做会直接数据丢失。
-- ------------------------------------------------------------

-- 2.1 先加成可空，因为这一刻还没有值
ALTER TABLE repair_order
    ADD COLUMN equipment_id BIGINT NULL AFTER id;

-- 2.2 用旧的 code 关联回填出 id（此时 equipment_code 列还在）
UPDATE repair_order o
    JOIN equipment e ON e.code = o.equipment_code
SET o.equipment_id = e.id;

-- 2.3 回填完才能收紧。如果有孤儿工单（指向不存在的设备），
--     这一步会失败 —— 那正是我们想要的：脏数据不许过关。
ALTER TABLE repair_order
    MODIFY equipment_id BIGINT NOT NULL;

-- 2.4 删掉旧列（回填已完成，它没用了）
ALTER TABLE repair_order
    DROP COLUMN equipment_code;

-- 2.5 新外键指向 equipment(id)。
--     不写 ON UPDATE / ON DELETE = 默认 NO ACTION，
--     即"只要还有工单引用这台设备，就不许改它的 id、也不许删它"。
ALTER TABLE repair_order
    ADD CONSTRAINT fk_order_equipment FOREIGN KEY (equipment_id) REFERENCES equipment (id);

-- 2.6 按设备查工单要用的索引
CREATE INDEX idx_order_equipment ON repair_order (equipment_id);

-- ------------------------------------------------------------
-- 第三步：软删除列（02.04 引入）
-- ------------------------------------------------------------

-- NOT NULL DEFAULT 0 让存量数据自动变成"未删除"，不需要 UPDATE 回填。
-- 用数值类型而不是字符串：以后若要 `@TableLogic(delval="id")`
-- 这种"已删值各不相同"的写法，只有数值类型能原样拼进 SQL。
ALTER TABLE repair_order
    ADD COLUMN deleted TINYINT NOT NULL DEFAULT 0 COMMENT '0未删除 1已删除';

-- ------------------------------------------------------------
-- 迁移后自检（在客户端手动跑，确认没迁歪）
-- ------------------------------------------------------------
-- SELECT COUNT(*) FROM equipment;                                   -- 行数与迁移前一致
-- SELECT COUNT(*) FROM equipment WHERE id IS NULL;                  -- 必须 0
-- SELECT COUNT(*) FROM equipment WHERE code <> TRIM(code);          -- 必须 0
-- SELECT COUNT(*) FROM repair_order WHERE equipment_id IS NULL;     -- 必须 0
-- SELECT COUNT(*) FROM repair_order WHERE deleted <> 0;             -- 必须 0
--
-- 外键与索引应当都在：
-- SHOW CREATE TABLE equipment\G
-- SHOW CREATE TABLE repair_order\G
