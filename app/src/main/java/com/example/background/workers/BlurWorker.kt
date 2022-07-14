package com.example.background.workers

import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import android.text.TextUtils
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.example.background.KEY_IMAGE_URI
import com.example.background.R
import java.lang.Exception


private const val TAG = "BlurWorker"

class BlurWorker(ctx: Context,params: WorkerParameters):Worker(ctx, params) {
    override fun doWork(): Result {
        val appCtx = applicationContext
        val resourceUri = inputData.getString(KEY_IMAGE_URI)
        makeStatusNotification("Blurring Image", appCtx);
        sleep()

        return try {
//            val picture = BitmapFactory.decodeResource(
//                appCtx.resources,
//                R.drawable.android_cupcake);

            if (TextUtils.isEmpty(resourceUri)) {
                Log.e(TAG, "Invalid input uri")
                throw IllegalArgumentException("Invalid input uri")
            }

            val resolver = appCtx.contentResolver

            val picture = BitmapFactory.decodeStream(
                resolver.openInputStream(Uri.parse(resourceUri)))

            val output = blurBitmap(picture, appCtx)

            // Write bitmap to a temp file
            val outputUri = writeBitmapToFile(appCtx, output)
            val outputData = workDataOf(KEY_IMAGE_URI to outputUri.toString())

            Result.success(outputData)
        }catch (e:Exception) {
            Log.e(TAG, "Error applying blur")
            Result.failure()
        }
    }
}