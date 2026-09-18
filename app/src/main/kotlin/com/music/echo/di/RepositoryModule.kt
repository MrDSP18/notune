package echo.music.iad1tya.di

import com.music.echo.repository.EventRepositoryImpl
import com.music.echo.repository.PlaybackRepositoryImpl
import echo.music.iad1tya.repository.EventRepository
import echo.music.iad1tya.repository.PlaybackRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindPlaybackRepository(
        playbackRepositoryImpl: PlaybackRepositoryImpl
    ): PlaybackRepository

    @Binds
    @Singleton
    abstract fun bindEventRepository(
        eventRepositoryImpl: EventRepositoryImpl
    ): EventRepository

    @Binds
    @Singleton
    abstract fun bindLocalMediaRepository(
        localMediaRepositoryImpl: com.music.echo.repository.LocalMediaRepositoryImpl
    ): echo.music.iad1tya.repository.LocalMediaRepository

    @Binds
    @Singleton
    abstract fun bindSocialRepository(
        socialRepositoryImpl: com.music.echo.repository.SocialRepositoryImpl
    ): echo.music.iad1tya.repository.SocialRepository

    @Binds
    @Singleton
    abstract fun bindMessagingRepository(
        messagingRepositoryImpl: com.music.echo.repository.MessagingRepositoryImpl
    ): echo.music.iad1tya.repository.MessagingRepository

    @Binds
    @Singleton
    abstract fun bindProfileRepository(
        profileRepositoryImpl: com.music.echo.repository.ProfileRepositoryImpl
    ): echo.music.iad1tya.repository.ProfileRepository
}
