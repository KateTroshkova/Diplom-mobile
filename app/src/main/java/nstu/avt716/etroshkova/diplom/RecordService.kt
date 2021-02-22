package nstu.avt716.etroshkova.diplom

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
import java.io.IOException
import java.util.concurrent.TimeUnit

class RecordService : Service() {

    private val mediaRecorder: MediaRecorder by lazy { MediaRecorder() }
    private var virtualDisplay: VirtualDisplay? = null
    private var imageReader: ImageReader? = null
    private var timer: Disposable? = null

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
        measureScreen()
        mirror()
        return super.onStartCommand(intent, flags, startId)
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

    private fun getScreenshot(): Bitmap {
        return makeScreenshot() ?: getScreenshot()
    }

    private fun initImageReader() {
        imageReader = null
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
            setAudioSource(MediaRecorder.AudioSource.MIC)
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
        try {
            val image = imageReader?.acquireLatestImage()
            return image?.let {
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
        } catch (e: IllegalStateException) {
            initImageReader()
            return null
        }
    }

    private fun measureScreen() {
        val metrics = resources.displayMetrics
        width = metrics.widthPixels
        height = metrics.heightPixels
        dpi = metrics.densityDpi
    }

    private fun mirror() {
        timer = Observable.timer(SCREENSHOT_DELAY, TimeUnit.SECONDS)
            .repeat()
            .subscribeOn(Schedulers.newThread())
            .subscribe {
                val bitmap = getScreenshot()
                saveImage(applicationContext, bitmap)
            }
    }

    inner class RecordBinder : Binder() {
        val recordService: RecordService
            get() = this@RecordService
    }

    private companion object {
        private const val SCREENSHOT_DELAY = 1L
        private const val MAX_IMAGES = 10
        private const val ENCODE_BIT_RATE = 5 * 1024 * 1024
        private const val FRAME_RATE = 30
    }
}
