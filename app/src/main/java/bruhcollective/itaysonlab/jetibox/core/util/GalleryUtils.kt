package bruhcollective.itaysonlab.jetibox.core.util

import android.Manifest
import android.content.ContentValues
import android.content.Context
import android.media.MediaScannerConnection
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.text.format.Formatter
import androidx.annotation.FloatRange
import androidx.annotation.RequiresApi
import com.markodevcic.peko.Peko
import com.markodevcic.peko.PermissionResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import okhttp3.OkHttpClient
import okhttp3.Request
import okio.BufferedSink
import okio.buffer
import okio.sink
import java.io.File

object GalleryUtils {
    fun saveToGallery(
        ctx: Context,
        url: String,
        filename: String,
        video: Boolean
    ): Flow<SaveToGalleryState> = flow {
        if (Peko.requestPermissionsAsync(
                ctx,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) !is PermissionResult.Granted
        ) {
            emit(SaveToGalleryState.PermissionsRequired)
            return@flow
        }

        provideOutputSink(
            ctx.applicationContext,
            filename + "." + if (video) "mp4" else "png",
            video
        ) { sink ->
            OkHttpClient().newCall(Request.Builder().url(url).build()).execute().body?.use { body ->
                val bytesSize = body.contentLength().toFloat()
                val source = body.source()
                var totalBytesRead = 0L

                val formattedTotalSize = Formatter.formatFileSize(ctx, bytesSize.toLong())

                while (true) {
                    val readCount: Long = source.read(sink.buffer, 8 * 1024)
                    if (readCount == -1L) break
                    totalBytesRead += readCount
                    sink.emitCompleteSegments()
                    emit(SaveToGalleryState.Downloading(
                        progress = (totalBytesRead / bytesSize).coerceIn(0f..1f),
                        formattedDownloadedSize = Formatter.formatFileSize(ctx, totalBytesRead),
                        formattedTotalSize = formattedTotalSize,
                    ))
                }
            }
        }

        emit(SaveToGalleryState.Success)
    }.catch { e ->
        e.printStackTrace()
        emit(SaveToGalleryState.Error(e))
    }.flowOn(Dispatchers.IO).cancellable()

    private suspend fun provideOutputSink(
        ctx: Context,
        filename: String,
        video: Boolean,
        process: suspend (BufferedSink) -> Unit
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            saveModern(ctx, filename, video, process)
        } else {
            saveLegacy(ctx, filename, video, process)
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private suspend fun saveModern(
        ctx: Context,
        filename: String,
        video: Boolean,
        process: suspend (BufferedSink) -> Unit
    ) {
        val imageUri = ctx.contentResolver.insert(
            if (video) MediaStore.Video.Media.EXTERNAL_CONTENT_URI else MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
                put(MediaStore.MediaColumns.MIME_TYPE, if (video) "video/mp4" else "image/png")
                put(
                    MediaStore.MediaColumns.RELATIVE_PATH,
                    if (video) Environment.DIRECTORY_MOVIES else Environment.DIRECTORY_PICTURES
                )
                put(MediaStore.MediaColumns.IS_PENDING, 1)
            })!!

        ctx.contentResolver.openOutputStream(imageUri)?.sink()?.buffer()?.use { sink ->
            process(sink)
        }

        ctx.contentResolver.update(imageUri, ContentValues().apply {
            put(MediaStore.MediaColumns.IS_PENDING, 0)
        }, null, null)
    }

    private suspend fun saveLegacy(
        ctx: Context,
        filename: String,
        video: Boolean,
        process: suspend (BufferedSink) -> Unit
    ) {
        val file = File(
            Environment.getExternalStoragePublicDirectory(if (video) Environment.DIRECTORY_MOVIES else Environment.DIRECTORY_PICTURES),
            filename
        )
        file.sink().buffer().use { sink -> process(sink) }
        MediaScannerConnection.scanFile(ctx, arrayOf(file.absolutePath), null, null)
    }

    sealed class SaveToGalleryState {
        class Downloading(
            @FloatRange(from = 0.0, to = 1.0) val progress: Float,
            val formattedTotalSize: String,
            val formattedDownloadedSize: String
        ) : SaveToGalleryState()

        class Error(val exception: Throwable) : SaveToGalleryState()

        object PermissionsRequired : SaveToGalleryState()
        object Success : SaveToGalleryState()
    }
}