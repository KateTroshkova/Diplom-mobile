package nstu.avt716.etroshkova.diplom.presentation.di

import android.content.Context
import android.content.SharedPreferences
import nstu.avt716.etroshkova.diplom.data.repository.ConnectionRepository
import nstu.avt716.etroshkova.diplom.data.repository.PreferencesRepository
import nstu.avt716.etroshkova.diplom.domain.api.ConnectionRepositoryApi
import nstu.avt716.etroshkova.diplom.domain.api.PreferencesRepositoryApi
import nstu.avt716.etroshkova.diplom.presentation.service.RecordPresenter
import toothpick.config.Module

class Injector(context: Context) : Module() {

    init {
        bind(Context::class.java).toInstance(context)
        bind(SharedPreferences::class.java).toInstance(
            context.getSharedPreferences(
                "diplom_settings",
                Context.MODE_PRIVATE
            )
        )
        bind(RecordPresenter::class.java).to(RecordPresenter::class.java).singleton()
        bind(ConnectionRepositoryApi::class.java).to(ConnectionRepository::class.java).singleton()
        bind(PreferencesRepositoryApi::class.java).to(PreferencesRepository::class.java).singleton()
    }
}