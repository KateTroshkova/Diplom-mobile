package nstu.avt716.etroshkova.diplom.presentation

import android.app.Application
import moxy.MvpFacade
import nstu.avt716.etroshkova.diplom.presentation.di.Injector
import toothpick.Toothpick

class CustomApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        val appScope = Toothpick.openScope("App")
        appScope.installModules(Injector())
        Toothpick.inject(this, appScope)
        MvpFacade.init()
    }
}