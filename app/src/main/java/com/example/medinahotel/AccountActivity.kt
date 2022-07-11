package com.example.medinahotel

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import com.example.medinahotel.databinding.ActivityAccountBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class AccountActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAccountBinding

    private var cateogryId = ""
    private var category = ""
    private var hotelName = ""
    private var name = ""
    private var location = ""
    private var city = ""
    private var price = 0
    private var days = 0

    private lateinit var firebaseAuth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAccountBinding.inflate(layoutInflater)
        setContentView(binding.root)
        firebaseAuth = FirebaseAuth.getInstance()
        binding.backBtn.setOnClickListener {
            onBackPressed()
        }
        initReserve()

    }

    private fun initReserve() {
        val userId = firebaseAuth.currentUser!!.uid

        val ref = FirebaseDatabase.getInstance().getReference("Reserved")

        ref.child(userId).addValueEventListener( object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                    binding.hotelNameTv.text = snapshot.child("hotelName").getValue(String::class.java).toString()
                    binding.nameTv.text = snapshot.child("name").getValue(String::class.java).toString()
                    binding.cityTv.text = "City: " + snapshot.child("city").getValue(String::class.java).toString()
                    binding.locationTv.text = "Location: " + snapshot.child("location").getValue(String::class.java).toString()
                    binding.categoryTv.text = snapshot.child("category").getValue(String::class.java).toString()
                    binding.priceTv.text = "Price: " + snapshot.child("price").getValue(Int::class.java).toString()
                    binding.msg.text = "Days: " + snapshot.child("days").getValue(Int::class.java).toString()
            }
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

}