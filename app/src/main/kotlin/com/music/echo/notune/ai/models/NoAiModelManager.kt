package com.music.echo.notune.ai.models

import com.music.echo.notune.ai.core.NoAiModel
import com.music.echo.notune.ai.core.NoAiModelRegistry
import java.security.MessageDigest

enum class ModelDownloadStatus {
    NOT_INSTALLED,
    DOWNLOADING,
    VERIFYING,
    INSTALLED,
    READY,
    FAILED
}

data class ModelState(
    val model: NoAiModel,
    val status: ModelDownloadStatus,
    val downloadProgressPercent: Int = 0,
    val errorMessage: String? = null
)

object NoAiModelManager {

    private val modelStates = mutableMapOf<String, ModelState>()

    init {
        NoAiModelRegistry.Models.forEach { model ->
            modelStates[model.id] = ModelState(
                model = model,
                status = if (model.downloadSizeMb == 0) ModelDownloadStatus.READY else ModelDownloadStatus.NOT_INSTALLED
            )
        }
    }

    fun getModelState(modelId: String): ModelState? = modelStates[modelId]

    fun getAllModelStates(): List<ModelState> = modelStates.values.toList()

    fun verifyChecksum(modelData: ByteArray, expectedSha256: String): Boolean {
        if (expectedSha256.isBlank()) return true
        val digest = MessageDigest.getInstance("SHA-256")
        val hashBytes = digest.digest(modelData)
        val computedHash = hashBytes.joinToString("") { "%02x".format(it) }
        return computedHash.equals(expectedSha256, ignoreCase = true)
    }

    fun markModelReady(modelId: String) {
        val current = modelStates[modelId] ?: return
        modelStates[modelId] = current.copy(status = ModelDownloadStatus.READY, downloadProgressPercent = 100)
    }
}
