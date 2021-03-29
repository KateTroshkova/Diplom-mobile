package nstu.avt716.etroshkova.diplom.domain.common

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.annotation.RequiresApi
import java.io.*

var imageIndex: Int = 0

fun imageFileName(n: Int): String = "screenshot$n.jpg"

val videoFileName: String
    get() = "record1.mp4"

val textFileName: String
    get() = "mobile_info.txt"

val galleryPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)

val diplomPath = "${galleryPath.absolutePath}/diplom"

val videoPath = "$diplomPath/$videoFileName"

@RequiresApi(Build.VERSION_CODES.Q)
fun videoUri(context: Context) = context.contentResolver.let { resolver ->
    val contentValues = ContentValues().apply {
        put(
            MediaStore.MediaColumns.DISPLAY_NAME,
            videoFileName
        )
        put(MediaStore.MediaColumns.MIME_TYPE, "video/mp4")
        put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
    }
    resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
}

@RequiresApi(Build.VERSION_CODES.Q)
fun imageUri(context: Context) = context.contentResolver.let { resolver ->
    val contentValues = ContentValues().apply {
        put(
            MediaStore.MediaColumns.DISPLAY_NAME,
            imageFileName(imageIndex)
        )
        put(MediaStore.MediaColumns.MIME_TYPE, "image/jpg")
        put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
    }
    resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
}

fun saveImage(context: Context, bitmap: Bitmap) {
    val dir = File(diplomPath)
    dir.mkdirs()
    imageIndex++
    if (imageIndex == 20) {
        imageIndex = 0
    }
    val fos: OutputStream? = /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        context.contentResolver?.let { resolver ->
            imageUri(context)?.let { resolver.openOutputStream(it, "wt") }
        }
    } else {*/
        FileOutputStream(
            File(
                diplomPath,
                imageFileName(imageIndex)
            )
        )
    //}
    fos?.use {
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it)
    }
}

fun writeTextFile(content: String) {
    val dir = File(diplomPath)
    dir.mkdirs()
    val file = File(dir, textFileName)
    try {
        val pw = PrintWriter(FileOutputStream(file))
        pw.println(content)
        pw.flush()
        pw.close()
    } catch (e: FileNotFoundException) {
        e.printStackTrace()
    } catch (e: IOException) {
        e.printStackTrace()
    }
}

fun deleteFile(dir: File) {
    if (dir.exists()) {
        dir.delete()
    }
}