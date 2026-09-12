package com.music.echo.notune.privacy.models

enum class DataClassificationLevel {
    PUBLIC,
    NORMAL,
    PERSONAL,
    PRIVATE,
    HIGHLY_PRIVATE,
    SECRET,
    CRYPTOGRAPHIC_SECRET
}

data class DataCategoryInfo(
    val categoryName: String,
    val level: DataClassificationLevel,
    val description: String,
    val isStoredLocally: Boolean,
    val isCloudSynced: Boolean,
    val canBeExported: Boolean,
    val canBeDeleted: Boolean
)

data class DataInventorySummary(
    val listeningHistoryCount: Int = 0,
    val favoritesCount: Int = 0,
    val playlistsCount: Int = 0,
    val aiConversationsCount: Int = 0,
    val musicDnaEnabled: Boolean = true,
    val flowEnabled: Boolean = true,
    val cloudSyncEnabled: Boolean = false,
    val analyticsEnabled: Boolean = false
)

data class AITransparencyInfo(
    val sharedFields: List<String>,
    val excludedFields: List<String>,
    val processingMode: String
)
