package nstu.avt716.etroshkova.diplom.domain.api

import nstu.avt716.etroshkova.diplom.domain.model.Screenshot

interface DesktopRepositoryApi {

    fun sendScreenshot(screenshot: Screenshot)
}
