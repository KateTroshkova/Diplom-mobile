package nstu.avt716.etroshkova.diplom.data.repository

import android.os.Build
import android.os.Environment
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.schedulers.Schedulers
import nstu.avt716.etroshkova.diplom.data.connection.ConnectionSourceFactory

import nstu.avt716.etroshkova.diplom.domain.api.ConnectionRepositoryApi
import java.io.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class ConnectionRepository @Inject constructor() : ConnectionRepositoryApi {
    private val factory: ConnectionSourceFactory? = null

    override fun connect(): Completable = Completable
        .fromAction { writeMobileInfo() }
        .delay(2, TimeUnit.SECONDS)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())

    override fun disconnect(): Completable = Completable
        .fromAction {
            deleteMobileInfo()
            deleteScreenshots()
        }
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())

    private fun writeMobileInfo() {
        val root = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
        val dir = File(root.absolutePath.toString() + "/diplom")
        dir.mkdirs()
        val file = File(dir, "mobile_info.txt")
        try {
            val pw = PrintWriter(FileOutputStream(file))
            val info = "${Build.BRAND} ${Build.MODEL} ${Build.DEVICE} ${Build.ID}"
            pw.println(info)
            pw.flush()
            pw.close()
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun deleteMobileInfo() {
        val root = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
        val dir = File(root.absolutePath.toString() + "/diplom/mobile_info.txt")
        if (dir.exists()) {
            dir.delete()
        }
    }

    private fun deleteScreenshots() {
        val root = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
        for (i in 0..20) {
            val dir = File(root.absolutePath.toString() + "/screenshot$i.jpg")
            if (dir.exists()) {
                dir.delete()
            }
        }
    }
}