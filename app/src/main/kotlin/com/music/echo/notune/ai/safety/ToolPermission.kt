package echo.music.iad1tya.notune.ai.safety

enum class ToolPermission {
    READ,
    LOW_RISK_WRITE,
    MEDIUM_RISK,
    HIGH_RISK
}

data class ActionPolicy(
    val requiresConfirmation: Boolean = false,
    val isReadOnly: Boolean = false,
    val isDestructive: Boolean = false,
    val allowedInRoom: Boolean = true
)

class ToolGuard {
    fun validateAction(
        permission: ToolPermission,
        policy: ActionPolicy,
        userConfirmed: Boolean = false
    ): Boolean {
        if (policy.isDestructive || permission == ToolPermission.HIGH_RISK) {
            return userConfirmed
        }
        return true
    }
}
