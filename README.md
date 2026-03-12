# Fuck VPN

> 一个 **模块化、插件化、可扩展的 VPN / Proxy 管理系统**

Fuck VPN 提供一个统一的服务端管理框架，通过插件方式扩展不同协议节点，实现 **服务器、节点、密钥、订阅** 的统一管理。

项目目标是构建一个 **简单、高度可扩展的 VPN 管理平台**。

⚠️ 项目仍处于开发阶段，接口与功能可能持续调整。

---

# ✨ 项目特点

* 🔌 **插件架构**

  * 支持 Node / Key 插件扩展
  * 自动扫描并加载插件
  * 支持运行时扩展协议

* ⚡ **模块化设计**

  * 节点服务模块
  * 密钥服务模块
  * 订阅生成模块

* 🌐 **Web 管理界面**

  * 提供完整的节点与服务器管理 UI

* 📡 **REST API**

  * 提供完整 HTTP API

* 🧩 **高度可扩展**

  * 可支持任意代理协议
  * 可自定义密钥生成方式

---

# 🚀 核心功能

系统围绕 **四个核心管理模块**构建：

## 1. 订阅管理 (Subscription)

为客户端提供统一的订阅接口。

支持：

* 自动生成客户端订阅
* 聚合多个节点
* 动态生成配置
* 支持不同客户端格式

示例接口：

GET /subscribe/{id}

---

## 2. 服务器管理 (Server Management)

管理服务器运行状态与服务信息。

功能包括：

* 查看服务器状态
* 查看已加载插件
* 查询节点服务
* 查询密钥服务
* 控制服务运行状态

示例接口：

GET /manager/info
POST /manager/setStatus/{id}

---

## 3. 密钥管理 (Key Management)

密钥服务用于生成或管理访问密钥。

支持：

* 创建密钥
* 删除密钥
* 查询密钥
* 不同协议密钥生成

密钥服务通过插件实现。

插件需实现接口：

KeyService

---

## 4. 节点管理 (Node Management)

用于管理 VPN / Proxy 节点实例。

支持：

* 创建节点
* 删除节点
* 启动节点
* 停止节点
* 查询节点状态

节点服务通过插件实现。

插件需实现接口：

NodeService

---

# 🧩 插件系统

系统支持 **自动加载插件**。

插件只需要：

1. 引入 `fuck-vpn-plugin-api`
2. 实现插件接口
3. 添加 `@VPNPlugin` 注解
4. 打包为 jar

示例：

@VPNPlugin(
id = "demo",
name = "Demo Node"
)
class DemoNodeService : NodeService

编译后将插件放入：

plugins/

系统启动时会自动加载。

---

# 🏗 项目结构

```
fuck-vpn
│
├─ fuck-vpn-server      后端服务 (Spring Boot)
│
├─ fuck-vpn-plugin-api  插件接口定义
│
├─ fuck-vpn-web         Web 管理界面
│
└─ plugins              插件目录
```

---

# ⚙️ 技术栈

后端：

* Java 21
* Kotlin
* Spring Boot
* Gradle

前端：

* Web UI

---

# 🌐 Web 管理界面

Web 项目位于：

fuck-vpn-web

当前 **Web 前端中的功能即后端需要实现的管理功能**。

也就是说：

Web 界面中的服务器管理、节点管理、密钥管理、订阅管理等功能，后端 API 都会逐步实现。

⚠️ 目前 Web 版本 **尚未完全匹配最新后端 API**，需要等待后续版本更新。

---

# 🚀 启动项目

克隆仓库：

git clone [https://github.com/lisonggi/fuck-vpn.git](https://github.com/lisonggi/fuck-vpn.git)
cd fuck-vpn

启动后端：

cd fuck-vpn-server
./gradlew bootRun

Windows：

gradlew.bat bootRun

默认端口：

8080

---

# 🔧 插件开发

插件需要依赖：

fuck-vpn-plugin-api

实现以下接口之一：

NodeService
KeyService

并添加注解：

@VPNPlugin

插件打包后放入：

plugins/

目录即可自动加载。

---

# 🛣 开发计划

未来计划：

* Web UI 完善
* 插件热加载
* 插件市场
* 节点监控
* Docker 部署
* API 文档自动生成

---

# 🤝 贡献

欢迎提交：

* Issue
* Pull Request
* 插件扩展

---

# 📜 License

MIT License

---

# ⚠️ Disclaimer

本项目仅用于学习和研究。

使用本软件请遵守当地法律法规。
