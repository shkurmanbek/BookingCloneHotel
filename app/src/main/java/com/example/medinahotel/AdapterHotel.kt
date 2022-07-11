package com.example.medinahotel

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.medinahotel.databinding.RowTypeBinding
import com.google.firebase.storage.FirebaseStorage
import java.io.File

class AdapterHotel :RecyclerView.Adapter<AdapterHotel.HolderClothes>{

    private var context: Context

    private var clothesArrayList: ArrayList<ModelHotel>

    private lateinit var binding: RowTypeBinding

    constructor(context: Context, clothesArrayList: ArrayList<ModelHotel>) : super() {
        this.context = context
        this.clothesArrayList = clothesArrayList
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderClothes {
        binding = RowTypeBinding.inflate(LayoutInflater.from(context), parent, false)
        return HolderClothes(binding.root)
    }

    override fun onBindViewHolder(holder: HolderClothes, position: Int) {
        val model = clothesArrayList[position]
        val clothesId = model.id
        val categoryId = model.categoryId
        val article = model.article
        val city = model.city
        val location = model.location
        val description = model.description
        val price = model.price
        val url = model.url
        val timestamp = model.timestamp
        val imageUrl = model.url
        val category = model.category
        //convert timestamp
//
        val formattedDate = MyApplication.formatTimeStamp(timestamp)
        holder.descriptionTv.text = description
        holder.city.text = city
        holder.locationTv.text = location
        holder.titleTv.text = article
        holder.dateTv.text = formattedDate
        holder.priceTv.text = price.toString()

        MyApplication.loadCategory(categoryId, holder.categoryTv)
        //set data
        holder.categoryTv.text = category

        val ref = FirebaseStorage.getInstance().reference.child("Images/$imageUrl")
        val localfile = File.createTempFile("tempImage", "jpeg")

        ref.getFile(localfile).addOnSuccessListener {
            val bitmap = BitmapFactory.decodeFile(localfile.absolutePath)
            holder.imageView.setImageBitmap(bitmap)
        }
            .addOnFailureListener{
                Log.d("TAG", "All is not okay")
            }
        Log.d("TAG", "All is okay")

        // handle click, delete
//        holder.deleteBtn.setOnClickListener {
//            //confirm before delete
//            val builder = AlertDialog.Builder(context)
//            builder.setTitle("Delete")
//                .setMessage("Are you sure want to delete this category?")
//                .setPositiveButton("Confirm"){a, d->
//                    Toast.makeText(context, "Deleting....", Toast.LENGTH_SHORT).show()
//                    deleteCategory(model, holder)
//                }
//                .setNegativeButton("Cancel"){a, d->
//                    a.dismiss()
//                }
//                .show()
//        }

//        //handle click, start image list admin activity
        holder.imageView.setOnClickListener{
            val intent = Intent(context, ReserveHotelActivity::class.java)
            intent.putExtra("categoryId", categoryId)
            intent.putExtra("category", category)
            intent.putExtra("name", article)
            intent.putExtra("price", price)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return clothesArrayList.size
    }

    inner class HolderClothes(itemView: View):RecyclerView.ViewHolder(itemView){
        val imageView = binding.imageView
        val titleTv = binding.titleTv
        val descriptionTv = binding.descriptionTv
        val city = binding.barcodeTv
        val categoryTv = binding.categoryTv
        val locationTv = binding.sizeTv
        val dateTv = binding.dateTv
        val priceTv = binding.priceTv
    }


}