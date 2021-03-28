package nstu.avt716.etroshkova.diplom.domain.interactor

import io.reactivex.rxjava3.core.Completable
import nstu.avt716.etroshkova.diplom.domain.api.ConnectionRepositoryApi
import javax.inject.Inject

class ConnectionInteractor @Inject constructor(
    private val repository: ConnectionRepositoryApi
) {

    fun connect(): Completable = repository.connect()

    fun disconnect(): Completable = repository.disconnect()
}