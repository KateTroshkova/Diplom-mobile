package nstu.avt716.etroshkova.diplom.presentation.main

import android.content.Intent
import io.reactivex.rxjava3.disposables.Disposable
import moxy.InjectViewState
import moxy.MvpPresenter
import nstu.avt716.etroshkova.diplom.R
import nstu.avt716.etroshkova.diplom.domain.interactor.ConnectionInteractor
import javax.inject.Inject

@InjectViewState
class MainPresenter @Inject constructor(
    private val connection: ConnectionInteractor
) : MvpPresenter<MainView>() {

    private var isPermissionsGranted = false
    private var disposables = mutableListOf<Disposable>()

    override fun destroyView(view: MainView?) {
        super.destroyView(view)
        disposables.forEach {
            it.dispose()
        }
    }

    fun handleUsbConnection() {
        viewState.requestPermissions()
    }

    fun notifyScreenProjectionGranted(resultCode: Int, data: Intent) {
        viewState.startService(resultCode, data)
        viewState.showError(R.string.record_start)
        disposables.add(
            connection
                .connect()
                .subscribe(
                    {},
                    { viewState.showError(R.string.error_connection) }
                )
        )
    }

    fun notifyScreenProjectionDenied() {
        viewState.showError(R.string.error_permission_denied)
    }

    fun disconnect() {
        viewState.stopService()
        disposables.add(
            connection
                .disconnect()
                .subscribe(
                    {},
                    { viewState.showError(R.string.error_connection) }
                )
        )
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