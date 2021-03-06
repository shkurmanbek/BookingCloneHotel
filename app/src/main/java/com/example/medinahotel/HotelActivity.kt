package com.example.medinahotel

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import com.example.medinahotel.databinding.ActivityHotelBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.lang.Exception


class HotelActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHotelBinding

    private lateinit var  categoryArrayList: ArrayList<ModelCategory>

    private lateinit var adapterCategory: AdapterCategory

    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHotelBinding.inflate(layoutInflater)

        setContentView(binding.root)
        loadCategories()
        firebaseAuth = FirebaseAuth.getInstance()

        binding.searchEt.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                //called as and when user type anything
                try {
                    adapterCategory.filter.filter(s)
                } catch(e: Exception){

                }
            }
            override fun afterTextChanged(s: Editable?) {

            }
        })

        binding.addCategoryBtn.setOnClickListener {
            startActivity(Intent(this, CategoryAddActivity::class.java))
        }
        binding.addClothesBtn.setOnClickListener {
            startActivity(Intent(this, HotelAddActivity::class.java))
        }


    }

    private fun loadCategories() {
        //init arraylist

        categoryArrayList = ArrayList()

        // get all categories from firebase... Firebase DB > Categories
        val ref = FirebaseDatabase.getInstance().getReference("Categories")
        ref.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                //clear list before starting adding data into it
                categoryArrayList.clear()
                for (ds in snapshot.children){
                    var model = ds.getValue(ModelCategory::class.java)
                    categoryArrayList.add(model!!)
                }
                //Setup adapter
                adapterCategory = AdapterCategory(this@HotelActivity, categoryArrayList)
                binding.categoriesRv.adapter = adapterCategory
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }
}