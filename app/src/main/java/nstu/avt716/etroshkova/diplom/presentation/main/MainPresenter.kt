package nstu.avt716.etroshkova.diplom.presentation.main

import android.content.Intent
import moxy.InjectViewState
import moxy.MvpPresenter
import nstu.avt716.etroshkova.diplom.R
import javax.inject.Inject

@InjectViewState
class MainPresenter @Inject constructor() : MvpPresenter<MainView>() {

    private var isPermissionsGranted = false

    fun handleUsbConnection() {
        viewState.requestPermissions()
    }

    fun notifyScreenProjectionGranted(resultCode: Int, data: Intent) {
        viewState.startService(resultCode, data)
        viewState.showError(R.string.record_start)
    }

    fun notifyScreenProjectionDenied() {
        viewState.showError(R.string.error_permission_denied)
    }

    fun disconnect() {
        viewState.stopService()
    }

    fun notifyPermissionsGranted() {
        isPermissionsGranted = true
        viewState.requestScreenProjection()

    }

    fun notifyPermissionDenied() {
        isPermissionsGranted = false
        viewState.showError(R.string.error_permission_denied)
    }
}