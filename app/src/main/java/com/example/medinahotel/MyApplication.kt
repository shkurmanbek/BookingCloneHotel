package com.example.medinahotel

import android.app.Application
import android.graphics.BitmapFactory
import android.media.Image
import android.text.format.DateFormat
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storageMetadata
import java.io.File
import java.util.*

class MyApplication :Application() {
    override fun onCreate() {
        super.onCreate()
    }

    companion object {
        fun formatTimeStamp(timestamp:Long): String {
            val cal = Calendar.getInstance(Locale.ENGLISH)
            cal.timeInMillis = timestamp

            return DateFormat.format("dd/MM/yyyy", cal).toString()
        }
        fun loadBookSize(pdfUrl:String, bookTitle:String, sizeTv:TextView){
            val TAG = "PDF_SIZE_TAG"
            val ref = FirebaseStorage.getInstance().getReferenceFromUrl(pdfUrl)
            ref.metadata
                .addOnSuccessListener { storageMetadata ->
                    val bytes = storageMetadata.sizeBytes.toDouble()
                    val kb = bytes/1024
                    val mb = kb/1024
                    if(mb>1){
                        sizeTv.text = "${String.format("%.2f", mb)}MB"
                    }
                    else if(kb>=1){
                        sizeTv.text = "${String.format("%.2f", kb)}KB"
                    } else{
                        sizeTv.text = "${String.format("%.2f", bytes)} bytes"
                    }

                }
                .addOnFailureListener { e->

                }
        }

        fun loadImageFromUrlSinglePage(
            imageId: String,
            imageView: ImageView
        ) {
            val TAG = "PDF_Thumb_TAG"
            val ref = FirebaseStorage.getInstance().reference.child("Images/$imageId")
            val localfile = File.createTempFile("tempImage", "jpeg")

            ref.getFile(localfile).addOnSuccessListener {
                val bitmap = BitmapFactory.decodeFile(localfile.absolutePath)
                imageView.setImageBitmap(bitmap)
            }
                .addOnFailureListener{
                    Log.d("TAG", "All is not okay")
                }
            Log.d("TAG", "All is okay")
        }

        fun loadCategory(categoryId: String, categoryTv: TextView){
            val ref = FirebaseDatabase.getInstance().getReference("Categories")
            ref.child(categoryId)
                .addListenerForSingleValueEvent(object: ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val category:String = "${snapshot.child("category").value}"
                        categoryTv.text = category
                    }

                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }
                })
        }
    }


}

