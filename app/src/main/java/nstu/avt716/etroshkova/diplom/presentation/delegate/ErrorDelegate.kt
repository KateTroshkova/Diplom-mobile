package nstu.avt716.etroshkova.diplom.presentation.delegate

import android.content.Context
import android.util.Log
import android.widget.Toast

class ErrorDelegate(private val context: Context) {

    fun showToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    fun writeError(message: String) {
        Log.e("ERROR", message)
    }
}
