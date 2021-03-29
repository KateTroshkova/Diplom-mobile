package nstu.avt716.etroshkova.diplom.data.repository

import android.os.Build
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.schedulers.Schedulers
import nstu.avt716.etroshkova.diplom.data.connection.ConnectionSourceFactory
import nstu.avt716.etroshkova.diplom.domain.api.ConnectionRepositoryApi
import nstu.avt716.etroshkova.diplom.domain.common.*
import java.io.File
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
        val info = "${Build.BRAND} ${Build.MODEL} ${Build.DEVICE} ${Build.ID}"
        writeTextFile(info)
    }

    private fun deleteMobileInfo() {
        deleteFile(File("$diplomPath/$textFileName"))
    }

    private fun deleteScreenshots() {
        for (i in 0..20) {
            deleteFile(File("$galleryPath/screenshot$i.jpg"))
        }
    }
}