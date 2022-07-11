package com.example.medinahotel

import android.R
import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.View
import android.widget.*
import com.example.medinahotel.databinding.ActivityReserveHotelBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.*

class ReserveHotelActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {
    private var cateogryId = ""
    private var category = ""
    private var hotelName = ""
    private var name = ""
    private var location = ""
    private var city = ""
    var list_of_items = arrayOf("1 Days", "2 Days", "3 Days", "4 Days", "5 Days", "6 Days", "7 Days",
        "8 Days", "9 Days", "10 Days", "11 Days", "12 Days", "13 Days", "14 Days", "15 Days", "16 Days",
        "17 Days","18 Days","19 Days","20 Days")
    var spinner: Spinner? = null
    var textView_msg: TextView? = null
    var price1: Int = 0
    var typeCompany: Int = 0
    var month1: Int = 0
    private lateinit var binding: ActivityReserveHotelBinding

    private lateinit var progressDialog: ProgressDialog

    private lateinit var firebaseAuth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityReserveHotelBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()
        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Please wait")
        progressDialog.setCanceledOnTouchOutside(false)

        val intent = intent
        cateogryId = intent.getStringExtra("categoryId")!!
        category = "Category: " + intent.getStringExtra("category")!!
        hotelName = "Hotel name: "+intent.getStringExtra("name")!!

        binding.subTitleTv.text = category

        val editable = Editable.Factory.getInstance().newEditable(hotelName)
        val editable1 = Editable.Factory.getInstance().newEditable(category)
        binding.hotelNameEt.text = editable
        binding.categoryEt.text = editable1

        binding.backBtn.setOnClickListener {
            onBackPressed()
        }

        initUser()
        initHotel()
        price1 = intent.getIntExtra("price",0)

        textView_msg = binding.msg
        spinner = binding.planetsSpinner
        spinner!!.setOnItemSelectedListener(this)

        // Create an ArrayAdapter using a simple spinner layout and languages array
        val aa = ArrayAdapter(this, R.layout.simple_spinner_item, list_of_items)
        // Set layout to use when the list of choices appear
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        // Set Adapter to Spinner
        spinner!!.setAdapter(aa)


        binding.signupBtn.setOnClickListener {
            updateHotelInfo()
        }
    }

    private fun initHotel() {
        val ref = FirebaseDatabase.getInstance().getReference("Images")
        ref.addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                for (ds in snapshot.children){
                    var model = ds.getValue(ModelHotel::class.java)
                    if(model!=null) {
                        location = model.location
                        city = model.city
                        val editable = Editable.Factory.getInstance().newEditable("City: " + model.city)
                        val editable1 = Editable.Factory.getInstance().newEditable("Location: " + model.location)
                        binding.cityEt.text = editable
                        binding.locationEt.text = editable1
                    }
                }
            }
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

    private fun initUser() {
        val userId = firebaseAuth.currentUser!!.uid
        val userRef = FirebaseDatabase.getInstance().getReference("Users")
        val ordersRef = userRef.child(userId)

        val valueEventListener = object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                name = snapshot.child("name").getValue(String::class.java).toString()
                val editable = Editable.Factory.getInstance().newEditable(name)
                binding.nameEt.text = editable
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        }
        ordersRef.addValueEventListener(valueEventListener)
    }

    override fun onItemSelected(arg0: AdapterView<*>, arg1: View, position: Int, id: Long) {
        textView_msg!!.text = "Days : "+list_of_items[position]
        var editable :Editable?= null
        var res = 0
        for(i in 0..19){
            if(position==i){
                res = price1 * (i+1)
                editable = Editable.Factory.getInstance().newEditable("Price: $res")
                month1 = i
            }
        }
        binding.priceEt.text = editable
    }

    override fun onNothingSelected(arg0: AdapterView<*>) {

    }

    private fun updateHotelInfo() {
        progressDialog.setMessage("Saving user info...")

        val timestamp = System.currentTimeMillis()

        val uid = firebaseAuth.uid

        val hashMap: HashMap<String, Any?> = HashMap()

        hashMap["uid"] = uid
        hashMap["hotelName"] = hotelName
        hashMap["name"] = name
        hashMap["city"] = city
        hashMap["location"] = location
        hashMap["category"] = category
        hashMap["price"] = price1
        hashMap["days"] = month1+1

        val ref = FirebaseDatabase.getInstance().getReference("Reserved")
        ref.child(uid!!)
            .setValue(hashMap)
            .addOnSuccessListener {
                progressDialog.dismiss()
                Toast.makeText(this, "Created Successfully...", Toast.LENGTH_SHORT).show()
                val intent = Intent(this@ReserveHotelActivity, ImageListActivity::class.java)
                intent.putExtra("category",category)
                startActivity(intent)
            }
            .addOnFailureListener { e->
                progressDialog.dismiss()
                Toast.makeText(this, "Failed to Create Account due to${e.message}...", Toast.LENGTH_SHORT).show()
            }
    }
}