package nstu.avt716.etroshkova.diplom.presentation.service

import android.app.Service
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.PixelFormat
import android.hardware.display.DisplayManager
import android.hardware.display.VirtualDisplay
import android.media.ImageReader
import android.media.MediaRecorder
import android.media.projection.MediaProjection
import android.os.Binder
import android.os.IBinder
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import nstu.avt716.etroshkova.diplom.domain.common.saveImage
import nstu.avt716.etroshkova.diplom.domain.common.videoPath
import nstu.avt716.etroshkova.diplom.domain.interactor.PreferencesInteractor
import nstu.avt716.etroshkova.diplom.presentation.delegate.NotificationDelegate
import nstu.avt716.etroshkova.diplom.presentation.main.MainActivity
import toothpick.Toothpick
import java.io.IOException
import java.util.concurrent.TimeUnit

class RecordService : Service() {

    private val mediaRecorder: MediaRecorder by lazy { MediaRecorder() }
    private var virtualDisplay: VirtualDisplay? = null
    private var imageReader: ImageReader? = null
    private var timer: Disposable? = null
    private val notificationDelegate by lazy { NotificationDelegate() }
    private val preferences by lazy {
        Toothpick.openScopes("App").getInstance(PreferencesInteractor::class.java)
    }

    private var height = 1080
    private var width = 720
    private var dpi = 0
    private var running: Boolean = false

    var projection: MediaProjection? = null
        set(value) {
            field = value
            startRecord()
            initImageReader()
        }

    override fun onCreate() {
        super.onCreate()
        running = false
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
            startForeground(1, notification)
        }
        if (intent?.action == MainActivity.RECORD_STOP_KEY) {
            stopForeground(true)
            stopSelfResult(startId)
        }
        return START_NOT_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        timer?.dispose()
        try {
            stopRecord()
        } catch (e: Throwable) {
            e.printStackTrace()
        }
    }

    override fun onBind(intent: Intent): IBinder? {
        return RecordBinder()
    }

    private fun stopRecord(): Boolean {
        if (!running) return false
        running = false
        mediaRecorder.stop()
        mediaRecorder.reset()
        virtualDisplay?.release()
        projection?.stop()
        return true
    }

    private fun startRecord(): Boolean {
        if (running) return false
        projection
            ?.let {
                initRecorder()
                crateImageVirtualDisplay()
                mediaRecorder.start()
                running = true
                return true
            }
            ?: return false
    }

    private fun initImageReader() {
        imageReader = ImageReader.newInstance(width, height, PixelFormat.RGBA_8888, MAX_IMAGES)
        crateImageVirtualDisplay()
    }

    private fun crateImageVirtualDisplay() {
        virtualDisplay = projection?.createVirtualDisplay(
            "mediaprojection",
            width,
            height,
            dpi,
            DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
            imageReader?.surface,
            null,
            null
        )
    }

    private fun initRecorder() {
        mediaRecorder.apply {
            if (preferences.isAudioRecordAllowed()) {
                setAudioSource(MediaRecorder.AudioSource.MIC)
            }
            setVideoSource(MediaRecorder.VideoSource.SURFACE)
            setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
            setOutputFile(videoPath)
            setVideoSize(width, height)
            setVideoEncoder(MediaRecorder.VideoEncoder.H264)
            setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
            setVideoEncodingBitRate(ENCODE_BIT_RATE)
            setVideoFrameRate(FRAME_RATE)
            try {
                prepare()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    private fun makeScreenshot(): Bitmap? {
        val image = imageReader?.acquireLatestImage()
        val bitmap = image?.let {
            val width = image.width
            val height = image.height
            val planes = image.planes
            val buffer = planes[0].buffer
            val pixelStride = planes[0].pixelStride
            val rowStride = planes[0].rowStride
            val rowPadding = rowStride - pixelStride * width
            val bitmap = Bitmap.createBitmap(
                width + rowPadding / pixelStride,
                height,
                Bitmap.Config.ARGB_8888
            )
            bitmap.copyPixelsFromBuffer(buffer)
            Bitmap.createBitmap(bitmap, 0, 0, width, height)
        }
        image?.close()
        return bitmap
    }

    private fun measureScreen() {
        val metrics = resources.displayMetrics
        width = metrics.widthPixels
        height = metrics.heightPixels
        dpi = metrics.densityDpi
    }

    private fun mirror() {
        timer = Observable.interval(SCREENSHOT_DELAY, TimeUnit.MILLISECONDS)
            .subscribeOn(Schedulers.newThread())
            .subscribe {
                val bitmap = makeScreenshot()
                bitmap?.let { it1 ->
                    saveImage(
                        applicationContext,
                        it1
                    )
                }
            }
    }

    inner class RecordBinder : Binder() {
        val recordService: RecordService
            get() = this@RecordService
    }

    private companion object {
        private const val SCREENSHOT_DELAY = 40L
        private const val MAX_IMAGES = 5
        private const val ENCODE_BIT_RATE = 5 * 1024 * 1024
        private const val FRAME_RATE = 30
        private const val CHANNEL_ID = "ForegroundScreenRecordingChannel"
    }
}
