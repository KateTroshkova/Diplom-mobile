package nstu.avt716.etroshkova.diplom.domain.interactor

import nstu.avt716.etroshkova.diplom.domain.api.ConnectionRepositoryApi

class ConnectionInteractor {
    private val repository: ConnectionRepositoryApi? = null

    fun connect() {
        repository!!.connect()
    }

    fun disconnect() {
        repository!!.disconnect()
    }
}