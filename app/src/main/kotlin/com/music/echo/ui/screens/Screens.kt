

package echo.music.iad1tya.ui.screens

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.runtime.Immutable
import echo.music.iad1tya.R

@Immutable
sealed class Screens(
    @StringRes val titleId: Int,
    @DrawableRes val iconIdInactive: Int,
    @DrawableRes val iconIdActive: Int,
    val route: String,
) {
    object Home : Screens(
        titleId = R.string.home,
        iconIdInactive = R.drawable.home_outlined,
        iconIdActive = R.drawable.home_filled,
        route = "home"
    )

    object Search : Screens(
        titleId = R.string.search,
        iconIdInactive = R.drawable.search,
        iconIdActive = R.drawable.search,
        route = "search_input"
    )

    object ListenTogether : Screens(
        titleId = R.string.together,
        iconIdInactive = R.drawable.group_outlined,
        iconIdActive = R.drawable.group_filled,
        route = "listen_together"
    )

    object Library : Screens(
        titleId = R.string.filter_library,
        iconIdInactive = R.drawable.library_music_outlined,
        iconIdActive = R.drawable.library_music_filled,
        route = "library"
    )

    object CoupleMode : Screens(
        titleId = R.string.together,
        iconIdInactive = R.drawable.group_outlined,
        iconIdActive = R.drawable.group_filled,
        route = "couple_mode"
    )

    object RoomsDiscovery : Screens(
        titleId = R.string.together,
        iconIdInactive = R.drawable.group_outlined,
        iconIdActive = R.drawable.group_filled,
        route = "rooms_discovery"
    )

    object CoupleHome : Screens(
        titleId = R.string.together,
        iconIdInactive = R.drawable.group_outlined,
        iconIdActive = R.drawable.group_filled,
        route = "couple_home"
    )

    object PrivacyCenter : Screens(
        titleId = R.string.privacy,
        iconIdInactive = R.drawable.security,
        iconIdActive = R.drawable.security,
        route = "privacy_center"
    )

    object Personalization : Screens(
        titleId = R.string.settings,
        iconIdInactive = R.drawable.tune,
        iconIdActive = R.drawable.tune,
        route = "settings/personalization"
    )

    object TasteProfile : Screens(
        titleId = R.string.settings,
        iconIdInactive = R.drawable.sparks,
        iconIdActive = R.drawable.sparks,
        route = "taste_profile"
    )

    object SocialHub : Screens(
        titleId = R.string.together,
        iconIdInactive = R.drawable.group_outlined,
        iconIdActive = R.drawable.group_filled,
        route = "social_hub"
    )

    object CollaborativePlaylist : Screens(
        titleId = R.string.filter_library,
        iconIdInactive = R.drawable.library_music_outlined,
        iconIdActive = R.drawable.library_music_filled,
        route = "social/collaborative_playlist"
    )

    object MusicCompatibility : Screens(
        titleId = R.string.together,
        iconIdInactive = R.drawable.sparks,
        iconIdActive = R.drawable.sparks,
        route = "social/music_compatibility"
    )

    object FriendCircles : Screens(
        titleId = R.string.together,
        iconIdInactive = R.drawable.group_outlined,
        iconIdActive = R.drawable.group_filled,
        route = "social/friend_circles"
    )

    object MusicStories : Screens(
        titleId = R.string.together,
        iconIdInactive = R.drawable.sparks,
        iconIdActive = R.drawable.sparks,
        route = "social/music_stories"
    )

    companion object {
        val MainScreens = listOf(Home, Search, ListenTogether, Library)
    }
}
