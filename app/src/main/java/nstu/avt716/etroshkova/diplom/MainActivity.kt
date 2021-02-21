package nstu.avt716.etroshkova.diplom

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private val permissionDelegate by lazy { PermissionDelegate(this) }

    private val toastDelegate by lazy { ToastDelegate(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        permissionDelegate.requestMediaProjection(MEDIA_PROJECTION_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode != MEDIA_PROJECTION_CODE) return
        if (resultCode == Activity.RESULT_OK) {
            toastDelegate.showToast(R.string.record_start)
        } else {
            toastDelegate.showToast(R.string.error_permission_denied)
        }
    }

    companion object {
        const val MEDIA_PROJECTION_CODE = 1001
    }
}