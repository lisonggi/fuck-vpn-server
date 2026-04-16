# fuck-vpn-server

`fuck-vpn-server` 是一个基于 Kotlin + Spring Boot 的后端服务，负责管理插件、节点、密钥和订阅。它支持插件自动加载，并提供 REST API 供前端控制台调用。

相关项目：
- [fuck-vpn-plugin-api](https://github.com/lisonggi/fuck-vpn-plugin-api)
- [fuck-vpn-web](https://github.com/lisonggi/fuck-vpn-web)

## 核心功能

- 插件自动扫描与加载
- 节点管理与刷新
- 密钥池管理与过期更新
- 订阅生成与使用统计
- 后端运行状态控制

## 技术栈

- Kotlin 2.3.20
- Spring Boot 4.0.5
- Java 21
- Gradle Wrapper
- Kotlin Serialization / Jackson
- Spring WebMVC / WebSocket

## 环境要求

- JDK 21
- Gradle Wrapper 已集成

## 快速启动

```bash
./gradlew bootRun
```

构建可执行 Jar：

```bash
./gradlew clean build
java -jar build/libs/fuck-vpn-server-0.0.1-SNAPSHOT.jar
```

运行测试：

```bash
./gradlew test
```

## 默认配置

- 默认服务监听端口：`8080`
- 默认后端地址：`http://localhost:8080`

## 后端接口概览

- 插件管理
  - `GET /plugin/getAllPlugin`
  - `GET /plugin/{id}`
- 运行状态
  - `GET /{id}/getRunState`
  - `PUT /{id}/updateRunState`
- 密钥管理
  - `GET /{id}/getKeyState`
  - `GET /{id}/getKeys`
  - `POST /{id}/useKey`
  - `POST /{id}/refreshKeys`
  - `PUT /{id}/updateKeyConfig`
- 节点管理
  - `GET /{id}/getNodeState`
  - `GET /{id}/getNodes`
  - `POST /{id}/refreshNodes`
  - `PUT /{id}/updateNodeConfig`
- 订阅管理
  - `GET /{id}/getSubState`
  - `PUT /{id}/updateSubState`
  - `GET /{id}/getSub/{uuid}`
  - `GET /{id}/getAllSub`
  - `PUT /{id}/updateSub/{uuid}`
  - `DELETE /{id}/removeSub/{uuid}`
  - `POST /{id}/addSub`
  - `GET /{id}/useSub/{uuid}`

> `id` 表示插件或服务标识，用于区分不同插件实例。

## 插件开发说明

`fuck-vpn-server` 依赖 `fuck-vpn-plugin-api` 提供的插件接口定义。

- `NodePlugin`：提供 `getPluginInfo()` 和 `generateNodes()`。
- `KeyPlugin`：继承 `NodePlugin`，额外提供 `generateKey()`。
- 使用 `@VPNPlugin` 注解标记插件实现类。
- 数据模型包括 `PluginInfo`、`NodeData`、`KeyData`。

### 数据模型说明

- `PluginInfo`：插件元信息，包含 id、name、version、author、description。
- `NodeData`：节点数据模型，支持 `data`、`createTime`、`getViewText()` 和 `toSubscription(keyData)`。
- `KeyData`：密钥数据模型，支持 `data`、`createTime`、`getExpireAt()` 和 `getViewText()`。

## 项目结构

- `src/main/kotlin`：后端主程序、控制器与服务实现
- `plugins/`：插件实现目录
- `plugin-configs/`：插件配置文件
- `src/main/resources/application.properties`：Spring Boot 配置

## 说明

本项目负责后端业务与插件扩展，不包含前端展示逻辑。
