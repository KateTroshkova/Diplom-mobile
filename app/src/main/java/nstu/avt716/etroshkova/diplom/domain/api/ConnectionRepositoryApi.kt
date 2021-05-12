package nstu.avt716.etroshkova.diplom.domain.api

import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single

interface ConnectionRepositoryApi {

    fun connect(): Completable

    fun disconnect(): Completable

    fun getConnectedWifiIp(): Single<String>
}
