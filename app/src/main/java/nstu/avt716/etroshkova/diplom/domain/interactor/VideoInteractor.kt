package nstu.avt716.etroshkova.diplom.domain.interactor

import nstu.avt716.etroshkova.diplom.domain.api.DesktopRepositoryApi
import nstu.avt716.etroshkova.diplom.domain.model.Screenshot
import javax.inject.Inject

class VideoInteractor @Inject constructor(
    private val repository: DesktopRepositoryApi
) {

    fun sendScreenshot(screenshot: Screenshot) = repository.sendScreenshot(screenshot)
}
