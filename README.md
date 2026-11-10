# 校园食堂菜品评价系统(RestaurantDishes)

一个基于 Spring Boot 的校园食堂菜品评价系统后端服务,提供用户注册登录、菜品浏览与搜索、热门排行、菜品评价等功能。

## 功能特性

- **用户认证**:注册、登录、退出登录
  - 密码使用 BCrypt 加密存储,任何接口均不返回密码
  - JWT(2 小时有效期)+ Redis 会话白名单双重校验,退出登录后令牌立即失效
- **菜品浏览**:分页查询、关键词搜索、按食堂/分类筛选、多种排序(最新 / 评分 / 评价数)
- **热门排行**:按评价数量和平均评分综合排序
- **菜品详情与评价**:菜品详情、分页查看菜品评价
- **统一响应结构**:`{ code, message, data }`,配合全局异常处理与参数校验
- **单元测试**:控制器、服务、配置层均有测试覆盖

> 📋 完整规划共 16 个接口(含发表/修改/删除评价、我的评价、管理员菜品管理等),详见 [接口文档](docs/校园食堂菜品评价系统-接口文档.md)。当前已实现其中 7 个,其余功能开发中。

## 技术栈

| 类别 | 技术 |
|---|---|
| 框架 | Spring Boot 3.3.4(Java 23) |
| ORM | MyBatis-Flex 1.11.8 |
| 数据库 | MySQL 8.0 |
| 缓存/会话 | Redis |
| 认证 | JWT(java-jwt 4.4.0)+ BCrypt(spring-security-crypto) |
| 校验 | spring-boot-starter-validation |
| 工具 | Lombok、Maven Wrapper |

## 项目结构

```
src/main/java/com/spring/restaurantdishes/
├── RestaurantDishesApplication.java   # 启动类
├── config/         # 配置类(BCrypt Bean、拦截器注册)
├── controller/     # 控制器(DishController、userController)
├── service/        # 业务接口与实现
├── Mapper/         # MyBatis-Flex Mapper
├── model/          # 数据模型(Result、PageBean、DTO 等)
│   ├── dataBasePojo/   # 数据库实体(user、dish、review)
│   └── RequestDetails/ # 请求参数对象
├── Interceptor/    # JWT 登录拦截器
├── utils/          # JWT 工具、ThreadLocal 工具
└── ecception/      # 全局异常处理
docs/               # 接口文档、数据库设计文档、开发计划
```

## 快速开始

### 环境要求

- JDK 23
- Maven 3.8+
- MySQL 8.0+
- Redis(默认 `localhost:6379`,无需密码)

### 1. 初始化数据库

仓库 `sql/` 目录提供统一的建库脚本与演示数据,每个开发者在自己本地的 MySQL 上执行一次:

```bash
mysql -uroot -p < sql/schema.sql   # 建库建表(会清空重建 restaurant 库的表,仅用于开发库)
mysql -uroot -p < sql/data.sql     # 导入演示数据(3 个账号、5 个菜品、3 条评价)
```

> 演示账号:`admin` / `student01` / `ganfanren`,密码均为 `123456`。
>
> 合作开发约定:改了表结构就更新 `sql/schema.sql`(新增演示数据则更新 `sql/data.sql`)并提交,其他人 pull 后重新执行脚本即可保持库结构一致。

### 2. 修改配置

数据库配置已拆到 `application-local.yml`(每人本地一份,已被 `.gitignore` 忽略,不会提交)。复制模板并填入你自己的 MySQL 密码:

```bash
cp src/main/resources/application-local.yml.example src/main/resources/application-local.yml
```

```yaml
# src/main/resources/application-local.yml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/restaurant?useUnicode=true&characterEncoding=utf-8&serverTimezone=Asia/Shanghai&allowMultiQueries=true
    username: root
    password: <你的密码>
```

### 3. 启动

```bash
./mvnw spring-boot:run        # Linux / macOS
mvnw.cmd spring-boot:run      # Windows
```

服务默认运行在 `http://localhost:8080`,所有接口以 `/api` 为前缀。

### 4. 运行测试

```bash
mvnw test
```

## API 概览(已实现)

| 方法 | 路径 | 权限 | 说明 |
|---|---|---|---|
| POST | `/api/auth/register` | 公开 | 用户注册 |
| POST | `/api/auth/login` | 公开 | 用户登录,返回 JWT 与用户信息 |
| POST | `/api/auth/logout` | 登录 | 退出登录,删除 Redis 会话 |
| GET | `/api/dishes` | 公开 | 菜品分页查询(支持 keyword/canteenName/category/sort) |
| GET | `/api/dishes/hot` | 公开 | 热门菜品排行 |
| GET | `/api/dishes/{id}` | 公开 | 菜品详情 |
| GET | `/api/dishes/{id}/reviews` | 公开 | 菜品评价分页 |

完整的接口定义(含 16 个规划接口的请求/响应示例、错误码)见 [接口文档](docs/校园食堂菜品评价系统-接口文档.md)。

### 认证方式

需要登录的接口携带请求头:

```http
Authorization: Bearer <token>
```

登录成功后,后端同时写入 Redis 会话(`canteen:session:{jti}`,TTL 2 小时);拦截器校验 JWT 签名与 Redis 会话,退出登录会删除对应会话,令牌立即失效。

### 统一响应结构

```json
{
  "code": 0,
  "message": "success",
  "data": {}
}
```

`code` 为 `0` 表示成功;`400` 参数错误、`401` 未登录、`403` 无权限、`404` 数据不存在、`409` 数据冲突。

### 快速体验

使用文档中的演示账号(密码均为 `123456`):

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"student01","password":"123456"}'
```

## 文档

- [接口文档](docs/校园食堂菜品评价系统-接口文档.md) — API 设计、请求/响应示例、错误码
- [数据库设计文档](docs/校园食堂菜品评价系统-数据库设计文档.md) — 表结构、Redis 键设计、建表 SQL、初始化数据
