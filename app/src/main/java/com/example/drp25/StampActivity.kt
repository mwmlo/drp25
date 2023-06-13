package com.example.drp25

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class StampActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stamp)

        val selectedMatchName:String? = intent.getStringExtra("selectedMatchName")

        /* Functionality for SEND button -> takes user to chat page. */
        val sendBtn = findViewById<Button>(R.id.match_send_button)
        sendBtn.setOnClickListener { view ->
            val intent = Intent(this, ChatActivity::class.java)
            intent.putExtra("fromMatch", true)
            intent.putExtra("matchedName", selectedMatchName)
            startActivity(intent)
        }

        // Set up stamps
        val stamp1 = findViewById<ImageView>(R.id.stamp_option_1)
        val stamp2 = findViewById<ImageView>(R.id.stamp_option_2)
        val stamp3 = findViewById<ImageView>(R.id.stamp_option_3)

        val nationalityRef = FirebaseDatabase.getInstance().reference.child("universities")
            .child(UNI_ID).child("users").child(USER_ID).child("nationality")

        nationalityRef.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val nationality = snapshot.value as String
                if (nationality == "Chinese") {
                    stamp1.setImageResource(R.drawable.china_flag_stamp)
                    stamp2.setImageResource(R.drawable.china_tourist_stamp)
                    stamp3.setImageResource(R.drawable.china_food_stamp)
                } else if (nationality == "British") {
                    stamp1.setImageResource(R.drawable.britain_flag_stamp)
                    stamp2.setImageResource(R.drawable.britain_tourist_stamp)
                    stamp3.setImageResource(R.drawable.britain_food_stamp)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }
}