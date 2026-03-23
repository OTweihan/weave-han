# WeaveHan Backend

WeaveHan 后端项目，服务于个人博客系统与后台管理能力。

## 项目说明

- 项目名称：`WeaveHan`
- 技术栈：Java 21 / Spring Boot 3 / Maven / MyBatis-Plus / Sa-Token / Redisson
- 项目类型：前后端分离博客系统后端
- 仓库地址：<https://github.com/OTweihan/weave-han>

## 项目背景

本项目基于 [RuoYi-Vue-Plus](https://github.com/dromara/RuoYi-Vue-Plus) 的能力进行定制与演进，当前主要面向个人博客系统与相关后台管理场景。

说明：

- 当前代码以个人项目实际需求为主
- 与上游框架存在持续演进和定制差异
- 上游框架更新可按需择机同步，不追求完全一致

## 目录结构

```text
weave-han/
├── han-admin/      # 管理后台启动模块
├── han-common/     # 通用能力模块
├── han-extend/     # 扩展能力模块
├── han-modules/    # 业务模块
└── script/         # 脚本与相关资源
```

其中主要业务模块包括：

- `han-system`
- `han-blog`

## 环境要求

- JDK 21
- Maven 3.9+
- MySQL
- Redis

## 本地开发

### 1. 安装依赖并构建

```bash
mvn clean package -DskipTests
```

### 2. 配置运行环境

根据本地环境准备数据库、Redis 和相关配置。

### 3. 启动项目

可使用 IDEA 直接启动对应启动模块，也可按当前 Maven / Spring Boot 配置运行。

## 开发说明

- 默认使用 Maven 多模块结构维护后端能力
- 博客相关业务代码主要位于 `han-modules/han-blog`
- 系统与公共能力按模块拆分，便于扩展与维护

## 关联仓库

- 前端仓库：<https://github.com/OTweihan/weave-han-ui>

## 备注

这是个人长期维护项目，README 会随着项目演进持续更新。
