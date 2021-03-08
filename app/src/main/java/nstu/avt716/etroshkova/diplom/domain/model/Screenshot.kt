package nstu.avt716.etroshkova.diplom.domain.model

import android.graphics.Bitmap

data class Screenshot(
    val screenshot: Bitmap,
    val width: Int,
    val height: Int,
    val mobileInfo: String,
    val timestamp: Long
)