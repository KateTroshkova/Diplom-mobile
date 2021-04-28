package nstu.avt716.etroshkova.diplom.presentation.main

import android.content.Intent
import moxy.MvpView
import moxy.viewstate.strategy.alias.AddToEndSingle
import moxy.viewstate.strategy.alias.OneExecution

interface MainView : MvpView {

    @OneExecution
    fun startService(resultCode: Int, data: Intent)

    @AddToEndSingle
    fun applyAudioSettings(isAudioRecordAllowed: Boolean)

    @AddToEndSingle
    fun applyVideoSettings(isSaveVideoAllowed: Boolean)

    @OneExecution
    fun stopService()

    @OneExecution
    fun requestUSBPermissions()

    @OneExecution
    fun requestWifiPermissions()

    @OneExecution
    fun requestScreenProjection()

    @OneExecution
    fun showError(error: Int)
}