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

后端 API 基础 URL 在 `build.gradle.kts` 中定义：

- **调试版本**：`192.168.31.224:8080`
- **发布版本**：`cn-qz-plc-1.ofalias.net:52396`

测试其他环境时需要更新 `build.gradle.kts` 中的 `DOMAIN` 字段，并重新构建。

## 架构

### 分层结构

```
io.github.touko/
├── data/
│   ├── local/          # 本地持久化（Room、MMKV、Entity/Mapper）
│   ├── remote/         # 网络层（Retrofit、WebSocket、Interceptor）
│   ├── model/          # 请求/响应 DTO（Kotlin Serialization）
│   └── remote/api/     # Retrofit API 接口定义
├── feature/            # 功能模块（UI + ViewModel + State）
│   ├── chat/           # 聊天页面
│   ├── home/           # 首页（好友列表、申请列表）
│   ├── login/          # 登录页面
│   ├── register/       # 注册页面
│   └── profile/        # 个人中心
├── navigation/         # 全局导航（NavEntry、Page、NavigatorManager）
└── ui/                 # 共享 UI 组件和主题
```

**重要说明**：`feature/` 模块不是独立的 Gradle 子模块，而是 `app/` 模块内的代码组织。

### 导航

- 使用 **Jetpack Navigation v3** with `NavDisplay` 和 `NavEntry`
- **NavigatorManager** 是一个单例，具有可变的 `backStack` 列表
- 入口点：`MainActivity.kt` - 根据认证状态决定起始页面
- 页面在 `navigation/route.kt` 中定义为密封类：`LoginPage`、`RegisterPage`、`MainPage`、`ChatPage`、`ProfilePage`
- 在根视图双击返回键可退出应用
- `ChatPage` 带有参数 `userId` 和 `userName`

### 状态管理

**全局状态：**

- `NavigatorManager.backStack` - 导航栈
- `LocalUserManager` - 通过 MMKV 存储用户凭据的单例
- `TokenManager` - JWT 令牌存储，401 时自动重定向到登录页

**功能本地状态：**

- `CurrentUserState`（home/state/）- 当前用户 ID、用户名和好友关系状态（FRIEND/PENDING/NONE）
- `ProfileState`（profile/）- 个人信息状态
- 所有状态使用 `mutableStateOf` 或 `MutableStateFlow`，通过 Compose 和 ViewModel 共享

**本地持久化：**

- `MMKV` 用于用户凭据（`getUid()`、`getUsername()`）和偏好设置
- `TokenManager` 用于 JWT 令牌，401 时自动重定向到登录页
- `Room 3` 用于消息历史（`MessageEntity` + `MessageDao`）
- `Mapper` 模式：`MessageMapper.toEntity()` 用于 DTO 与 Entity 转换

**ViewModels：**

- 每个功能模块都有一个 ViewModel，具有 `mutableStateOf` 状态和业务逻辑
- `HomeViewModel` 每秒轮询 API 获取好友列表更新

### 网络通信

- **Retrofit 3**，通过版本目录使用 Kotlin 序列化转换器
- 版本目录文件位于 `gradle/libs.versions.toml`

主要依赖版本：

- Kotlin: 2.3.21
- Compose BOM: 2026.05.00
- Room 3: 3.0.0-alpha04
- Navigation 3: 1.1.1
- MMKV: 2.4.0
- 三个 API 客户端：`userApi`、`friendApi`、`messageApi`
- **GlobalInterceptor** 添加 Authorization 头，401 时清除令牌，重定向到登录页
- 调试版本中启用日志拦截器
- **WebSocket** 位于 `ws://192.168.31.224:8080/ws`，通过 `ChatWebSocketManager` 进行实时消息通信

### 实时消息

- `ChatWebSocketManager` 单例管理 WebSocket 连接，在应用启动时自动连接当前用户
- 使用 `MutableSharedFlow<Message>` 接收消息，`ChatViewModel` 监听此 Flow
- `ChatViewModel.onReceiveMessage()` 接收 WebSocket 消息后通过 `MessageRepository.saveMessage()` 持久化
- **AppLifecycleObserver** 监听应用前后台切换，应用回到前台时自动重连 WebSocket，进入后台时主动断开
- **自动重连机制**：WebSocket 断开时按指数退避策略重连（2秒、4秒、8秒...最多30秒），最多尝试10次
- **双击退出**：在 MainActivity 根视图双击返回键可退出应用

## 关键文件

- `App.kt` - 应用单例，初始化 Room 数据库和 MMKV
- `MainActivity.kt` - 导航入口点，处理返回手势和双击退出
- `HttpClient.kt` - Retrofit 实例、基础 URL、认证拦截器
- `NavigatorManager.kt` - 全局导航状态（单例，具有 `goTo()` 和 `back()` 方法）
- `LocalUserManager.kt` - 用户会话管理，登出时清除状态并重启 MainActivity
- `TokenManager.kt` - JWT 令牌存储和检索
- `route.kt` - 密封类 `Page`，包含所有导航页面
- `ChatWebSocketManager.kt` - WebSocket 连接管理、消息 Flow 和自动重连
- `AppLifecycleObserver.kt` - 应用前后台切换监听，自动管理 WebSocket 连接

### Room 数据库

- 使用 `AppDataBase` 初始化数据库，路径为 `getDatabasePath("touko.db")`
- `MessageEntity` 表示消息记录，包含 messageId、senderId、receiverId、content、createTime
- `MessageDao` 使用 `observeMessageByFriendId()` 和 `observeLastMessages()` 提供 Flow 接口
- `MessageMapper` 提供 `toEntity()` 方法进行 DTO ↔ Entity 转换

