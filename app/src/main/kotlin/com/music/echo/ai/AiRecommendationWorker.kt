package echo.music.iad1tya.ai

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.hilt.work.HiltWorker
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import timber.log.Timber

@HiltWorker
class AiRecommendationWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val aiRecommendationHelper: AiRecommendationHelper
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            aiRecommendationHelper.generateRecommendations(applicationContext)
            Result.success()
        } catch (e: Exception) {
            Timber.e(e, "AiRecommendationWorker: recommendation generation failed (attempt ${runAttemptCount + 1})")
            // Retry up to 3 times for transient failures; give up after that to avoid battery drain.
            if (runAttemptCount < 3) Result.retry() else Result.failure()
        }
    }
}
