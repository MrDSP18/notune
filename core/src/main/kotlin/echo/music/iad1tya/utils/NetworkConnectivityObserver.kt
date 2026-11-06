

package echo.music.iad1tya.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow


class NetworkConnectivityObserver(context: Context) {
    private val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    private val _networkStatus = Channel<Boolean>(Channel.CONFLATED)
    val networkStatus = _networkStatus.receiveAsFlow()

    private val networkCallback = object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
            _networkStatus.trySend(isCurrentlyConnected())
        }

        override fun onLost(network: Network) {
            // Another validated network may still be active (for example Wi-Fi
            // after mobile data disconnects), so recompute instead of forcing offline.
            _networkStatus.trySend(isCurrentlyConnected())
        }

        override fun onCapabilitiesChanged(network: Network, networkCapabilities: NetworkCapabilities) {
            _networkStatus.trySend(isCurrentlyConnected())
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
            _networkStatus.trySend(isCurrentlyConnected())
        }
        
        
        val isInitiallyConnected = isCurrentlyConnected()
        _networkStatus.trySend(isInitiallyConnected)
    }

    fun unregister() {
        connectivityManager.unregisterNetworkCallback(networkCallback)
    }
    
    
    fun isCurrentlyConnected(): Boolean {
        return try {
            val activeNetwork = connectivityManager.activeNetwork
            val networkCapabilities = connectivityManager.getNetworkCapabilities(activeNetwork)
            
            
            val hasInternet = networkCapabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true
            
            
            val isValidated =
                networkCapabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED) == true

            hasInternet && isValidated
        } catch (e: Exception) {
            false
        }
    }
}
