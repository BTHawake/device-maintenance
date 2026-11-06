# 设备维保 API 约定（W02）

> 服务地址：`http://localhost:8080`
> 所有请求与响应均为 `application/json; charset=UTF-8`
> 本文档对应当前代码提交的行为，已由 `requests/run-cases.ps1` 的 21 条用例覆盖

---

## 一、通用约定

### 1.1 错误响应

**所有错误**（4xx / 5xx）返回同一种结构：

```json
{
  "status": 400,
  "error": "Bad Request",
  "message": "equipment_code: 不能为空",
  "path": "/api/repair-orders",
  "timestamp": "2026-11-05 21:51:00"
}
```

| 字段 | 含义 |
|---|---|
| `status` | HTTP 状态码（与响应头一致） |
| `error` | 状态码的标准短语 |
| `message` | **给用户看的具体原因** —— 前端该展示这个 |
| `path` | 出错的请求路径 |
| `timestamp` | 服务端时间 |

### 1.2 状态码

| 场景 | 状态码 | 触发条件 |
|---|---|---|
| 成功查询 / 更新 | **200** | — |
| 创建成功 | **201** | 响应体含服务器生成的对象，并带 `Location` 头 |
| 删除成功 | **204** | 无响应体 |
| 参数非法 | **400** | 校验失败、枚举值非法、参数类型错、业务白名单不通过 |
| 查不到 / 引用的资源不存在 | **404** | 工单不存在、设备不存在 |
| 状态冲突 | **409** | 业务状态不允许（预留，当前无接口触发） |
| 未预期异常 | **500** | `message` 固定为「服务器内部错误」，不含内部细节 |

> **安全约定**：500 的 `message` 永远是固定文案，真实堆栈只写服务端日志。
> 因为异常消息可能带 SQL、表名、路径甚至凭据。

---

## 二、设备接口

### 2.1 按编号查设备

```
GET /api/equipment/{code}
```

| 情况 | 响应 |
|---|---|
| 存在 | `200` `{"code":"EQ-A001","model":"SIM-P100","state":"ACTIVE","id":1}` |
| 不存在 | `404` `{"message":"设备不存在"}` |

> 注意：路径参数叫 `{id}` 是历史命名，实际按 **`code`** 匹配。

---

## 三、工单接口

### 3.1 创建工单

```
POST /api/repair-orders
```

请求体：

```json
{
  "equipment_code": "EQ-A001",
  "site_id": "SITE-A",
  "issue_summary": "模拟状态页出现黄灯",
  "priority": "NORMAL"
}
```

| 字段 | 必填 | 约束 |
|---|---|---|
| `equipment_code` | ✅ | 非空，且**必须指向存在的设备** |
| `site_id` | ✅ | 非空 |
| `issue_summary` | ✅ | 非空，长度 1–255（与数据库 `VARCHAR(255)` 对齐） |
| `priority` | ✅ | `LOW` / `NORMAL` / `HIGH` 之一 |

响应 `201`：

```json
{
  "id": "RO-2118341591248912386",
  "equipment_code": "EQ-A001",
  "status": "OPEN",
  "priority": "NORMAL",
  "created_at": "2026-11-05 21:51:00"
}
```

响应头：`Location: /api/repair-orders/RO-2118341591248912386`

**服务端生成、客户端不能指定的字段**：`id`、`status`（恒为 `OPEN`）、`created_at`

::: warning 工单号是雪花 ID
格式为 `RO-` + 19 位数字，**不连续**（中间会跳号）。

原因：并发安全 + 重启不冲突。**不要对工单号做"连号"假设**（比如从号里推算"这是第几张单"）。
:::

失败情况：

| 输入 | 响应 |
|---|---|
| `equipment_code: ""` | `400` `equipment_code: 不能为空` |
| `priority: "URGENT"` | `400` `请求体格式错误或字段值非法` |
| 漏传 `priority` | `400` `priority: 不能为null` |
| `issue_summary` 超 255 字 | `400`（`@Size` 拦截） |
| `equipment_code: "EQ-XXX"` | `404` `设备不存在: EQ-XXX` |

### 3.2 按 ID 查工单

```
GET /api/repair-orders/{id}
```

| 情况 | 响应 |
|---|---|
| 存在且未删除 | `200` + 工单对象 |
| 不存在 / 已删除 | `404` `{"message":"工单不存在: RO-xxx"}` |

### 3.3 分页查询工单列表

```
GET /api/repair-orders?page=1&size=10&status=OPEN&equipmentCode=EQ-A001
```

| 参数 | 必填 | 默认 | 说明 |
|---|---|---|---|
| `page` | ❌ | `1` | 页码，`< 1` 会被夹成 1；类型错（如 `abc`）返回 **400** |
| `size` | ❌ | `10` | 每页条数，夹在 `1..100` |
| `status` | ❌ | — | `OPEN` / `RESOLVED`；其它值返回 **400** |
| `equipmentCode` | ❌ | — | 按设备编号过滤 |

响应 `200`：

```json
{
  "records": [
    {
      "id": "RO-2118341591248912386",
      "equipmentCode": "EQ-A001",
      "siteId": "SITE-A",
      "issueSummary": "模拟状态页出现黄灯",
      "priority": "NORMAL",
      "status": "OPEN",
      "createdAt": "2026-11-05T21:51:00"
    }
  ],
  "total": 4,
  "pages": 2,
  "current": 1,
  "size": 2
}
```

**分页语义**：

| 情况 | 行为 |
|---|---|
| 页码超出总页数 | `records: []`，但 `total` 仍为真实总数 |
| `size` 超过 100 | 夹到 100 |
| 数据量不足以填满一页 | 返回实际条数 |

**排序**：`created_at DESC, id DESC` —— **双键保证稳定**。

> 只按 `created_at` 排序时，同一秒创建的多张工单顺序由数据库自由决定，
> 翻页会出现重复或漏行。加 `id` 作为第二排序键打破并列。

**筛选条件的组合**：多个条件之间是 **AND**；都不传等于全查。

### 3.4 更新工单

```
PATCH /api/repair-orders/{id}
```

请求体（**所有字段可选，只改传了的**）：

```json
{ "status": "RESOLVED", "priority": "HIGH", "issueSummary": "换了新描述" }
```

| 字段 | 约束 |
|---|---|
| `status` | `OPEN` / `RESOLVED`，其它值 **400** |
| `priority` | `LOW` / `NORMAL` / `HIGH` |
| `issueSummary` | 长度 1–255 |

响应 `200` + **更新后的完整对象**。

::: warning 为什么用 PATCH 而不是 PUT
本接口是**部分更新**：没传的字段保持原值。

`PUT` 的语义是**整体替换**（没传的字段应被清空），和实现不符。
:::

**不能更新的字段**：`id`、`equipment_id`、`created_at`、`deleted` ——
更新走的是一张写死的白名单，请求体里带了也没用。

::: warning 这是防 Mass Assignment 漏洞
如果直接用 `updateById(entity)`，实体里所有非 null 字段都会被写进去 ——
前端传一个 `id` 就能把主键改掉，传一个 `created_at` 就能伪造创建时间。
:::

失败情况：

| 输入 | 响应 |
|---|---|
| 不存在的 id | `404` |
| `status: "XXX"` | `400` `状态只能是 OPEN 或 RESOLVED` |
| 空请求体 `{}` | `200`（什么都不改） |

### 3.5 删除工单

```
DELETE /api/repair-orders/{id}
```

| 情况 | 响应 |
|---|---|
| 删除成功 | **204**（无响应体） |
| 不存在 | `404` |
| 已经删过了 | `404` |

::: definition 这是软删除
接口返回 204 后，数据**并没有从表里消失**：

```sql
UPDATE repair_order SET deleted = 1 WHERE id = ? AND deleted = 0
```

它只是被标记，并在所有查询里被排除。**DBA 仍然查得到，可以恢复。**
:::

---

## 四、删除策略

### 4.1 工单：软删除

| 问题 | 回答 |
|---|---|
| 为什么不物理删除？ | 工单是**业务凭证** —— 谁在什么时候报了什么故障、谁处理的，是追责和统计的依据 |
| 数据还在吗？ | 在，`deleted = 1` |
| 怎么恢复？ | `UPDATE repair_order SET deleted = 0 WHERE id = ?` |
| 表会不会越来越大？ | 会。真实项目要按时间归档（本项目暂不做） |
| 哪些查询会自动排除已删的？ | MP 自动生成的（`selectById` / 分页 count / `deleteById`）；**手写 XML 需要自己加 `deleted = 0`** |

### 4.2 设备：不提供删除

**当前没有任何删除设备的接口。** 这是有意为之。

### 4.3 删除设备会发生什么（如果真去删）

设备表被工单以外键引用，且外键**没有 `ON DELETE` 子句**，MySQL 默认行为是 **`NO ACTION`（等同 `RESTRICT`）**：

```sql
DELETE FROM equipment WHERE code = 'EQ-A001';
-- ERROR 1451 (23000): Cannot delete or update a parent row:
--   a foreign key constraint fails ...
```

| 设备状态 | 删除结果 |
|---|---|
| **有工单引用** | **被拒绝**（`ERROR 1451`） |
| 无任何工单引用 | 允许删除（但当前没有接口能发起） |
| 改 `code`（有工单引用时） | 同样被拒绝 —— 这正是改成 `id` 代理主键的原因 |

::: warning 为什么这样设计
设备是**长期存在的实体**，工单是它的历史记录。

物理删掉设备会让历史工单指向空气 —— 所以数据库层面就把它挡住了。
真要"下线"一台设备，正确做法是改 `state`（如 `ARCHIVED`），而不是删行。
:::

### 4.4 已知问题：设备的软删除与唯一约束冲突

如果将来给设备加软删除，会和 `code` 的 `UNIQUE` 约束打架：

```
EQ-A001 被软删除（deleted=1）→ 那一行还在表里
再新增一台编号 EQ-A001 的设备 → ERROR 1062 Duplicate entry
```

Official 推荐解法是让"已删除的值各不相同"：

```java
@TableLogic(value = "0", delval = "id")
private Long deleted;
```

删除时执行 `UPDATE equipment SET deleted = id WHERE id = ?`，每条已删记录的 `deleted` 都不同，唯一约束就不会撞。

**本项目暂未实现**，留作后续设计。

---

## 五、已知限制

| # | 限制 | 计划 |
|---|---|---|
| 1 | 数据库密码明文写在 `application.properties` | W07 配置外置 |
| 2 | 无"新增设备"接口，重复编号只在数据库层验证 | 暂无计划 |
| 3 | 兜底异常处理器可能吞掉未注册的 Spring 内建异常 | W07 继承 `ResponseEntityExceptionHandler` |
| 4 | 设备软删除与唯一约束冲突 | 见 4.4，待设计 |
| 5 | 无鉴权，任何调用方都能改/删 | W07 |
| 6 | 无并发控制（同一工单被两人同时改，后写的覆盖先写的） | 可引入 `@Version` 乐观锁 |
