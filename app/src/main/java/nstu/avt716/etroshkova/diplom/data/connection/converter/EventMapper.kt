package nstu.avt716.etroshkova.diplom.data.connection.converter

import nstu.avt716.etroshkova.diplom.data.connection.entity.EventResponse
import nstu.avt716.etroshkova.diplom.domain.common.Mapper
import nstu.avt716.etroshkova.diplom.domain.model.event.Event

object EventMapper : Mapper<Event, EventResponse>()