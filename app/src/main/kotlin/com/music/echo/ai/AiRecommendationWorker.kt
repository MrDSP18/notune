package echo.music.iad1tya.ai

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters

import androidx.hilt.work.HiltWorker
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

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
            e.printStackTrace()
            Result.retry()
        }
    }
}
