package nstu.avt716.etroshkova.diplom.data.repository

import android.content.SharedPreferences
import nstu.avt716.etroshkova.diplom.domain.api.PreferencesRepositoryApi
import javax.inject.Inject

class PreferencesRepository @Inject constructor(
    sharedPreferences: SharedPreferences
) : BasePreferences(sharedPreferences), PreferencesRepositoryApi {

    override var isAudioRecordAllowed: Boolean
        get() = preferences.readBoolean(AUDIO_SETTINGS, default = true)
        set(value) {
            preferences.saveBoolean(AUDIO_SETTINGS, value)
        }

    override var isSaveVideoAllowed: Boolean
        get() = preferences.readBoolean(VIDEO_SETTINGS, default = true)
        set(value) {
            preferences.saveBoolean(VIDEO_SETTINGS, value)
        }

    override fun allowAudioRecord(isAudioRecordAllow: Boolean) {
        this.isAudioRecordAllowed = isAudioRecordAllow
    }

    override fun allowVideoSave(isSaveVideoAllowed: Boolean) {
        this.isSaveVideoAllowed = isSaveVideoAllowed
    }

    companion object {
        private const val AUDIO_SETTINGS = "audio_settings"
        private const val VIDEO_SETTINGS = "video_settings"
    }
}