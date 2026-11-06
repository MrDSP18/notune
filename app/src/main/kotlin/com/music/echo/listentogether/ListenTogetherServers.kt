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
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject

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
    private var lastFetchTimestamp: Long = 0L
    private const val MIN_FETCH_INTERVAL_MS = 10 * 60 * 1000L // 10 minutes cache window

    val serversFlow: StateFlow<List<ListenTogetherServer>> = _servers

    val servers: List<ListenTogetherServer>
        get() = _servers.value

    init {
        refreshManifest()
    }

    fun refreshManifest(force: Boolean = false) {
        val now = System.currentTimeMillis()
        if (!force && (now - lastFetchTimestamp) < MIN_FETCH_INTERVAL_MS) {
            return
        }
        
        scope.launch {
            try {
                lastFetchTimestamp = System.currentTimeMillis()
                val client = okhttp3.OkHttpClient.Builder()
                    .connectTimeout(5, java.util.concurrent.TimeUnit.SECONDS)
                    .readTimeout(5, java.util.concurrent.TimeUnit.SECONDS)
                    .build()
                val request = okhttp3.Request.Builder().url(SERVER_JSON_URL).build()
                val response = client.newCall(request).execute()
                val jsonString = response.body.string()
                val parsedServers = parseManifest(jsonString)
                if (parsedServers.isNotEmpty()) {
                    val combined = (parsedServers + DEFAULT_SERVERS).distinctBy { it.url }
                    _servers.value = combined
                }
            } catch (e: Exception) {
                // Keep existing servers intact on network error or offline mode
            }
        }
    }

    private fun parseManifest(jsonString: String): List<ListenTogetherServer> {
        val result = mutableListOf<ListenTogetherServer>()
        try {
            val jsonElement = Json.parseToJsonElement(jsonString)
            val jsonObject = jsonElement.jsonObject

            // 1. Structured Endpoints Array (Version 1+)
            val endpointsElement = jsonObject["endpoints"]
            if (endpointsElement is JsonArray) {
                endpointsElement.forEach { element ->
                    if (element is JsonObject) {
                        val enabled = element["enabled"]?.jsonPrimitive?.content?.toBooleanStrictOrNull() ?: true
                        val url = element["url"]?.jsonPrimitive?.content
                        val name = element["name"]?.jsonPrimitive?.content ?: "Remote Server"
                        val region = element["region"]?.jsonPrimitive?.content ?: "Global Edge"
                        val type = element["type"]?.jsonPrimitive?.content ?: "PRIMARY"

                        if (enabled && isValidServerUrl(url)) {
                            result.add(
                                ListenTogetherServer(
                                    name = name,
                                    url = url!!,
                                    location = region,
                                    operator = if (type.contains("EMERGENCY")) "Third-Party Fallback" else "NØTUNE Ecosystem"
                                )
                            )
                        }
                    }
                }
            } else {
                // 2. Legacy Simple Manifest Fallback
                val url = jsonObject["serverUrl"]?.jsonPrimitive?.content
                val name = jsonObject["name"]?.jsonPrimitive?.content ?: "Custom Server"
                val region = jsonObject["region"]?.jsonPrimitive?.content ?: "Global"

                if (isValidServerUrl(url)) {
                    result.add(
                        ListenTogetherServer(
                            name = name,
                            url = url!!,
                            location = region,
                            operator = "Remote Config"
                        )
                    )
                }
            }
        } catch (e: Exception) {
            // Ignore parse errors, return empty list to trigger fallback
        }
        return result
    }

    private fun isValidServerUrl(url: String?): Boolean {
        if (url.isNullOrBlank()) return false
        if (!url.startsWith("ws://") && !url.startsWith("wss://")) return false
        if (url.contains("hf.space")) return false // Blacklist deprecated legacy test servers
        return true
    }

    val defaultServerUrl: String
        get() = servers.first().url

    fun findByUrl(url: String): ListenTogetherServer? = servers.firstOrNull { it.url == url }
}
