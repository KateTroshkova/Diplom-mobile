package nstu.avt716.etroshkova.diplom.domain.interactor

import nstu.avt716.etroshkova.diplom.domain.api.PreferencesRepositoryApi
import javax.inject.Inject

class PreferencesInteractor @Inject constructor(
    private val preferencesRepository: PreferencesRepositoryApi
) {

    fun isAudioRecordAllowed() = preferencesRepository.isAudioRecordAllowed

    fun isSaveVideoAllowed() = preferencesRepository.isSaveVideoAllowed

    fun allowAudioRecord(isAudioRecordAllow: Boolean) {
        preferencesRepository.allowAudioRecord(isAudioRecordAllow)
    }

    fun allowVideoSave(isSaveVideoAllowed: Boolean) {
        preferencesRepository.allowVideoSave(isSaveVideoAllowed)
    }
}