package nstu.avt716.etroshkova.diplom.presentation

import android.app.Application
import android.os.Build
import android.os.Process
import moxy.MvpFacade
import nstu.avt716.etroshkova.diplom.presentation.di.Injector
import toothpick.Toothpick
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import kotlin.system.exitProcess

class CustomApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        if (isRooted()) {
            Process.killProcess(Process.myPid())
            exitProcess(0)
        }
        val appScope = Toothpick.openScope("App")
        appScope.installModules(Injector(applicationContext))
        Toothpick.inject(this, appScope)
        MvpFacade.init()
    }

    private fun isRooted() =
        isTestKeysExist() || isRootSpecificFilesExist() || isRootSpecificCommandExecutable()

    private fun isTestKeysExist() = Build.TAGS?.contains("test-keys") ?: false

    private fun isRootSpecificFilesExist() = arrayOf(
        "/system/app/Superuser.apk",
        "/data/local/su", "/data/local/busybox",
        "/data/local/bin/su", "/data/local/bin/busybox",
        "/data/local/xbin/su", "/data/local/xbin/busybox",
        "/sbin/su", "/sbin/busybox",
        "/su/bin/su", "/su/bin/busybox",
        "/system/bin/su", "/system/bin/busybox",
        "/system/bin/.ext/su", "/system/bin/.ext/busybox",
        "/system/bin/failsafe/su", "/system/bin/failsafe/busybox",
        "/system/xbin/su", "/system/xbin/busybox",
        "/system/sd/xbin/su", "/system/sd/xbin/busybox",
        "/cache/su", "/cache/busybox",
        "/data/su", "/data/busybox",
        "/dev/su", "/dev/busybox"
    )
        .any { (File(it).exists()) }

    private fun isRootSpecificCommandExecutable(): Boolean {
        var process: java.lang.Process? = null
        return try {
            process = Runtime.getRuntime().exec(arrayOf("/system/xbin/which", "su"))
            BufferedReader(InputStreamReader(process.inputStream)).readLine() != null
        } catch (t: Throwable) {
            false
        } finally {
            process?.destroy()
        }
    }
}