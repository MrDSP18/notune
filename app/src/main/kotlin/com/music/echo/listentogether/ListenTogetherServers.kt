package echo.music.iad1tya.listentogether

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

@Serializable
data class ListenTogetherServer(
    val name: String,
    val url: String,
    val location: String,
    val operator: String
)

@OptIn(DelicateCoroutinesApi::class)
object ListenTogetherServers {
    private const val SERVER_JSON_URL = "https://raw.githubusercontent.com/MrDSP18/notune/refs/heads/main/app/server.json"

    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    private val DEFAULT_SERVERS = listOf(
        ListenTogetherServer(
            name = "NØTUNE Cloudflare Edge",
            url = "wss://notune.dharansundarapandiyan24.workers.dev/ws",
            location = "Global Edge (Cloudflare)",
            operator = "NØTUNE Ecosystem Primary"
        ),
        ListenTogetherServer(
            name = "NØTUNE Cloud Server",
            url = "wss://notune-backend-service.onrender.com/ws",
            location = "Global Cloud (Render/OCI)",
            operator = "NØTUNE Ecosystem API"
        ),
        ListenTogetherServer(
            name = "Metrolist Server",
            url = "wss://metroserverx.meowery.eu/ws",
            location = "Global Sync (Fallback)",
            operator = "Third-Party Fallback"
        )
    )

    private val _servers = MutableStateFlow(DEFAULT_SERVERS)
    
    val serversFlow: StateFlow<List<ListenTogetherServer>> = _servers

    val servers: List<ListenTogetherServer>
        get() = _servers.value

    init {
        scope.launch {
            try {
                val client = okhttp3.OkHttpClient()
                val request = okhttp3.Request.Builder().url(SERVER_JSON_URL).build()
                val response = client.newCall(request).execute()
                response.body.string().let { jsonString ->
                    val jsonObject = Json.parseToJsonElement(jsonString).jsonObject
                    val name = jsonObject["name"]?.jsonPrimitive?.content
                    val url = jsonObject["serverUrl"]?.jsonPrimitive?.content
                    val region = jsonObject["region"]?.jsonPrimitive?.content ?: "Global"
                    
                    if (!url.isNullOrBlank() && url != "wss://devilmi-vivi-music-listen-together.hf.space") {
                        val fetchedServer = ListenTogetherServer(
                            name = name ?: "Custom Server",
                            url = url,
                            location = region,
                            operator = "Remote Config"
                        )
                        val combined = (listOf(fetchedServer) + DEFAULT_SERVERS).distinctBy { it.url }
                        _servers.value = combined
                    }
                }
            } catch (e: Exception) {
                // Keep default servers intact on error
            }
        }
    }


    val defaultServerUrl: String
        get() = servers.first().url

    fun findByUrl(url: String): ListenTogetherServer? = servers.firstOrNull { it.url == url }
}
