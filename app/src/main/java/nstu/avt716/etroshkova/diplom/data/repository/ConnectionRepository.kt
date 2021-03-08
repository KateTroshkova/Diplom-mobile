package nstu.avt716.etroshkova.diplom.data.repository

import nstu.avt716.etroshkova.diplom.data.connection.ConnectionSourceFactory

import nstu.avt716.etroshkova.diplom.domain.api.ConnectionRepositoryApi

class ConnectionRepository : ConnectionRepositoryApi {
    private val factory: ConnectionSourceFactory? = null
    override fun connect() {}
    override fun disconnect() {}
}