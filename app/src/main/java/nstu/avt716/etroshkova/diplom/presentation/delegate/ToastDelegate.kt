package nstu.avt716.etroshkova.diplom.presentation.delegate

import android.content.Context
import android.widget.Toast

class ToastDelegate(private val context: Context) {

    fun showToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    fun showToast(message: Int) {
        Toast.makeText(context, context.getString(message), Toast.LENGTH_SHORT).show()
    }
}