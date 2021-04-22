package nstu.avt716.etroshkova.diplom.presentation.main

import android.Manifest
import android.app.Activity
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.media.projection.MediaProjection
import android.os.Bundle
import android.os.IBinder
import androidx.lifecycle.Observer
import kotlinx.android.synthetic.main.activity_main.*
import moxy.MvpAppCompatActivity
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import nstu.avt716.etroshkova.diplom.R
import nstu.avt716.etroshkova.diplom.presentation.delegate.PermissionDelegate
import nstu.avt716.etroshkova.diplom.presentation.delegate.ToastDelegate
import nstu.avt716.etroshkova.diplom.presentation.service.RecordService
import toothpick.Toothpick

class MainActivity : MvpAppCompatActivity(), MainView {

    private val permissionDelegate by lazy { PermissionDelegate(this) }

    private val toastDelegate by lazy { ToastDelegate(this) }

    private var projection: MediaProjection? = null
    private var recordService: RecordService? = null

    private val connection = object : ServiceConnection {
        override fun onServiceDisconnected(name: ComponentName?) {}

        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as RecordService.RecordBinder
            recordService = binder.recordService
            recordService?.projection = projection
        }

    }

    private val usbPermissions by lazy {
        arrayOf(
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
    }

    private val wifiPermissions by lazy {
        arrayOf(
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_WIFI_STATE
        )
    }

    @InjectPresenter
    lateinit var presenter: MainPresenter

    @ProvidePresenter
    fun providePresenter(): MainPresenter =
        Toothpick.openScopes("App").getInstance(MainPresenter::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        stopButton.setOnClickListener {
            presenter.disconnect()
            wifiTextView.text = ""
        }
        usbConnectButton.setOnClickListener {
            presenter.handleUsbConnection()
        }
        wifiConnectButton.setOnClickListener {
            presenter.handleIPConnection()
        }
    }

    override fun onResume() {
        super.onResume()
        observeState()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode != PermissionDelegate.MEDIA_PROJECTION_CODE) return
        if (resultCode == Activity.RESULT_OK) {
            if (data == null) return
            presenter.notifyScreenProjectionGranted(resultCode, data)
        } else {
            presenter.notifyScreenProjectionDenied()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PermissionDelegate.PERMISSION_CODE) {
            if (permissionDelegate.isPermissionsGranted(grantResults)) {
                presenter.notifyPermissionsGranted()
            } else {
                presenter.notifyPermissionDenied()
            }
        }
    }

    override fun requestUSBPermissions() {
        permissionDelegate.requestPermissions(usbPermissions)
    }

    override fun requestWifiPermissions() {
        permissionDelegate.requestPermissions(wifiPermissions)
    }

    override fun requestScreenProjection() {
        permissionDelegate.requestMediaProjection()
    }

    override fun showError(error: Int) {
        toastDelegate.showToast(error)
    }

    override fun startService(resultCode: Int, data: Intent) {
        val intent = Intent(this, RecordService::class.java)
        intent.action = RECORD_START_KEY
        startService(intent)
        projection = permissionDelegate.buildMediaProjection(resultCode, data)
        bindService(
            Intent(this, RecordService::class.java),
            connection,
            Context.BIND_AUTO_CREATE
        )
    }

    override fun stopService() {
        try {
            unbindService(connection)
            val intent = Intent(this, RecordService::class.java)
            intent.action = RECORD_STOP_KEY
            startService(intent)
        } catch (e: Throwable) {
            e.printStackTrace()
        }
    }

    private fun observeState() {
        presenter.wifiData.observe(this, wifiObserver)
    }

    private val wifiObserver
        get() = Observer<String> {
            wifiTextView.text = it
        }

    companion object {
        const val RECORD_START_KEY = "1003"
        const val RECORD_STOP_KEY = "1004"
    }
}