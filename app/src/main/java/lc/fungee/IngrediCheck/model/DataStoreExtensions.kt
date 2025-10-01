package lc.fungee.IngrediCheck.model


import android.content.Context
import androidx.datastore.preferences.preferencesDataStore

// Global extension property for DataStore
val Context.dataStore by preferencesDataStore("preferences_store")
