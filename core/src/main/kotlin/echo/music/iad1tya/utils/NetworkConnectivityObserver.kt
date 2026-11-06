
package echo.music.iad1tya.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow

enum class NetworkQuality {
    OFFLINE,
    SLOW_2G_3G,
    METERED,
    STABLE_4G,
    EXCELLENT_WIFI,
}

class NetworkConnectivityObserver(context: Context) {

    private val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    private val _networkStatus = Channel<Boolean>(Channel.CONFLATED)
    val networkStatus: Flow<Boolean> = _networkStatus.receiveAsFlow()

    private val _networkQuality = Channel<NetworkQuality>(Channel.CONFLATED)
    val networkQuality: Flow<NetworkQuality> = _networkQuality.receiveAsFlow()

    private val networkCallback = object : ConnectivityManager.NetworkCallback() {

        override fun onAvailable(network: Network) {
            updateNetworkState()
        }

        override fun onLost(network: Network) {
            updateNetworkState()
        }

        override fun onCapabilitiesChanged(
            network: Network,
            networkCapabilities: NetworkCapabilities
        ) {
            updateNetworkState()
        }
    }

    init {
        val request = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .addCapability(NetworkCapabilities.NET_CAPABILITY_NOT_RESTRICTED)
            .build()

        try {
            connectivityManager.registerNetworkCallback(request, networkCallback)
        } catch (e: Exception) {
            updateNetworkState()
        }

        updateNetworkState()
    }

    private fun updateNetworkState() {
        val connected = isCurrentlyConnected()

        _networkStatus.trySend(connected)

        _networkQuality.trySend(
            if (connected) {
                getNetworkQuality()
            } else {
                NetworkQuality.OFFLINE
            }
        )
    }

    fun unregister() {
        try {
            connectivityManager.unregisterNetworkCallback(networkCallback)
        } catch (e: Exception) {
            // Callback may already be unregistered.
        }
    }

    fun isCurrentlyConnected(): Boolean {
        return try {
            val activeNetwork = connectivityManager.activeNetwork
            val networkCapabilities =
                connectivityManager.getNetworkCapabilities(activeNetwork)

            val hasInternet =
                networkCapabilities?.hasCapability(
                    NetworkCapabilities.NET_CAPABILITY_INTERNET
                ) == true

            val isValidated =
                networkCapabilities?.hasCapability(
                    NetworkCapabilities.NET_CAPABILITY_VALIDATED
                ) == true

            hasInternet && isValidated
        } catch (e: Exception) {
            false
        }
    }

    fun isMetered(): Boolean {
        return try {
            val activeNetwork = connectivityManager.activeNetwork
            val networkCapabilities =
                connectivityManager.getNetworkCapabilities(activeNetwork)

            networkCapabilities?.hasCapability(
                NetworkCapabilities.NET_CAPABILITY_NOT_METERED
            ) == false
        } catch (e: Exception) {
            false
        }
    }

    fun getNetworkQuality(): NetworkQuality {
        if (!isCurrentlyConnected()) {
            return NetworkQuality.OFFLINE
        }

        return try {
            val activeNetwork =
                connectivityManager.activeNetwork
                    ?: return NetworkQuality.OFFLINE

            val caps =
                connectivityManager.getNetworkCapabilities(activeNetwork)
                    ?: return NetworkQuality.OFFLINE

            when {
                caps.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                    caps.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> {

                    val linkKbps = caps.linkDownstreamBandwidthKbps

                    if (linkKbps in 1..2000) {
                        NetworkQuality.SLOW_2G_3G
                    } else {
                        NetworkQuality.EXCELLENT_WIFI
                    }
                }

                caps.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> {

                    val linkKbps = caps.linkDownstreamBandwidthKbps

                    when {
                        linkKbps in 1..1500 ->
                            NetworkQuality.SLOW_2G_3G

                        !caps.hasCapability(
                            NetworkCapabilities.NET_CAPABILITY_NOT_METERED
                        ) ->
                            NetworkQuality.METERED

                        else ->
                            NetworkQuality.STABLE_4G
                    }
                }

                else -> NetworkQuality.STABLE_4G
            }
        } catch (e: Exception) {
            NetworkQuality.STABLE_4G
        }
    }
}
