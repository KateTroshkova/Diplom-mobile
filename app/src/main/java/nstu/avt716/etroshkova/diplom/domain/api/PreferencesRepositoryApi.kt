package nstu.avt716.etroshkova.diplom.domain.api

interface PreferencesRepositoryApi {

    var isAudioRecordAllowed: Boolean

    var isSaveVideoAllowed: Boolean

    var fileToSend: String

    fun allowAudioRecord(isAudioRecordAllow: Boolean)

    fun allowVideoSave(isSaveVideoAllowed: Boolean)

    fun writeFileToSend(filePath: String)
}
