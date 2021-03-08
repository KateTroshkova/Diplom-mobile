package nstu.avt716.etroshkova.diplom.data.connection

class ConnectionSourceFactory internal constructor(type: String) {
    private var connection: ConnectionSource? = null

    init {
        if (type === "IP") connection = IPSource()
        if (type === "Wifi") connection = WifiSource()
        if (type === "USB") connection = USBSource()
    }
}