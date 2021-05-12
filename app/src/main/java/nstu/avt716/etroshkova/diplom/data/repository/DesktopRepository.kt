package nstu.avt716.etroshkova.diplom.data.repository

import android.content.Context
import nstu.avt716.etroshkova.diplom.domain.api.DesktopRepositoryApi
import nstu.avt716.etroshkova.diplom.domain.common.saveImage
import nstu.avt716.etroshkova.diplom.domain.model.Screenshot
import javax.inject.Inject

class DesktopRepository @Inject constructor(
    private val context: Context
) : DesktopRepositoryApi {

    override fun sendScreenshot(screenshot: Screenshot) {
        saveImage(context, screenshot.screenshot)
    }

}
