package com.ptit.theeyes.viewModel

import android.content.ContentResolver
import android.content.Context
import android.graphics.Bitmap
import android.provider.MediaStore
import androidx.core.net.toUri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.functions.ktx.functions
import com.google.firebase.ktx.Firebase
import com.google.gson.*
import com.ptit.theeyes.model.AppDatabase
import com.ptit.theeyes.model.DetectModel
import com.ptit.theeyes.utils.AppDispatchers
import com.ptit.theeyes.utils.toBase64
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber

class DetectViewModel(
    private val dispatchers: AppDispatchers,
    context: Context
): BaseViewModel() {
    lateinit var bitmap: Bitmap
    private val functions: FirebaseFunctions = Firebase.functions
    private val user = FirebaseAuth.getInstance().currentUser
    private val database = Firebase.database("https://the-eyes-b8744-default-rtdb.asia-southeast1.firebasedatabase.app/")
        .reference
        .child(user!!.uid)

    private val _historyList = MediatorLiveData<List<DetectModel>>()
    val historyList: LiveData<List<DetectModel>> get() = _historyList
    private var historySource: LiveData<List<DetectModel>> = MutableLiveData()

    private val local = AppDatabase.buildDatabase(context)

    fun setOriginImage(imageUri: String, contentResolver: ContentResolver){
        bitmap = MediaStore.Images.Media.getBitmap(contentResolver, imageUri.toUri())
    }

    private fun scaleBitmapDown(maxDimension: Int): Bitmap {
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

    fun writeData(detectModel: DetectModel){
        database.child(randomString()).setValue(detectModel)
        viewModelScope.launch(dispatchers.io){
            local.modelDao().saveModel(detectModel)
        }
    }

    private fun randomString(): String{
        val allowedChars = ('A'..'Z') + ('a'..'z') + ('0'..'9')
        return (1..10)
            .map { allowedChars.random() }
            .joinToString("")
    }

    fun getHistory(){
        val modelListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val history = snapshot.getValue(DetectModel::class.java)
                history?.let {
                    Timber.d(it.toString())
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Timber.e(error.details)
            }
        }
        database.addValueEventListener(modelListener)
    }

    fun getHistoryOnce(){
        viewModelScope.launch(dispatchers.main) {
            _historyList.removeSource(historyList)
            withContext(dispatchers.io){
                historySource = local.modelDao().getAllModel()
            }
            _historyList.addSource(historySource){
                _historyList.value = it
            }
        }
    }
}