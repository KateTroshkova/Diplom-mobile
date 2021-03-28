package nstu.avt716.etroshkova.diplom.presentation.main

import android.content.Intent
import moxy.MvpView
import moxy.viewstate.strategy.alias.OneExecution

interface MainView : MvpView {

    @OneExecution
    fun startService(resultCode: Int, data: Intent)

    @OneExecution
    fun stopService()

    @OneExecution
    fun requestPermissions()

    @OneExecution
    fun requestScreenProjection()

    @OneExecution
    fun showError(error: Int)
}