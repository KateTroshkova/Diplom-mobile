package nstu.avt716.etroshkova.diplom.domain.interactor

import nstu.avt716.etroshkova.diplom.domain.api.DesktopRepositoryApi
import nstu.avt716.etroshkova.diplom.domain.model.Screenshot
import java.io.File

class VideoInteractor {

    private val repository: DesktopRepositoryApi? = null

    fun sendScreenshot(): Screenshot? {
        return repository?.sendScreenshot()
    }

    fun sendFile(file: File?) {
        repository?.sendFile(file)
    }

    fun receiveFile(): File? {
        return repository?.receiveFile()
    }
}