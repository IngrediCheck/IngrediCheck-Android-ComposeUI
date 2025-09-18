package lc.fungee.IngrediCheck

import android.app.Application
import lc.fungee.IngrediCheck.di.AppContainer

class IngrediCheckApp : Application() {
    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
        appInstance = this
    }

    companion object {
        lateinit var appInstance: IngrediCheckApp
            private set
    }
}
