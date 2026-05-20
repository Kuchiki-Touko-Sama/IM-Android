package io.github.touko.data.remote

import android.util.Log
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import io.github.touko.BuildConfig
import io.github.touko.data.local.LocalUserManager
import io.github.touko.data.model.request.SendMessageRequest
import io.github.touko.data.model.response.Message
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import kotlin.math.pow

object ChatWebSocketManager {
    private val client = OkHttpClient() // 建议在真实项目中通过依赖注入（如 Hilt）复用全局 client
    private const val BASE_WS_URL = "ws://${BuildConfig.DOMAIN}/ws"
    private val managerScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    @Volatile
    private var webSocket: WebSocket? = null

    @Volatile
    private var reconnectAttempt = 0

    @Volatile
    private var isManualClose = false
    private var reconnectJob: Job? = null

    val messageFlow = MutableSharedFlow<Message>(extraBufferCapacity = 64)

    @Synchronized
    fun connect() {
        val uid = LocalUserManager.getUid()
        if (uid == 0) return
        isManualClose = false
        reconnectJob?.cancel()

        webSocket?.close(1000, "切换或重新连接")
        webSocket = null

        val request = Request.Builder()
            .url("${BASE_WS_URL}/chat?userId=$uid")
            .build()

        webSocket = client.newWebSocket(
            request,
            listener = object : WebSocketListener() {
                override fun onOpen(webSocket: WebSocket, response: Response) {
                    Log.d("my-im", "WebSocket 连接成功")
                    reconnectAttempt = 0
                }

                override fun onMessage(webSocket: WebSocket, text: String) {
                    try {
                        val message = Json.decodeFromString<Message>(text)
                        val dispatched = messageFlow.tryEmit(message)
                        if (!dispatched)
                        // 缓冲区满降级到协程中推送消息
                            managerScope.launch { messageFlow.emit(message) }
                    } catch (e: Exception) {
                        Log.e("my-im", "解析消息失败", e)
                    }
                }

                override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                    Log.e("my-im", "WebSocket 连接异常: ${t.message}")
                    if (!isManualClose)
                        reconnect()
                }

                override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                    Log.d("ChatWebSocketManager", "WebSocket 已关闭: $reason")
                }
            }
        )
    }

    @Synchronized
    private fun reconnect() {
        if (isManualClose) return
        if (reconnectJob?.isActive == true) return

        val delayTime = (2.0.pow(reconnectAttempt).toLong() * 1000).coerceAtMost(30000)
        reconnectAttempt++
        reconnectJob = managerScope.launch {
            delay(delayTime)
            Log.d("ChatWebSocketManager", "正在尝试第 $reconnectAttempt 次重连...")
            connect()
        }
    }

    @Synchronized
    fun disconnect() {
        isManualClose = true
        reconnectJob?.cancel()
        webSocket?.close(1000, "App处于后台/退出登录")
        webSocket = null
        reconnectAttempt = 0
    }
    fun send(message: SendMessageRequest) {
        val text = Json.encodeToString(message)
        try {
            webSocket?.send(text)
        } catch (e: Exception) {
            Log.e("my-im", "发送消息失败", e)
        }

    }
}

class AppLifecycleObserver : DefaultLifecycleObserver {
    override fun onStart(owner: LifecycleOwner) {
        super.onStart(owner)
        Log.d("APP", "应用回到前台，尝试重连 WebSocket")
        ChatWebSocketManager.connect()
    }
    override fun onStop(owner: LifecycleOwner) {
        super.onStop(owner)
        Log.d("APP", "应用进入后台，主动切断 WebSocket 连接")
        ChatWebSocketManager.disconnect()
    }
}