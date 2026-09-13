package com.music.echo.notune.privacy.security

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import timber.log.Timber
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Local Room Database Security & Governance Manager for NØTUNE.
 * Manages database integrity verification, sensitive data classification,
 * corruption detection, and crash-safe database recovery protocols.
 */
@Singleton
class RoomSecurityManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val secureStorageManager: SecureStorageManager
) {
    enum class SensitiveEntityCategory {
        LISTENING_HISTORY,
        MUSIC_DNA,
        AI_HISTORY,
        ROOM_DATA,
        COUPLE_DATA,
        USER_PROFILE,
        NON_SENSITIVE_CATALOG
    }

    /**
     * Maps database entities to privacy sensitivity levels.
     */
    fun getEntitySensitivity(entityClassName: String): SensitiveEntityCategory {
        return when {
            entityClassName.contains("History", ignoreCase = true) -> SensitiveEntityCategory.LISTENING_HISTORY
            entityClassName.contains("DNA", ignoreCase = true) -> SensitiveEntityCategory.MUSIC_DNA
            entityClassName.contains("Ai", ignoreCase = true) -> SensitiveEntityCategory.AI_HISTORY
            entityClassName.contains("Room", ignoreCase = true) -> SensitiveEntityCategory.ROOM_DATA
            entityClassName.contains("Couple", ignoreCase = true) -> SensitiveEntityCategory.COUPLE_DATA
            entityClassName.contains("User", ignoreCase = true) || entityClassName.contains("Profile", ignoreCase = true) -> SensitiveEntityCategory.USER_PROFILE
            else -> SensitiveEntityCategory.NON_SENSITIVE_CATALOG
        }
    }

    /**
     * Verifies SQLite database file integrity on disk before Room initializes.
     * Returns true if database file is valid and readable, or false if corrupted.
     */
    fun verifyDatabaseIntegrity(dbName: String = "song.db"): Boolean {
        val dbFile = context.getDatabasePath(dbName)
        if (!dbFile.exists()) {
            return true // Fresh installation, no corrupt file
        }

        return try {
            val bytes = dbFile.readBytes()
            if (bytes.size < 100) return false

            // SQLite header check: First 16 bytes must equal "SQLite format 3\0"
            val header = String(bytes.sliceArray(0..15), Charsets.UTF_8)
            header.startsWith("SQLite format 3")
        } catch (e: Exception) {
            Timber.e(e, "Database integrity check failed for %s", dbName)
            false
        }
    }

    /**
     * Safe database recovery protocol for corrupted databases.
     * Backs up damaged file for debugging and resets to clean state to prevent app crash loops.
     */
    fun performSafeDatabaseRecovery(dbName: String = "song.db"): Boolean {
        val dbFile = context.getDatabasePath(dbName)
        if (!dbFile.exists()) return true

        return try {
            val backupFile = File(context.filesDir, "corrupted_${dbName}_${System.currentTimeMillis()}.bak")
            dbFile.copyTo(backupFile, overwrite = true)
            dbFile.delete()
            Timber.w("Database %s was corrupted. Safe recovery backup created at %s", dbName, backupFile.absolutePath)
            true
        } catch (e: Exception) {
            Timber.e(e, "Failed database recovery for %s", dbName)
            false
        }
    }
}
