package nstu.avt716.etroshkova.diplom

import android.Manifest
import android.app.Activity
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.media.projection.MediaProjection
import android.os.Bundle
import android.os.IBinder
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        stopButton.setOnClickListener {
            try {
                unbindService(connection)
                val intent = Intent(this, RecordService::class.java)
                intent.action = RECORD_STOP_KEY
                startService(intent)
            } catch (e: Throwable) {
                e.printStackTrace()
            }
        }
        requestPermissions()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode != MEDIA_PROJECTION_CODE) return
        if (resultCode == Activity.RESULT_OK) {
            if (data == null) return
            val intent = Intent(this, RecordService::class.java)
            intent.action = RECORD_START_KEY
            startService(intent)
            projection = permissionDelegate.buildMediaProjection(resultCode, data)
            bindService(
                Intent(this, RecordService::class.java),
                connection,
                Context.BIND_AUTO_CREATE
            )
            toastDelegate.showToast(R.string.record_start)
        } else {
            toastDelegate.showToast(R.string.error_permission_denied)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                grantResults[1] == PackageManager.PERMISSION_GRANTED
            ) {
                permissionDelegate.requestMediaProjection(MEDIA_PROJECTION_CODE)
            } else {
                toastDelegate.showToast(R.string.error_permission_denied)
            }
        }
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ),
            PERMISSION_CODE
        )
    }

    companion object {
        const val MEDIA_PROJECTION_CODE = 1001
        const val PERMISSION_CODE = 1002
        const val RECORD_START_KEY = "1003"
        const val RECORD_STOP_KEY = "1004"
    }
}