package nstu.avt716.etroshkova.diplom.data.repository

import nstu.avt716.etroshkova.diplom.data.connection.ConnectionSourceFactory
import nstu.avt716.etroshkova.diplom.domain.model.Screenshot
import nstu.avt716.etroshkova.diplom.domain.model.event.Event
import java.io.File

class DesktopRepository {
    private val factory: ConnectionSourceFactory? = null

    fun sendEvent(event: Event?) {}

    fun receiveScreenshot(): Screenshot? {
        return null
    }

    fun sendFile(file: File?) {}

    fun receiveFile(): File? {
        return null
    }
}