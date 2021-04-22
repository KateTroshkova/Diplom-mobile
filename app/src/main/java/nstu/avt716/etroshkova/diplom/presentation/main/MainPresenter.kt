package nstu.avt716.etroshkova.diplom.presentation.main

import android.content.Intent
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
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
    private val wifiDataLD: MutableLiveData<String> by lazy { MutableLiveData<String>() }
    private val loadingStateLD: MutableLiveData<Boolean> by lazy { MutableLiveData<Boolean>() }
    val wifiData: LiveData<String> by lazy { wifiDataLD }
    val loadingState: LiveData<Boolean> by lazy { loadingStateLD }

    override fun destroyView(view: MainView?) {
        super.destroyView(view)
        disposables.forEach {
            it.dispose()
        }
    }

    fun handleUsbConnection() {
        viewState.requestUSBPermissions()
    }

    fun handleIPConnection() {
        viewState.requestWifiPermissions()
        disposables.add(
            connection
                .getConnectedWifiIp()
                .doOnSubscribe { loadingStateLD.value = true }
                .doFinally { loadingStateLD.value = false }
                .subscribe(
                    {
                        wifiDataLD.value = it
                    },
                    { viewState.showError(R.string.error_wifi) }
                )
        )
    }

    fun notifyScreenProjectionGranted(resultCode: Int, data: Intent) {
        viewState.startService(resultCode, data)
        viewState.showError(R.string.record_start)
        disposables.add(
            connection
                .connect()
                .doOnSubscribe { loadingStateLD.value = true }
                .doFinally { loadingStateLD.value = false }
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
                .doOnSubscribe { loadingStateLD.value = true }
                .doFinally { loadingStateLD.value = false }
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