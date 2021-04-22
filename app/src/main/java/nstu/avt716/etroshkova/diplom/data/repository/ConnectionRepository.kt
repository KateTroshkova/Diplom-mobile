package nstu.avt716.etroshkova.diplom.data.repository

import android.content.Context
import android.content.Context.WIFI_SERVICE
import android.net.wifi.WifiManager
import android.os.Build
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import nstu.avt716.etroshkova.diplom.data.connection.ConnectionSourceFactory
import nstu.avt716.etroshkova.diplom.domain.api.ConnectionRepositoryApi
import nstu.avt716.etroshkova.diplom.domain.common.deleteFile
import nstu.avt716.etroshkova.diplom.domain.common.diplomPath
import nstu.avt716.etroshkova.diplom.domain.common.textFileName
import nstu.avt716.etroshkova.diplom.domain.common.writeTextFile
import java.io.File
import java.math.BigInteger
import java.net.InetAddress
import java.net.UnknownHostException
import java.nio.ByteOrder
import java.util.concurrent.TimeUnit
import javax.inject.Inject


class ConnectionRepository @Inject constructor(
    private val context: Context
) : ConnectionRepositoryApi {
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

    override fun getConnectedWifiIp(): Single<String> = Single
        .fromCallable {
            var ipAddress =
                (context.applicationContext.getSystemService(WIFI_SERVICE) as WifiManager).connectionInfo.ipAddress
            if (ByteOrder.nativeOrder().equals(ByteOrder.LITTLE_ENDIAN)) {
                ipAddress = Integer.reverseBytes(ipAddress)
            }
            val ipByteArray: ByteArray = BigInteger.valueOf(ipAddress.toLong()).toByteArray()
            val ipAddressString: String = try {
                InetAddress.getByAddress(ipByteArray).hostAddress
            } catch (ex: UnknownHostException) {
                ""
            }
            ipAddressString
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
            deleteFile(File("$diplomPath/screenshot$i.jpg"))
        }
    }
}