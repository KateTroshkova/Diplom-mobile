package nstu.avt716.etroshkova.diplom.domain.model

import org.json.JSONObject


data class DeviceInfo(
    val id: String,
    val brand: String,
    val model: String,
    val device: String,
    val isVideoNeeded: Boolean,
    val fileToSend: String
) {

    fun toJson(): String {
        val json = JSONObject()
        json.put("id", id)
        json.put("brand", brand)
        json.put("model", model)
        json.put("device", device)
        json.put("isVideoNeeded", isVideoNeeded)
        json.put("fileToSend", fileToSend)
        return json.toString()
    }
}