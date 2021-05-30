package com.ptit.theeyes.utils

import android.app.Activity
import android.content.ContentResolver
import android.graphics.Bitmap
import android.net.Uri
import android.provider.OpenableColumns
import java.io.File

fun File.writeBitmap(bitmap: Bitmap, format: Bitmap.CompressFormat, quality: Int){
    outputStream().use { out ->
        bitmap.compress(format, quality, out)
        out.flush()
    }
}

fun Uri.getFileName(contentResolver: ContentResolver): String? {
    return contentResolver.query(this, null, null, null, null)?.use { cursor ->
        if (!cursor.moveToFirst()) return@use null

        val name = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        cursor.getString(name)
    }
}

fun Activity.getMediaDirectory(folderName: String): File{
    val mediaDir = externalMediaDirs.firstOrNull()?.let {
        File(it, folderName).apply {
            mkdirs()
        }
    }
    return if(mediaDir != null && mediaDir.exists())
        mediaDir else filesDir
}
    