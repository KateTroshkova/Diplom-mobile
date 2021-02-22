package nstu.avt716.etroshkova.diplom

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.annotation.RequiresApi
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream

val imageFileName: String
    get() = "${System.currentTimeMillis()}.jpg"

val videoFileName: String
    get() = "${System.currentTimeMillis()}.mp4"

val galleryPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)

val videoPath = "$galleryPath$videoFileName"

@RequiresApi(Build.VERSION_CODES.Q)
fun videoUri(context: Context) = context.contentResolver.let { resolver ->
    val contentValues = ContentValues().apply {
        put(MediaStore.MediaColumns.DISPLAY_NAME, videoFileName)
        put(MediaStore.MediaColumns.MIME_TYPE, "video/mp4")
        put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
    }
    resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
}

@RequiresApi(Build.VERSION_CODES.Q)
fun imageUri(context: Context) = context.contentResolver.let { resolver ->
    val contentValues = ContentValues().apply {
        put(MediaStore.MediaColumns.DISPLAY_NAME, imageFileName)
        put(MediaStore.MediaColumns.MIME_TYPE, "image/jpg")
        put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
    }
    resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
}

fun saveImage(context: Context, bitmap: Bitmap) {
    val fos: OutputStream? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        context.contentResolver?.let { resolver ->
            imageUri(context)?.let { resolver.openOutputStream(it) }
        }
    } else {
        FileOutputStream(File(galleryPath, imageFileName))
    }
    fos?.use {
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it)
    }
}