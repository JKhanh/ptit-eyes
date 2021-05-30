package com.ptit.theeyes.viewModel

import android.content.ContentResolver
import android.graphics.Bitmap
import android.provider.MediaStore
import androidx.core.net.toUri
import com.google.android.gms.tasks.Task
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.functions.ktx.functions
import com.google.firebase.ktx.Firebase
import com.google.gson.*
import com.ptit.theeyes.utils.toBase64
import timber.log.Timber

class DetectViewModel: BaseViewModel() {
    lateinit var bitmap: Bitmap
    private val functions: FirebaseFunctions = Firebase.functions

    fun setOriginImage(imageUri: String, contentResolver: ContentResolver){
        bitmap = MediaStore.Images.Media.getBitmap(contentResolver, imageUri.toUri())
    }

    fun scaleBitmapDown(maxDimension: Int): Bitmap {
        val originalWidth = bitmap.width
        val originalHeight = bitmap.height
        var resizedWidth = maxDimension
        var resizedHeight = maxDimension
        if (originalHeight > originalWidth) {
            resizedHeight = maxDimension
            resizedWidth =
                (resizedHeight * originalWidth.toFloat() / originalHeight.toFloat()).toInt()
        } else if (originalWidth > originalHeight) {
            resizedWidth = maxDimension
            resizedHeight =
                (resizedWidth * originalHeight.toFloat() / originalWidth.toFloat()).toInt()
        } else if (originalHeight == originalWidth) {
            resizedHeight = maxDimension
            resizedWidth = maxDimension
        }
        return Bitmap.createScaledBitmap(bitmap, resizedWidth, resizedHeight, false)
    }

    fun annotateImage(request: String): Task<JsonElement>{
        return functions
            .getHttpsCallable("annotateImage")
            .call(request)
            .continueWith { task ->
                val result = task.result?.data
                Timber.d(Gson().toJson(result))
                JsonParser.parseString(Gson().toJson(result))
            }
    }

    fun createRequest(): String{
        val request = JsonObject()
        val image = JsonObject()
        image.add("content", JsonPrimitive(scaleBitmapDown(640).toBase64()))
        request.add("image", image)

        val feature = JsonObject()
        feature.add("maxResults", JsonPrimitive(5))
        feature.add("type", JsonPrimitive("LABEL_DETECTION"))
        val features = JsonArray()
        features.add(feature)
        request.add("features", features)
        return request.toString()
    }
}