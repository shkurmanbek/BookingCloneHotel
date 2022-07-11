package com.example.medinahotel

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.example.medinahotel.databinding.RowHotelBinding
import com.google.firebase.database.FirebaseDatabase

class AdapterCategory :RecyclerView.Adapter<AdapterCategory.HolderCategory>, Filterable{

    private val context: Context

    public var categoryArrayList: ArrayList<ModelCategory>
    private var filterList:ArrayList<ModelCategory>

    private var filter: FilterCategory? = null

    private lateinit var binding: RowHotelBinding

    constructor(context: Context, categoryArrayList: ArrayList<ModelCategory>) {
        this.context = context
        this.categoryArrayList = categoryArrayList
        this.filterList = categoryArrayList
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderCategory {
        //inflate/bind row_category.xml
        binding = RowHotelBinding.inflate(LayoutInflater.from(context), parent, false)
        return HolderCategory(binding.root)
    }

    override fun onBindViewHolder(holder: HolderCategory, position: Int) {
        /*-------- GET DATA ,SET DATA, HANDLE CLICKS etc ---------*/
        val model = categoryArrayList[position]
        val id = model.id
        val category = model.category
        val uid = model.uid
        val timestamp = model.timestamp

        //set data
        holder.categoryTv.text = category

        // handle click, delete
        holder.deleteBtn.setOnClickListener {
            //confirm before delete
            val builder = AlertDialog.Builder(context)
            builder.setTitle("Delete")
                .setMessage("Are you sure want to delete this category?")
                .setPositiveButton("Confirm"){a, d->
                    Toast.makeText(context, "Deleting....", Toast.LENGTH_SHORT).show()
                    deleteCategory(model, holder)
                }
                .setNegativeButton("Cancel"){a, d->
                    a.dismiss()
                }
                .show()
        }
        //handle click, start image list admin activity
        holder.itemView.setOnClickListener{
            val intent = Intent(context, ImageListActivity::class.java)
            intent.putExtra("categoryId", id)
            intent.putExtra("category", category)
            context.startActivity(intent)
        }
    }

    private fun deleteCategory(model: ModelCategory, holder: HolderCategory) {
        //get id of category to delete

        val id = model.id
        //Firebase DB -> Categories -> categoryId
        val ref = FirebaseDatabase.getInstance().getReference("Categories")
        ref.child(id)
            .removeValue()
            .addOnSuccessListener {
                Toast.makeText(context, "Deleted....", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(context, "Unable to delete due to ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    override fun getItemCount(): Int {
        return categoryArrayList.size // number of items in list
    }

    inner class HolderCategory(itemView: View): RecyclerView.ViewHolder(itemView){
        var categoryTv: TextView = binding.categoryTv
        var deleteBtn: ImageButton = binding.deleteBtn
    }

    override fun getFilter(): Filter {
        if (filter== null){
            filter = FilterCategory(filterList, this)

        }
        return filter as FilterCategory
    }
}