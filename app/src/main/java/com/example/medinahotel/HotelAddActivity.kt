package com.example.medinahotel

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import com.example.medinahotel.databinding.ActivityHotelAddBinding
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage

class HotelAddActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHotelAddBinding

    private lateinit var firebaseAuth: FirebaseAuth

    private lateinit var progressDialog: ProgressDialog

    private lateinit var categoryArrayList: ArrayList<ModelCategory>

    private var imageUri: Uri?= null

    private val TAG = "Photo_ADD_URI"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHotelAddBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()
        loadClothesCategories()

        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Please wait...")
        progressDialog.setCanceledOnTouchOutside(false)

        binding.categoryTv.setOnClickListener {
            categoryPickDialog()
        }

        binding.attachPicture.setOnClickListener {
            imagePickIntent()
        }

        binding.backBtn.setOnClickListener {
            onBackPressed()
        }

        binding.submitBtn.setOnClickListener {
            validateData()
        }

    }

    private fun loadClothesCategories() {
        Log.d(TAG, "LoadClothesCategories")
        categoryArrayList = ArrayList()
        val ref = FirebaseDatabase.getInstance().getReference("Categories")
        ref.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                categoryArrayList.clear()
                for(ds in snapshot.children){
                    val model = ds.getValue(ModelCategory::class.java)
                    categoryArrayList.add(model!!)
                    Log.d(TAG, "Ondata${model.category}")
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    private var selectedCategoryId = ""
    private var selectedCategoryTitle = ""

    private fun categoryPickDialog(){
        Log.d(TAG, "Showing category pick Dialog...")

        val categoriesArray = arrayOfNulls<String>(categoryArrayList.size)

        for (i in categoryArrayList.indices){
            categoriesArray[i] = categoryArrayList[i].category
        }

        val builder = AlertDialog.Builder(this)
        builder.setTitle("Pick Category")
            .setItems(categoriesArray){dialog, which->
                selectedCategoryTitle = categoryArrayList[which].category
                selectedCategoryId = categoryArrayList[which].id
                binding.categoryTv?.text = selectedCategoryTitle
                Log.d(TAG, "Picked Category : $selectedCategoryId" )
            }.show()
    }

    private fun imagePickIntent(){
        Log.d(TAG, "ImagePickIntent: starting image intent")

        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        imageActivityResultLauncher.launch(intent)
    }
    val imageActivityResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult(),
        ActivityResultCallback<ActivityResult> { result ->
            if (result.resultCode == RESULT_OK){
                Log.d(TAG, "Image Picked: ")
                imageUri = result.data!!.data
            }
            else{
                Log.d(TAG, "Image Picked cancelled")
                Toast.makeText(this, "cancelled", Toast.LENGTH_SHORT).show()
            }
        }
    )

    private var article = ""
    private var description = ""
    private var location = ""
    private var city = ""
    private var category = ""
    private var price = 0
    private fun validateData() {
        article =binding.articleEt.text.toString().trim()
        description =binding.descriptionEt.text.toString().trim()
        location =binding.sizeEt.text.toString().trim()
        city =binding.barcodeEt.text.toString().trim()
        category =binding.categoryTv.text.toString().trim()
        price =binding.priceEt.text.toString().toInt()
        if(article.isEmpty()){
            Toast.makeText(this, "Enter category...", Toast.LENGTH_SHORT).show()
        } else if (description.isEmpty()){
            Toast.makeText(this, "Enter category...", Toast.LENGTH_SHORT).show()
        } else if (category.isEmpty()){
            Toast.makeText(this, "Enter category...", Toast.LENGTH_SHORT).show()
        } else if(location.isEmpty()) {
            Toast.makeText(this, "Enter category...", Toast.LENGTH_SHORT).show()
        } else if (city.isEmpty()){
            Toast.makeText(this, "Enter category...", Toast.LENGTH_SHORT).show()
        } else if(imageUri==null) {
            Toast.makeText(this, "Enter category...", Toast.LENGTH_SHORT).show()
        } else if(price==0) {
            Toast.makeText(this, "Enter category...", Toast.LENGTH_SHORT).show()
        }
        else {
            uploadImageToStorage()
        }
    }

    private fun uploadImageToStorage() {
        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Uploading Image..")
        progressDialog.show()
        val timestamp = System.currentTimeMillis()

        val filePathAndName = "Images/$timestamp"

        val storageReference = FirebaseStorage.getInstance().getReference(filePathAndName)
        storageReference.putFile(imageUri!!)
            .addOnSuccessListener {taskSnapshot->
                val uriTask: Task<Uri> = taskSnapshot.storage.downloadUrl
                while(!uriTask.isSuccessful);
                val uploadedImageUrl = "${uriTask.result}"
                uploadImageInfoToDb(uploadedImageUrl, timestamp)
            }
            .addOnFailureListener { e->
                Log.d(TAG, "uploadTOPDf: failed to upload due tp ${e.message}")
                progressDialog.dismiss()
                Toast.makeText(this, "failed to upload due tp ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun uploadImageInfoToDb(uploadedImageUrl: String, timestamp: Long) {
        progressDialog.setMessage("Uploading image info")
        val uid = firebaseAuth.uid

        val hashMap: HashMap<String, Any> = HashMap()
        hashMap["uid"] = "$uid"
        hashMap["id"] = "$timestamp"
        hashMap["article"] = "$article"
        hashMap["description"] = "$description"
        hashMap["location"] = "$location"
        hashMap["city"] = "$city"
        hashMap["category"] = "$category"
        hashMap["categoryId"] = "$selectedCategoryId"
        hashMap["url"] = "$uploadedImageUrl"
        hashMap["timestamp"] = timestamp
        hashMap["price"] = price
        hashMap["downloadsCount"] = 0

        val ref = FirebaseDatabase.getInstance().getReference("Images")
        ref.child("$timestamp")
            .setValue(hashMap)
            .addOnSuccessListener {
                Log.d(TAG, "uploadTOPDf: success to upload")
                progressDialog.dismiss()
                Toast.makeText(this, "Uploaded PDF ", Toast.LENGTH_SHORT).show()
                imageUri=null
            }
            .addOnFailureListener { e->
                Log.d(TAG, "uploadTOPDf: failed to upload due tp ${e.message}")
                progressDialog.dismiss()
                Toast.makeText(this, "failed to upload due tp ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}