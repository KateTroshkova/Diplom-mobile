package nstu.avt716.etroshkova.diplom.presentation.service

import android.app.Service
import android.content.Intent
import android.media.projection.MediaProjection
import android.os.Binder
import android.os.IBinder
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import moxy.presenter.ProvidePresenter
import nstu.avt716.etroshkova.diplom.presentation.delegate.NotificationDelegate
import nstu.avt716.etroshkova.diplom.presentation.main.MainActivity
import toothpick.Toothpick
import java.util.concurrent.TimeUnit

class RecordService : Service(), ServiceView {

    private var timer: Disposable? = null
    private val notificationDelegate by lazy { NotificationDelegate() }

    var projection: MediaProjection? = null
        set(value) {
            field = value
            presenter.projection = value
        }

    val presenter: RecordPresenter by lazy { providePresenter() }

    @ProvidePresenter
    fun providePresenter(): RecordPresenter =
        Toothpick.openScopes("App").getInstance(RecordPresenter::class.java)

    override fun onCreate() {
        super.onCreate()
        presenter.onStart()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.action == MainActivity.RECORD_START_KEY) {
            measureScreen()
            mirror()
            val notification = notificationDelegate.createNotification(
                this,
                CHANNEL_ID,
                "foregroundRecord",
                "foregroundRecord",
                "recording..."
            )
            startForeground(972425, notification)
        }
        if (intent?.action == MainActivity.RECORD_STOP_KEY) {
            stopForeground(true)
            notificationDelegate.cancelNotification(this, 972425)
            stopSelfResult(startId)
        }
        return START_NOT_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        timer?.dispose()
        presenter.onStop()
        projection?.stop()
    }

    override fun onBind(intent: Intent): IBinder {
        return RecordBinder()
    }

    private fun measureScreen() {
        val metrics = resources.displayMetrics
        presenter.measureScreen(metrics.widthPixels, metrics.heightPixels, metrics.densityDpi)
    }

    private fun mirror() {
        timer = Observable.interval(SCREENSHOT_DELAY, TimeUnit.MILLISECONDS)
            .subscribeOn(Schedulers.newThread())
            .subscribe {
                presenter.sendScreenshot()
            }
    }

    inner class RecordBinder : Binder() {
        val recordService: RecordService
            get() = this@RecordService
    }

    private companion object {
        private const val SCREENSHOT_DELAY = 40L
        private const val CHANNEL_ID = "ForegroundScreenRecordingChannel"
    }
}
