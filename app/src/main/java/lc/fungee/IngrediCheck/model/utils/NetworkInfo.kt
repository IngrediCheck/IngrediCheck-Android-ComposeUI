package lc.fungee.IngrediCheck.model.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.telephony.TelephonyManager
import android.util.Log

object NetworkInfo {
    /**
     * Get network type: "wifi", "cellular", "other", or "none"
     */
    fun getNetworkType(context: Context): String {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        val network = connectivityManager.activeNetwork
        if (network == null) {
            Log.d("NetworkInfo", "No active network found, returning 'none'")
            return "none"
        }
        
        val capabilities = connectivityManager.getNetworkCapabilities(network)
        if (capabilities == null) {
            Log.d("NetworkInfo", "No network capabilities found, returning 'none'")
            return "none"
        }

        val networkType = when {
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> "wifi"
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> "cellular"
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> "other"
            else -> "none"
        }
        Log.d("NetworkInfo", "Detected network type: $networkType")
        return networkType
    }

    /**
     * Get cellular generation: "3g", "4g", "5g", "unknown", or "none"
     */
    fun getCellularGeneration(context: Context): String {
        val networkType = getNetworkType(context)
        if (networkType != "cellular") {
            Log.d("NetworkInfo", "Not on cellular network, returning 'none' for cellular generation")
            return "none"
        }

        val telephonyManager =
            context.getSystemService(Context.TELEPHONY_SERVICE) as? TelephonyManager
        if (telephonyManager == null) {
            Log.w("NetworkInfo", "TelephonyManager not available, returning 'unknown'")
            return "unknown"
        }

        val generation = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // Android 11+ (API 30+)
            val networkTypeValue = telephonyManager.dataNetworkType
            Log.d("NetworkInfo", "Android 11+, dataNetworkType: $networkTypeValue")
            when (networkTypeValue) {
                TelephonyManager.NETWORK_TYPE_GPRS,
                TelephonyManager.NETWORK_TYPE_EDGE,
                TelephonyManager.NETWORK_TYPE_CDMA,
                TelephonyManager.NETWORK_TYPE_1xRTT,
                TelephonyManager.NETWORK_TYPE_IDEN -> "3g"
                TelephonyManager.NETWORK_TYPE_UMTS,
                TelephonyManager.NETWORK_TYPE_EVDO_0,
                TelephonyManager.NETWORK_TYPE_EVDO_A,
                TelephonyManager.NETWORK_TYPE_HSDPA,
                TelephonyManager.NETWORK_TYPE_HSUPA,
                TelephonyManager.NETWORK_TYPE_HSPA,
                TelephonyManager.NETWORK_TYPE_EVDO_B,
                TelephonyManager.NETWORK_TYPE_EHRPD,
                TelephonyManager.NETWORK_TYPE_HSPAP -> "3g"
                TelephonyManager.NETWORK_TYPE_LTE -> "4g"
                TelephonyManager.NETWORK_TYPE_NR -> "5g"
                else -> "unknown"
            }
        } else {
            // Android 10 and below
            @Suppress("DEPRECATION")
            val networkTypeValue = telephonyManager.networkType
            Log.d("NetworkInfo", "Android 10-, networkType: $networkTypeValue")
            when (networkTypeValue) {
                TelephonyManager.NETWORK_TYPE_GPRS,
                TelephonyManager.NETWORK_TYPE_EDGE,
                TelephonyManager.NETWORK_TYPE_CDMA,
                TelephonyManager.NETWORK_TYPE_1xRTT,
                TelephonyManager.NETWORK_TYPE_IDEN -> "3g"
                TelephonyManager.NETWORK_TYPE_UMTS,
                TelephonyManager.NETWORK_TYPE_EVDO_0,
                TelephonyManager.NETWORK_TYPE_EVDO_A,
                TelephonyManager.NETWORK_TYPE_HSDPA,
                TelephonyManager.NETWORK_TYPE_HSUPA,
                TelephonyManager.NETWORK_TYPE_HSPA,
                TelephonyManager.NETWORK_TYPE_EVDO_B,
                TelephonyManager.NETWORK_TYPE_EHRPD,
                TelephonyManager.NETWORK_TYPE_HSPAP -> "3g"
                TelephonyManager.NETWORK_TYPE_LTE -> "4g"
                TelephonyManager.NETWORK_TYPE_NR -> "5g"
                else -> "unknown"
            }
        }
        Log.d("NetworkInfo", "Detected cellular generation: $generation")
        return generation
    }

    /**
     * Get carrier name or null if unavailable
     */
    fun getCarrier(context: Context): String? {
        val networkType = getNetworkType(context)
        if (networkType != "cellular") {
            Log.d("NetworkInfo", "Not on cellular network, returning null for carrier")
            return null
        }

        val telephonyManager =
            context.getSystemService(Context.TELEPHONY_SERVICE) as? TelephonyManager
        if (telephonyManager == null) {
            Log.w("NetworkInfo", "TelephonyManager not available, returning null for carrier")
            return null
        }

        return try {
            @Suppress("DEPRECATION")
            val carrierName = telephonyManager.networkOperatorName?.takeIf { it.isNotBlank() }
            Log.d("NetworkInfo", "Detected carrier: ${carrierName ?: "null/empty"}")
            carrierName
        } catch (e: SecurityException) {
            // READ_PHONE_STATE permission may not be granted
            Log.w("NetworkInfo", "SecurityException getting carrier (permission denied): ${e.message}")
            null
        }
    }
}

