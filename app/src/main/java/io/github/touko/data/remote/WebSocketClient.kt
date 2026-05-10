package io.github.touko.data.remote

import android.util.Log
import io.github.touko.data.model.request.SendMessageRequest
import io.github.touko.data.model.response.Message
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener

object ChatWebSocketManager {

    private val client = OkHttpClient()
    private const val BASE_WS_URL = "ws://192.168.31.224:8080/ws"
    private var webSocket: WebSocket? = null
    val messageFlow = MutableSharedFlow<Message>(
            extraBufferCapacity = 64
        )

    fun connect(userId: Int) {
        val request = Request.Builder()
            .url("${BASE_WS_URL}/chat?userId=$userId")
            .build()

        webSocket = client.newWebSocket(
            request,
            object : WebSocketListener() {

                override fun onOpen(
                    webSocket: WebSocket,
                    response: Response
                ) {
                    println("链接成功")
                }

                override fun onMessage(
                    webSocket: WebSocket,
                    text: String
                ) {
                    Log.d("im-server", text)
                    val message = Json.decodeFromString<Message>(text)
                    Log.d("im-server", "onMessage: $message")
                    CoroutineScope(Dispatchers.Main).launch {
                        messageFlow.emit(message)
                    }
                }

                override fun onFailure(
                    webSocket: WebSocket,
                    t: Throwable,
                    response: Response?
                ) {

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

    fun send(message: SendMessageRequest) {
        Log.d("my-im", "send: $message")
        val text =
            Json.encodeToString(message)
        webSocket?.send(text)
    }
}



