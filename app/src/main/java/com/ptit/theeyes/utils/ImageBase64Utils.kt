package com.ptit.theeyes.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import java.io.ByteArrayOutputStream

fun Bitmap.toBase64(): String{
    val baos = ByteArrayOutputStream()
    this.compress(Bitmap.CompressFormat.JPEG, 100, baos)
    val byteArray = baos.toByteArray()
    return Base64.encodeToString(byteArray, Base64.DEFAULT)
}

fun String.base64ToImage(): Bitmap{
    val decodedString = Base64.decode(this, Base64.DEFAULT)
    return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
}