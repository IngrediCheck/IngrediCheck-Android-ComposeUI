package lc.fungee.IngrediCheck.viewmodel

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import lc.fungee.IngrediCheck.model.utils.isInternetAvailable

class NetworkViewmodel  : ViewModel() {
        var isOnline by mutableStateOf(true)
            private set

        fun startMonitoring(context: Context) {
            viewModelScope.launch {
                while (true) {
                    val available = isInternetAvailable(context)
                    if (isOnline != available) isOnline = available
                    delay(2000)
                }
            }
        }
    }