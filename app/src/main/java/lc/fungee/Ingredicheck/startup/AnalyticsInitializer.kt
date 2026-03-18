package lc.fungee.Ingredicheck.startup

import android.content.Context
import androidx.startup.Initializer
import lc.fungee.Ingredicheck.store.AnalyticsService

class AnalyticsInitializer : Initializer<Unit> {
    override fun create(context: Context) {
        AnalyticsService.configure(context)
    }

    override fun dependencies(): List<Class<out Initializer<*>>> = emptyList()
}
