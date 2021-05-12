package nstu.avt716.etroshkova.diplom.presentation.service

import android.graphics.Bitmap
import android.graphics.PixelFormat
import android.hardware.display.DisplayManager
import android.hardware.display.VirtualDisplay
import android.media.ImageReader
import android.media.MediaRecorder
import android.media.projection.MediaProjection
import moxy.InjectViewState
import moxy.MvpPresenter
import nstu.avt716.etroshkova.diplom.domain.common.videoPath
import nstu.avt716.etroshkova.diplom.domain.interactor.PreferencesInteractor
import nstu.avt716.etroshkova.diplom.domain.interactor.VideoInteractor
import nstu.avt716.etroshkova.diplom.domain.model.Screenshot
import java.io.IOException
import javax.inject.Inject

@InjectViewState
class RecordPresenter @Inject constructor(
    private val videoInteractor: VideoInteractor,
    private val preferences: PreferencesInteractor
) : MvpPresenter<ServiceView>() {

    private val mediaRecorder: MediaRecorder by lazy { MediaRecorder() }
    private var virtualDisplay: VirtualDisplay? = null
    private var imageReader: ImageReader? = null

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

    fun onStart() {
        running = false
    }

    fun onStop() {
        try {
            stopRecord()
        } catch (e: Throwable) {
            e.printStackTrace()
        }
    }

    fun sendScreenshot() {
        val bitmap = makeScreenshot()
        bitmap?.let {
            videoInteractor.sendScreenshot(
                Screenshot(
                    it,
                    width,
                    height,
                    "",
                    System.currentTimeMillis()
                )
            )
        }
    }

    fun measureScreen(width: Int, height: Int, dpi: Int) {
        this.width = width
        this.height = height
        this.dpi = dpi
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
                setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
            }
            setVideoSource(MediaRecorder.VideoSource.SURFACE)
            setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
            setOutputFile(videoPath)
            setVideoSize(width, height)
            setVideoEncoder(MediaRecorder.VideoEncoder.H264)
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

    private companion object {
        private const val MAX_IMAGES = 5
        private const val ENCODE_BIT_RATE = 5 * 1024 * 1024
        private const val FRAME_RATE = 30
    }
}
