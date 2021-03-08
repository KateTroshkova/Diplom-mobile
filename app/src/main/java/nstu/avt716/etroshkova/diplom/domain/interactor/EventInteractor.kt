package nstu.avt716.etroshkova.diplom.domain.interactor

import nstu.avt716.etroshkova.diplom.domain.api.DesktopRepositoryApi
import nstu.avt716.etroshkova.diplom.domain.model.event.Event

class EventInteractor {

    private val repository: DesktopRepositoryApi? = null

    fun receiveEvent(event: Event?) {
        repository?.receiveEvent(event)
    }
}