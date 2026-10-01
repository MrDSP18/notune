package echo.music.iad1tya.notune.ai.engine

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import echo.music.iad1tya.utils.dataStore
import echo.music.iad1tya.utils.get
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton
import dagger.hilt.android.qualifiers.ApplicationContext

private val AiMemoryFactsKey = stringPreferencesKey("notune_ai_memory_facts")

@Singleton
class NoMindMemory @Inject constructor(
    @ApplicationContext private val context: Context
) {
    val memoryFacts: Flow<List<String>> = context.dataStore.data.map { prefs ->
        val raw = prefs[AiMemoryFactsKey] ?: ""
        if (raw.isBlank()) emptyList()
        else raw.split("|||")
    }

    suspend fun rememberFact(fact: String) {
        context.dataStore.edit { prefs ->
            val current = prefs[AiMemoryFactsKey] ?: ""
            val list = if (current.isBlank()) mutableListOf() else current.split("|||").toMutableList()
            if (!list.contains(fact)) {
                list.add(fact)
                prefs[AiMemoryFactsKey] = list.takeLast(50).joinToString("|||")
            }
        }
    }

    suspend fun forgetFact(fact: String) {
        context.dataStore.edit { prefs ->
            val current = prefs[AiMemoryFactsKey] ?: ""
            if (current.isNotBlank()) {
                val list = current.split("|||").filter { it != fact }
                prefs[AiMemoryFactsKey] = list.joinToString("|||")
            }
        }
    }

    suspend fun clearMemory() {
        context.dataStore.edit { prefs ->
            prefs.remove(AiMemoryFactsKey)
        }
    }
}
