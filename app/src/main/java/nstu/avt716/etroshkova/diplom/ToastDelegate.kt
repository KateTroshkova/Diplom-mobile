package nstu.avt716.etroshkova.diplom

import android.content.Context
import android.widget.Toast
import androidx.core.content.ContextCompat

class ToastDelegate(private val context: Context) {

    fun showToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    fun showToast(message: Int) {
        Toast.makeText(context, context.getString(message), Toast.LENGTH_SHORT).show()
    }
}