package nstu.avt716.etroshkova.diplom.presentation.delegate

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.projection.MediaProjection
import android.media.projection.MediaProjectionManager
import androidx.core.app.ActivityCompat

class PermissionDelegate(private val context: Activity) {

    private val projectionManager by lazy {
        context.getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
    }

    fun requestPermissions(permissions: Array<String>) {
        ActivityCompat.requestPermissions(context, permissions, PERMISSION_CODE)
    }

    fun isPermissionsGranted(results: IntArray) =
        results.all { it == PackageManager.PERMISSION_GRANTED }

    fun buildMediaProjection(code: Int, data: Intent): MediaProjection =
        projectionManager.getMediaProjection(code, data)

    fun requestMediaProjection() {
        context.startActivityForResult(
            projectionManager.createScreenCaptureIntent(),
            MEDIA_PROJECTION_CODE
        )
    }

    companion object {
        const val MEDIA_PROJECTION_CODE = 1001
        const val PERMISSION_CODE = 1002
    }
}
