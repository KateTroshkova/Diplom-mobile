package nstu.avt716.etroshkova.diplom

import android.app.Activity
import android.content.Context
import android.media.projection.MediaProjectionManager

class PermissionDelegate(private val context: Activity) {

    private val projectionManager by lazy {
        context.getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
    }

    fun requestMediaProjection(code: Int) {
        context.startActivityForResult(projectionManager.createScreenCaptureIntent(), code)
    }
}