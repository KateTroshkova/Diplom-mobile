package nstu.avt716.etroshkova.diplom.data.connection.entity

import android.graphics.Bitmap

data class ScreenshotRequest(
    val screenshot: Bitmap,
    val width: Int,
    val height: Int,
    val mobileInfo: String,
    val timestamp: Long
)