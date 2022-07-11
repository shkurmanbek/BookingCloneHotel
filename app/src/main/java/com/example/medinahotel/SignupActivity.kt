package com.example.medinahotel

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import com.example.medinahotel.databinding.ActivitySignupBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class SignupActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignupBinding

    private lateinit var firebaseAuth: FirebaseAuth

    private lateinit var progressDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Please wait")
        progressDialog.setCanceledOnTouchOutside(false)

        binding.signupBtn.setOnClickListener {
            validateData()
        }

    }

    private var name = ""
    private var password = ""
    private var email = ""
    private fun validateData() {
        name = binding.nameEt.text.toString().trim()
        password = binding.passwordEt.text.toString().trim()
        email = binding.emailEt.text.toString().trim()
        val cPassword = binding.cpasswordEt.text.toString().trim()
        if(name.isEmpty()){
            Toast.makeText(this, "Enter your name", Toast.LENGTH_SHORT).show()
        } else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches())
            Toast.makeText(this, "Invalid Email...", Toast.LENGTH_SHORT).show()
        else if (password.isEmpty()){
            Toast.makeText(this, "Enter Password...", Toast.LENGTH_SHORT).show()
        }
        else if (cPassword.isEmpty()){
            Toast.makeText(this, "Confirm Password...", Toast.LENGTH_SHORT).show()
        }
        else if (password != cPassword){
            Toast.makeText(this, "Passwords Not matching...", Toast.LENGTH_SHORT).show()
        } else {
            createUserAccount()
        }
    }

    private fun createUserAccount() {
        progressDialog.setTitle("Please wait...")
        progressDialog.show()
        firebaseAuth.createUserWithEmailAndPassword(email,password)
            .addOnSuccessListener {
                updateUserInfo()
                Toast.makeText(this, "Created Successfully...", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                progressDialog.dismiss()
                Toast.makeText(this, "Failed to Create Account...", Toast.LENGTH_SHORT).show()

            }
    }

    private fun updateUserInfo() {
        progressDialog.setMessage("Saving user info...")

        val timestamp = System.currentTimeMillis()

        val uid = firebaseAuth.uid

        val hashMap: HashMap<String, Any?> = HashMap()

        hashMap["uid"] = uid
        hashMap["email"] = email
        hashMap["name"] = name
        hashMap["password"] = password
        hashMap["timestamp"] = timestamp
        hashMap["userType"] = "user"

        val ref = FirebaseDatabase.getInstance().getReference("Users")
        ref.child(uid!!)
            .setValue(hashMap)
            .addOnSuccessListener {
                progressDialog.dismiss()
                Toast.makeText(this, "Created Successfully...", Toast.LENGTH_SHORT).show()
                checkUser()
            }
            .addOnFailureListener { e->
                progressDialog.dismiss()
                Toast.makeText(this, "Failed to Create Account due to${e.message}...", Toast.LENGTH_SHORT).show()
            }
    }

    private fun checkUser() {
        progressDialog.setMessage("Checking wait...")
        val firebaseUser = firebaseAuth.currentUser!!

        val ref = FirebaseDatabase.getInstance().getReference("Users")
        ref.child(firebaseUser.uid)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    progressDialog.dismiss()
                    val userType = snapshot.child("userType").value
                    if (userType == "user"){
                        startActivity(Intent(this@SignupActivity, DashboardUserActivity::class.java))
                        finish()
                    }
                    else if (userType == "admin"){
                        val intent = Intent(this@SignupActivity,HotelActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                }
                override fun onCancelled(error: DatabaseError) {

                }
            })
    }
}