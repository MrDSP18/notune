package echo.music.iad1tya.ui.screens

import androidx.compose.runtime.Composable
import com.music.echo.notune.personalization.repository.TasteProfileRepository
import com.music.echo.notune.personalization.ui.OnboardingWizardScreen

@Composable
fun WelcomeDialog(
    tasteProfileRepository: TasteProfileRepository,
    onDismissRequest: () -> Unit
) {
    OnboardingWizardScreen(
        tasteProfileRepository = tasteProfileRepository,
        onComplete = onDismissRequest
    )
}
