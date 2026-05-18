# CLAUDE.md

本文件为 Claude Code (claude.ai/code) 在此代码库中工作提供指导。

## 构建命令

```bash
# 构建调试版 APK
./gradlew assembleDebug

# 构建发布版 APK（启用代码混淆）
./gradlew assembleRelease

# 运行所有测试
./gradlew test
./gradlew connectedAndroidTest

# 运行特定模块的测试
./gradlew :app:test
./gradlew :app:connectedAndroidTest
```

后端 API 基础 URL 为 `http://192.168.31.224:8080/api/`（定义在 `HttpClient.kt` 中）。测试其他环境时需要更新此
URL。

## 架构

### 分层结构

```
io.github.touko/
├── data/
│   ├── local/          # 本地持久化（Room、MMKV、DataStore）
│   ├── remote/         # 网络层（Retrofit、WebSocket、拦截器）
│   └── model/          # 请求/响应 DTO
├── feature/            # 功能模块（业务逻辑）
│   ├── chat/
│   ├── home/
│   ├── login/
│   ├── register/
│   └── profile/
├── navigation/         # 全局导航状态
└── ui/                 # 共享 UI 组件和主题
```

### 导航

- 使用 **Jetpack Navigation v3** with `NavDisplay` 和 `NavEntry`
- **NavigatorManager** 是一个单例，具有可变的 `backStack` 列表
- 入口点：`MainActivity.kt` - 根据认证状态决定起始页面
- 页面在 `navigation/route.kt` 中定义为密封类：`LoginPage`、`RegisterPage`、`MainPage`、`ChatPage`、
  `ProfilePage`
- 在根视图双击返回键可退出应用

### 状态管理

**全局状态：**

- `NavigatorManager.backStack` - 导航栈
- `LocalUserManager` - 通过 MMKV 存储用户凭据的单例
- `TokenManager` - JWT 令牌存储，401 时自动重定向到登录页

**功能本地状态：**

- 每个功能模块都有 `state/` 包，包含对象（`CurrentUserState`、`FriendState`、`ChatState`、`ProfileState`）
- 简单的 Kotlin 对象，属性由 ViewModel 更新

**本地持久化：**

- `MMKV` 用于用户凭据（`getUid()`、`getUsername()`）和偏好设置
- `TokenManager` 用于 JWT 令牌，401 时自动重定向到登录页
- `Room 3` 用于消息历史（`MessageEntity` + `MessageDao`）

**ViewModels：**

- 每个功能模块都有一个 ViewModel，具有 `mutableStateOf` 状态和业务逻辑
- `HomeViewModel` 每秒轮询 API 获取好友列表更新

### 网络通信

- **Retrofit 3**，通过版本目录使用 Kotlin 序列化转换器
- 三个 API 客户端：`userApi`、`friendApi`、`messageApi`
- **GlobalInterceptor** 添加 Authorization 头，401 时清除令牌，重定向到登录页
- 调试版本中启用日志拦截器
- **WebSocket** 位于 `ws://192.168.31.224:8080/ws`，通过 `ChatWebSocketManager` 进行实时消息通信

### 实时消息

- `ChatWebSocketManager` 在应用启动时连接 userId
- 使用 `MutableSharedFlow<Message>` 接收消息
- `ChatRepository` 通过 Flow 观察将消息持久化到 Room DB

## 关键文件

- `App.kt` - 应用单例，初始化 Room 和 MMKV
- `MainActivity.kt` - 导航入口点，处理返回手势和双击退出
- `HttpClient.kt` - Retrofit 实例、基础 URL、认证拦截器
- `NavigatorManager.kt` - 全局导航状态（单例，具有 `goTo()` 和 `back()` 方法）
- `LocalUserManager.kt` - 用户会话管理，登出时清除状态并重启 MainActivity
- `TokenManager.kt` - JWT 令牌存储和检索
- `route.kt` - 密封类 `Page`，包含所有导航页面
- `WebSocketClient.kt` - 实时聊天的 WebSocket 连接管理

