package nstu.avt716.etroshkova.diplom.domain.api

import io.reactivex.rxjava3.core.Completable

interface ConnectionRepositoryApi {

    fun connect(): Completable

    fun disconnect(): Completable
}