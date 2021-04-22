package nstu.avt716.etroshkova.diplom.presentation.di

import android.content.Context
import nstu.avt716.etroshkova.diplom.data.repository.ConnectionRepository
import nstu.avt716.etroshkova.diplom.domain.api.ConnectionRepositoryApi
import nstu.avt716.etroshkova.diplom.presentation.service.RecordPresenter
import toothpick.config.Module

class Injector(context: Context) : Module() {

    init {
        bind(Context::class.java).toInstance(context)
        bind(RecordPresenter::class.java).to(RecordPresenter::class.java).singleton()
        bind(ConnectionRepositoryApi::class.java).to(ConnectionRepository::class.java).singleton()
    }
}