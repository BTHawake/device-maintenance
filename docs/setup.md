# 环境与启动说明

## 1. 环境版本

| 项 | 版本    | 怎么查到的       |
|---|-------|-------------|
| JDK | 25.0.1   | 命令行         |
| Maven | 3.9.11 | 命令行         |
| Spring Boot | 3.5.16     | pom的parent里 |
| MySQL | 8.0.34     | 命令行         |

## 2. 前置准备

- 数据库叫什么名字？
  device_maintenance
- 建表和种子数据要跑哪个文件？用什么命令？
  cd D:\WorkSpace\Java_Agent_Python_Complete_Roadmap\exercises\d1\untitled\infra\db
  mysql --login-path=course --default-character-set=utf8mb4 -e "source schema.sql;"
  mysql --login-path=course --default-character-set=utf8mb4 -e "source seed.sql;"


- 连接数据库用什么账号方式？（提示 login-path）
  mysql --login-path=course

## 3. 启动

- 在哪个目录执行？ D:\WorkSpace\Java_Agent_Python_Complete_Roadmap\exercises\d1\untitled
- 敲什么命令？mvn spring-boot:run
- 端口是多少？8080

## 4. 启动成功的标志

从日志里抄 2–3 行（能证明成功的那些）
Started Application in 1.104 seconds (process running for 1.32)
2026-10-26T15:08:55.798+08:00  INFO 20384 --- [           main] o.a.c.c.C.[Tomcat].[localhost].[/]       : Initializing Spring embedded WebApplicationContext
2026-10-26T15:08:56.086+08:00  INFO 20384 --- [           main] o.s.b.w.embedded.tomcat.TomcatWebServer  : Tomcat started on port 8080 (http) with context path '/'

## 5. 怎么停止
ctrl+c

## 6. 本阶段的已知限制
没有鉴权；工单号重启后会重置，可能和上次撞号
数据没有放数据库里而是存在内存的map里。
