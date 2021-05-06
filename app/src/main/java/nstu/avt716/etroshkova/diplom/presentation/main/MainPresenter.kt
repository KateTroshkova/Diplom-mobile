package nstu.avt716.etroshkova.diplom.presentation.main

import android.content.Intent
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import moxy.InjectViewState
import moxy.MvpPresenter
import nstu.avt716.etroshkova.diplom.R
import nstu.avt716.etroshkova.diplom.domain.interactor.ConnectionInteractor
import nstu.avt716.etroshkova.diplom.domain.interactor.PreferencesInteractor
import nstu.avt716.etroshkova.diplom.presentation.common.SingleLiveData
import javax.inject.Inject

@InjectViewState
class MainPresenter @Inject constructor(
    private val connection: ConnectionInteractor,
    private val preferences: PreferencesInteractor
) : MvpPresenter<MainView>() {

    private var isPermissionsGranted = false
    private var disposables = mutableListOf<Disposable>()
    private val wifiDataLD: MutableLiveData<String> by lazy { MutableLiveData<String>() }
    private val loadingStateLD: MutableLiveData<Boolean> by lazy { MutableLiveData<Boolean>() }
    private val informationEventLD: SingleLiveData<Boolean> by lazy { SingleLiveData<Boolean>() }
    val wifiData: LiveData<String> by lazy { wifiDataLD }
    val loadingState: LiveData<Boolean> by lazy { loadingStateLD }
    val informationEvent: LiveData<Boolean> by lazy { informationEventLD }

    override fun attachView(view: MainView?) {
        super.attachView(view)
        viewState.applyAudioSettings(preferences.isAudioRecordAllowed())
        viewState.applyVideoSettings(preferences.isSaveVideoAllowed())
    }

    override fun destroyView(view: MainView?) {
        super.destroyView(view)
        clear()
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

    fun allowAudioRecord(isAudioRecordAllowed: Boolean) {
        preferences.allowAudioRecord(isAudioRecordAllowed)
    }

    fun allowSaveVideo(isSaveVideoAllowed: Boolean) {
        preferences.allowVideoSave(isSaveVideoAllowed)
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
        clear()
        disposables.add(
            connection
                .disconnect()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
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

    fun openInformation() {
        informationEventLD.value = true
    }

    fun sendFile(filePath: String) {
        preferences.writeFileToSend(filePath)
    }

    private fun clear() {
        disposables.forEach {
            it.dispose()
        }
    }
}