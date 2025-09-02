package lc.fungee.IngrediCheck.data


import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore

// Global extension property for DataStore
val Context.dataStore by preferencesDataStore("preferences_store")
