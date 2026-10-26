# 一次请求的数据流

## 1. 场景
用户请求"给 EQ-A001 建一条工单"

## 2. 请求
（方法 + 路径 + 请求体 JSON）
POST请求


## 3. 链路（重点）
HTTP 请求
→ Controller   （这一层干什么？）
控制访问层，用来接收用户从浏览器传过来的数据

→ Service      （这一层干什么？）
服务层，用于提供接口，这里不会写具体实现

→ Repository   （这一层干什么？）
管理执行数据库指令

→ 数据库        （执行了什么 SQL？）
执行
INSERT INTO repair_order(id, equipment_code, site_id, issue_summary, priority, status, created_at)
VALUES ('RO-1001', 'EQ-A001', 'SITE-A', '模拟状态页出现黄灯', 'NORMAL', 'OPEN','2026-01-19 09:00:00');

← 一路返回

## 4. 响应
（状态码 + 响应体 JSON）
添加成功返回201；


## 5. 失败情况（至少 4 种）
（什么情况 → 哪一层拒绝 → 返回什么状态码）
参数不对或者缺少了，比如id输错了——>400 数据库拒绝
资源不存在——>404 数据库拒绝
服务器连接出错——>500 Controller层拒绝
