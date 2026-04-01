# 足球球队考勤管理系统 - 规格说明书

## 1. 项目概述

- **项目名称**: Football Team Attendance Management System (足球球队考勤管理系统)
- **项目类型**: 全栈 Web 应用 (Vue + Spring Boot + SQLite)
- **核心功能**: 管理球队成员、记录每周足球比赛出勤、生成考勤排名汇总
- **目标用户**: 球队管理员/领队

## 2. 技术架构

### 后端
- **框架**: Spring Boot 3.x
- **语言**: Java 17
- **数据库**: SQLite (嵌入式，无须独立部署)
- **ORM**: MyBatis-Plus

### 前端
- **框架**: Vue 3
- **构建工具**: Vite
- **UI 库**: Element Plus
- **状态管理**: Pinia
- **HTTP 客户端**: Axios

### 架构风格
- RESTful API
- 分层架构 (Controller -> Service -> Mapper)

## 3. 功能模块

### 3.1 球员管理
- 添加球员 (姓名、手机号)
- 编辑球员信息
- 删除球员
- 查看球员列表

### 3.2 比赛管理
- 创建比赛 (日期、地点)
- 编辑比赛信息
- 删除比赛
- 查看比赛列表

### 3.3 考勤记录
- 选择比赛场次
- 录入参赛球员 (出勤/请假)
- 编辑考勤记录
- 查看单场比赛考勤详情

### 3.4 考勤汇总
- 统计每位球员的出勤率
- 按出勤率排名
- 显示总比赛场次、出勤次数、请假次数

## 4. 数据模型

### Player (球员)
| 字段 | 类型 | 说明 |
|------|------|------|
| id | Long | 主键 |
| name | String | 姓名 |
| phone | String | 手机号 |
| createdAt | LocalDateTime | 创建时间 |

### Match (比赛)
| 字段 | 类型 | 说明 |
|------|------|------|
| id | Long | 主键 |
| matchDate | LocalDate | 比赛日期 |
| location | String | 比赛地点 |
| createdAt | LocalDateTime | 创建时间 |

### Attendance (考勤)
| 字段 | 类型 | 说明 |
|------|------|------|
| id | Long | 主键 |
| matchId | Long | 比赛ID |
| playerId | Long | 球员ID |
| status | String | PRESENT(出勤)/ABSENT(请假) |
| remark | String | 备注 |

## 5. API 接口

### 球员管理
- `GET /api/players` - 获取球员列表
- `POST /api/players` - 添加球员
- `PUT /api/players/{id}` - 更新球员
- `DELETE /api/players/{id}` - 删除球员

### 比赛管理
- `GET /api/matches` - 获取比赛列表
- `POST /api/matches` - 创建比赛
- `PUT /api/matches/{id}` - 更新比赛
- `DELETE /api/matches/{id}` - 删除比赛

### 考勤管理
- `GET /api/attendances/match/{matchId}` - 获取某场比赛考勤
- `POST /api/attendances` - 提交/更新考勤
- `GET /api/attendances/summary` - 获取考勤汇总排名

## 6. 页面结构

```
/                - 首页仪表盘
/players         - 球员管理页面
/matches         - 比赛管理页面
/attendance      - 考勤记录页面
/report          - 考勤汇总排名页面
```

## 7. 验收标准

1. 可以正常添加、编辑、删除球员
2. 可以创建、编辑、删除比赛
3. 可以为每场比赛录入球员出勤/请假状态
4. 考勤汇总页面正确显示所有球员的出勤率排名
5. SQLite 数据库文件正确创建并持久化数据
6. 前后端联调正常，API 调用无跨域问题
