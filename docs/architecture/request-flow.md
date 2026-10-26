# 一次请求的数据流

## 1. 场景

用户请求"给 EQ-A001 建一条工单"

## 2. 请求

（方法 + 路径 + 请求体 JSON）
POST请求 /api/repair_order
{       
"equipment_code" : "EQ-A001" ,
"site_id" : "SITE-A" ,      
"issue_summary" : "模拟状态页出现黄灯" ,
"priority" : "NORMAL" 
}

## 3. 链路（重点）

HTTP 请求
→ Controller （这一层干什么？）
从请求体读出 equipment_code / issue_summary / priority 三个字段，
把 JSON 转成工单对象，再交给 Service；最后把结果转成 JSON + 状态码

→ Service （这一层干什么？）
按编号查设备
├ 查不到 → 404（设备不存在，不能给不存在的设备建单）
└ 查到了 → 组装工单对象 → 交给 Repository 保存


→ Repository （这一层干什么？）
管理执行数据库SQL指令，处理数据库的返回值


→ 数据库 （执行了什么 SQL？）
执行
INSERT INTO repair_order(id, equipment_code, site_id, issue_summary, priority, status, created_at)
VALUES ('RO-1001', 'EQ-A001', 'SITE-A', '模拟状态页出现黄灯', 'NORMAL', 'OPEN','2026-01-14 09:00:00');

← 一路返回

## 4. 响应

（状态码 + 响应体 JSON）
201 Created
Location: /api/repair-orders/RO-1001
{
"id": "RO-1001",
"equipment_code": "EQ-A001",
"status": "OPEN",
"priority": "NORMAL",
"created_at": "2026-01-20 09:00:00"
}
## 5. 失败情况（至少 4 种）

（什么情况 → 哪一层拒绝 → 返回什么状态码）
参数不对或者缺少了，比如id输错了——>400 Controller层拒绝
资源不存在——>404 Service
服务器连接出错——>500 框架兜底
编号重复 -->409 Service返回
