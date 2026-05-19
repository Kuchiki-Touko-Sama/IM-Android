package io.github.touko.data.remote

import android.util.Log
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import io.github.touko.data.local.LocalUserManager
import io.github.touko.data.model.request.SendMessageRequest
import io.github.touko.data.model.response.Message
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
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
    private val client = OkHttpClient()
    private const val DOMAIN = "784514.xyz"
    private const val BASE_WS_URL = "wss://$DOMAIN/ws"
    private var webSocket: WebSocket? = null
    private var reconnectAttempt = 0
    private var isManualClose = false
    val messageFlow = MutableSharedFlow<Message>(extraBufferCapacity = 64)

    fun connect() {
        isManualClose = false
        webSocket?.close(1000, "切换当前用户")
        val request = Request.Builder()
            .url("${BASE_WS_URL}/chat?userId=${LocalUserManager.getUid()}")
            .build()

        webSocket = client.newWebSocket(
            request,
            object : WebSocketListener() {

                override fun onOpen(
                    webSocket: WebSocket,
                    response: Response
                ) {
                    reconnectAttempt = 0
                }

                override fun onMessage(
                    webSocket: WebSocket,
                    text: String
                ) {
                    val message = Json.decodeFromString<Message>(text)
                    CoroutineScope(Dispatchers.Main).launch {
                        messageFlow.emit(message)
                    }
                }

                override fun onFailure(
                    webSocket: WebSocket,
                    t: Throwable,
                    response: Response?
                ) {
                    reconnect()
                }

                override fun onClosed(
                    webSocket: WebSocket,
                    code: Int,
                    reason: String
                ) {
                }
            }
        )
    }

    private fun reconnect() {
        if (isManualClose)
            return
        // 指数退避
        val delayTime = (2.0.pow(reconnectAttempt).toLong() * 1000).coerceAtMost(30000)
        reconnectAttempt++

        CoroutineScope(Dispatchers.IO).launch {
            delay(delayTime)
            Log.d("my-im", "正在尝试第 $reconnectAttempt 次重连...")
            connect()
        }
    }

    fun disconnect() {
        isManualClose = true
        webSocket?.close(1000, "Logout")
        webSocket = null
    }

    fun send(message: SendMessageRequest) {
        Log.d("my-im", "send: $message")
        val text =
            Json.encodeToString(message)
        webSocket?.send(text)
    }
}

class AppLifecycleObserver : DefaultLifecycleObserver {
    override fun onStart(owner: LifecycleOwner) {
        super.onStart(owner)
        Log.d("APP", "应用回到前台")
        ChatWebSocketManager.connect()
    }

    override fun onStop(owner: LifecycleOwner) {
        super.onStop(owner)
        Log.d("APP", "应用进入后台")
    }
}

