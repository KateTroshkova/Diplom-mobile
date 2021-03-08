package nstu.avt716.etroshkova.diplom.domain.api

import nstu.avt716.etroshkova.diplom.domain.model.Screenshot
import nstu.avt716.etroshkova.diplom.domain.model.event.Event
import java.io.File

interface DesktopRepositoryApi {

    fun receiveEvent(event: Event?)

    fun sendScreenshot(): Screenshot?

    fun sendFile(file: File?)

    fun receiveFile(): File?
}