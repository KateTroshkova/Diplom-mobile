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
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.lifecycle.Observer
import ir.sohreco.androidfilechooser.ExternalStorageNotAvailableException
import ir.sohreco.androidfilechooser.FileChooser
import kotlinx.android.synthetic.main.activity_main.*
import moxy.MvpAppCompatActivity
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import nstu.avt716.etroshkova.diplom.R
import nstu.avt716.etroshkova.diplom.presentation.delegate.AdDelegate
import nstu.avt716.etroshkova.diplom.presentation.delegate.PermissionDelegate
import nstu.avt716.etroshkova.diplom.presentation.delegate.ToastDelegate
import nstu.avt716.etroshkova.diplom.presentation.information.InformationActivity
import nstu.avt716.etroshkova.diplom.presentation.service.RecordService
import toothpick.Toothpick

class MainActivity : MvpAppCompatActivity(), MainView {

    private val permissionDelegate by lazy { PermissionDelegate(this) }

    private val adDelegate by lazy { AdDelegate(this) }

    private val toastDelegate by lazy { ToastDelegate(this) }

    private var projection: MediaProjection? = null
    private var recordService: RecordService? = null

    private var resultCode: Int? = null
    private var data: Intent? = null

    private val connection = object : ServiceConnection {
        override fun onServiceDisconnected(name: ComponentName?) {}

        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as RecordService.RecordBinder
            recordService = binder.recordService
            resultCode?.let { code ->
                data?.let { data ->
                    recordService?.projection =
                        permissionDelegate.buildMediaProjection(code, data)
                }
            }
        }

    }

    private val usbPermissions by lazy {
        arrayOf(
            Manifest.permission.FOREGROUND_SERVICE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
        )
    }

    private val wifiPermissions by lazy {
        arrayOf(
            Manifest.permission.FOREGROUND_SERVICE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_WIFI_STATE
        )
    }

    private val audioPermissions by lazy {
        arrayOf(
            Manifest.permission.RECORD_AUDIO
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
        audioCheckBox.setOnCheckedChangeListener { _, isChecked ->
            presenter.allowAudioRecord(isChecked)
        }
        videoCheckBox.setOnCheckedChangeListener { _, isChecked ->
            presenter.allowSaveVideo(isChecked)
        }
        sendButton.setOnClickListener {
            showFileChooser()
        }
    }

    override fun onResume() {
        super.onResume()
        observeState()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PermissionDelegate.MEDIA_PROJECTION_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                if (data == null) return
                presenter.notifyScreenProjectionGranted(resultCode, data)
            } else {
                presenter.notifyScreenProjectionDenied()
            }
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

    override fun requestUSBPermissions(isAudioRecordAllowed: Boolean) {
        permissionDelegate.requestPermissions(
            usbPermissions + if (isAudioRecordAllowed) audioPermissions else arrayOf()
        )
    }

    override fun requestWifiPermissions(isAudioRecordAllowed: Boolean) {
        permissionDelegate.requestPermissions(
            wifiPermissions + if (isAudioRecordAllowed) audioPermissions else arrayOf()
        )
    }

    override fun requestAudioPermission() {
        permissionDelegate.requestPermissions(audioPermissions)
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
        this.resultCode = resultCode
        this.data = data
        //projection = permissionDelegate.buildMediaProjection(resultCode, data)
        bindService(
            Intent(this, RecordService::class.java),
            connection,
            Context.BIND_AUTO_CREATE
        )
    }

    override fun applyAudioSettings(isAudioRecordAllowed: Boolean) {
        audioCheckBox.isChecked = isAudioRecordAllowed
    }

    override fun applyVideoSettings(isSaveVideoAllowed: Boolean) {
        videoCheckBox.isChecked = isSaveVideoAllowed
    }

    override fun stopService() {
        try {
            unbindService(connection)
            val intent = Intent(this, RecordService::class.java)
            intent.action = RECORD_STOP_KEY
            startService(intent)
            adDelegate.loadAd()
        } catch (e: Throwable) {
            e.printStackTrace()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.settings -> {
                presenter.openInformation()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onBackPressed() {
        if (fileChooserContainer.visibility == View.VISIBLE) {
            fileChooserContainer.visibility = View.GONE
        } else {
            super.onBackPressed()
        }
    }

    private fun observeState() {
        presenter.wifiData.observe(this, wifiObserver)
        presenter.loadingState.observe(this, loadingObserver)
        presenter.informationEvent.observe(this, informationObserver)
    }

    private val wifiObserver
        get() = Observer<String> {
            wifiTextView.text = getString(R.string.info_wifi, it)
        }

    private val loadingObserver
        get() = Observer<Boolean> {
            loadingView.visibility = if (it) View.VISIBLE else View.GONE
        }

    private val informationObserver
        get() = Observer<Boolean> {
            startActivity(Intent(this, InformationActivity::class.java))
        }

    private fun showFileChooser() {
        val builder =
            FileChooser.Builder(FileChooser.ChooserType.FILE_CHOOSER, FileChooser.ChooserListener {
                presenter.sendFile(it)
                fileChooserContainer.visibility = View.GONE
            }
            )
                .setSelectMultipleFilesButtonText("Choose")
                .setSelectMultipleFilesButtonBackground(R.drawable.shape_rect_white)
                .setListItemsTextColor(R.color.colorPrimary)
                .setPreviousDirectoryButtonIcon(R.drawable.ic_prev_dir)
                .setDirectoryIcon(R.drawable.ic_folder)
                .setFileIcon(R.drawable.ic_file)
        try {
            val fileChooserFragment = builder.build()
            val transaction = supportFragmentManager.beginTransaction()
            transaction.replace(R.id.fileChooserContainer, fileChooserFragment)
            transaction.commit()
            fileChooserContainer.visibility = View.VISIBLE
        } catch (e: ExternalStorageNotAvailableException) {
            e.printStackTrace()
        }

    }

    companion object {
        const val RECORD_START_KEY = "1003"
        const val RECORD_STOP_KEY = "1004"
    }
}
