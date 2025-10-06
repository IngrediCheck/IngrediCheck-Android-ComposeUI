package lc.fungee.IngrediCheck.viewmodel

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import lc.fungee.IngrediCheck.model.utils.isInternetAvailable

class NetworkViewmodel : ViewModel() {
    var isOnline by mutableStateOf(true)
        private set

    private var connectivityManager: ConnectivityManager? = null
    private var registered = false
    private var fallbackPolling = false

    private val callback = object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
            isOnline = true
        }

        override fun onLost(network: Network) {
            // Check remaining active network capabilities to decide
            val cm = connectivityManager
            val active = cm?.activeNetwork
            val caps = active?.let { cm.getNetworkCapabilities(it) }
            isOnline = caps?.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED) == true
        }

        override fun onCapabilitiesChanged(network: Network, networkCapabilities: NetworkCapabilities) {
            // Consider validated network as online
            val online = networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
            if (isOnline != online) isOnline = online
        }
    }

    fun startMonitoring(context: Context) {
        if (connectivityManager != null) return
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        connectivityManager = cm

        // Set initial state instantly
        isOnline = isInternetAvailable(context)

        // Try to register callback for instant updates
        runCatching {
            val request = NetworkRequest.Builder()
                .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                .build()
            cm.registerNetworkCallback(request, callback)
            registered = true
        }.onFailure {
            registered = false
        }

        // Fallback polling if callback registration failed
        if (!registered && !fallbackPolling) {
            fallbackPolling = true
            viewModelScope.launch {
                while (fallbackPolling) {
                    val available = isInternetAvailable(context)
                    if (isOnline != available) isOnline = available
                    delay(2000)
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        fallbackPolling = false
        runCatching { connectivityManager?.unregisterNetworkCallback(callback) }
        connectivityManager = null
    }
}