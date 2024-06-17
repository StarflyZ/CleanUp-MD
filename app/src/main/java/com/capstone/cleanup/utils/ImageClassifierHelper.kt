package com.capstone.cleanup.utils

import android.content.ContentResolver
import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import com.capstone.cleanup.R
//import com.google.android.gms.tflite.client.TfLiteInitializationOptions
//import com.google.android.gms.tflite.gpu.support.TfLiteGpu
import org.tensorflow.lite.DataType
//import org.tensorflow.lite.gpu.CompatibilityList
import org.tensorflow.lite.support.common.ops.CastOp
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.image.ops.ResizeOp
import org.tensorflow.lite.task.core.BaseOptions
//import org.tensorflow.lite.task.gms.vision.TfLiteVision
import org.tensorflow.lite.task.vision.classifier.Classifications
import org.tensorflow.lite.task.vision.classifier.ImageClassifier

// Use commented code if using play-service for TFLite Vision
class ImageClassifierHelper(
    private var threshold: Float = 0.7f,
    private val modelName: String = "coba_metadata.tflite",
    val context: Context,
    val classifierListener: ClassifierListener?
) {
    private var imageClassifier: ImageClassifier? = null

    init {
//        TfLiteGpu.isGpuDelegateAvailable(context).onSuccessTask { gpuAvailable ->
//            val optionBuilder = TfLiteInitializationOptions.builder()
//            if (gpuAvailable) {
//                optionBuilder.setEnableGpuDelegateSupport(true)
//            }
//            TfLiteVision.initialize(context, optionBuilder.build())
//        }.addOnSuccessListener {
//            setupImageClassifier()
//        }.addOnFailureListener {
//            classifierListener?.onError(context.getString(R.string.tflitevision_is_not_initialized_yet))
//        }

        setupImageClassifier()
    }

    private fun setupImageClassifier() {
        val optionBuilder = ImageClassifier.ImageClassifierOptions.builder()
            .setScoreThreshold(threshold)
        val baseOptionBuilder = BaseOptions.builder()
            .setNumThreads(4)
//        if (CompatibilityList().isDelegateSupportedOnThisDevice) {
//            baseOptionBuilder.useGpu()
//        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
//            baseOptionBuilder.useNnapi()
//        } else {
//            // Menggunakan CPU
//            baseOptionBuilder.setNumThreads(4)
//        }
        optionBuilder.setBaseOptions(baseOptionBuilder.build())

        try {
            imageClassifier = ImageClassifier.createFromFileAndOptions(
                context,
                modelName,
                optionBuilder.build()
            )
        } catch (e: IllegalStateException) {
            classifierListener?.onError(context.getString(R.string.image_classifier_failed))
            Log.e(TAG,  e.message.toString())
        }
    }

    fun classifyStaticImage(imageUri: Uri) {
//        if (!TfLiteVision.isInitialized()) {
//            val errorMessage = context.getString(R.string.tflitevision_is_not_initialized_yet)
//            Log.e(TAG, errorMessage)
//            classifierListener?.onError(errorMessage)
//            return
//        }

        if (imageClassifier == null) {
            setupImageClassifier()
        }

        val imageProcessor = ImageProcessor.Builder()
            .add(ResizeOp(150, 150, ResizeOp.ResizeMethod.NEAREST_NEIGHBOR))
            .add(CastOp(DataType.FLOAT32))
            .build()

        var tensorImage: TensorImage? = null
        val contentResolver: ContentResolver = context.contentResolver
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val source = ImageDecoder.createSource(contentResolver, imageUri)
            ImageDecoder.decodeBitmap(source)
        } else {
            MediaStore.Images.Media.getBitmap(contentResolver, imageUri)
        }.copy(Bitmap.Config.ARGB_8888, true)?.let { bitmap ->
            tensorImage = imageProcessor.process(TensorImage.fromBitmap(bitmap))
        }

        val results = imageClassifier?.classify(tensorImage)
        classifierListener?.onResult(
            results
        )
    }

    interface ClassifierListener {
        fun onError(error: String)
        fun onResult(result: List<Classifications>?)
    }

    companion object {
        private const val TAG = "ImageClassifierHelper"
    }
}