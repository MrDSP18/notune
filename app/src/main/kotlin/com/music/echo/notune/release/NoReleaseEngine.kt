package com.music.echo.notune.release

data class ReleaseInfo(
    val versionName: String,
    val versionCode: Int,
    val channel: String,
    val approved: Boolean,
    val downloadUrl: String,
    val sha256: String,
    val releaseNotes: String
)

data class UpdateCheckResult(
    val isUpdateAvailable: Boolean,
    val latestRelease: ReleaseInfo?,
    val isMandatory: Boolean = false,
    val message: String
)

object NoReleaseEngine {

    const val CURRENT_VERSION_CODE = 30100
    const val CURRENT_VERSION_NAME = "3.1.0"

    fun getReleaseCheckUrl(channel: String = "stable"): String {
        return com.music.echo.notune.config.PlatformConfig.latestReleaseEndpoint(channel = channel)
    }

    fun checkForUpdate(latestRelease: ReleaseInfo): UpdateCheckResult {
        if (!latestRelease.approved) {
            return UpdateCheckResult(
                isUpdateAvailable = false,
                latestRelease = null,
                message = "Latest build is not yet approved for stable release channel."
            )
        }

        val isNewer = latestRelease.versionCode > CURRENT_VERSION_CODE
        return if (isNewer) {
            UpdateCheckResult(
                isUpdateAvailable = true,
                latestRelease = latestRelease,
                message = "NØTUNE ${latestRelease.versionName} is available! ${latestRelease.releaseNotes}"
            )
        } else {
            UpdateCheckResult(
                isUpdateAvailable = false,
                latestRelease = latestRelease,
                message = "NØTUNE $CURRENT_VERSION_NAME is up to date."
            )
        }
    }

    fun verifySha256Checksum(computedHash: String, expectedHash: String): Boolean {
        if (expectedHash.isBlank() || computedHash.isBlank()) return false
        return computedHash.equals(expectedHash, ignoreCase = true)
    }
}
