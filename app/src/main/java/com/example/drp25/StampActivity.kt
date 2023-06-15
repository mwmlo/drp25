package com.example.drp25

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class StampActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stamp)

        val selectedMatchId:String? = intent.getStringExtra("selectedMatchId")
        val selectedMatchName:String? = intent.getStringExtra("selectedMatchName")

        /* Functionality for SEND -> takes user to chat page. */
        fun send(selectedStamp: String) {
            val intent = Intent(this, ChatActivity::class.java)
            intent.putExtra("fromMatch", true)
            intent.putExtra("matchedName", selectedMatchName)

            // send the stamp
            if (selectedMatchId != null) {
                sendStamp(UNI_ID, selectedMatchId, selectedStamp)
            }

            Toast.makeText(this, "Stamp sent!", Toast.LENGTH_SHORT).show()

            startActivity(intent)
        }

        // Set up stamps
        val stamp1 = findViewById<ImageView>(R.id.stamp_option_1)
        val stamp2 = findViewById<ImageView>(R.id.stamp_option_2)
        val stamp3 = findViewById<ImageView>(R.id.stamp_option_3)

        val nationalityRef = FirebaseDatabase.getInstance().reference.child("universities")
            .child(UNI_ID).child("users").child(USER_ID).child("nationality")

        nationalityRef.addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val nationality = snapshot.value as String
                var img1: String? = null
                var img2: String? = null
                var img3: String? = null

                if (nationality == "Chinese") {
                    img1 = "china_flag_stamp"
                    img2 = "china_tourist_stamp"
                    img3 = "china_food_stamp"
                } else {
                    img1 = "britain_flag_stamp"
                    img2 = "britain_tourist_stamp"
                    img3 = "britain_food_stamp"
                }

                stamp1.setImageResource(resources.getIdentifier(img1, "drawable", packageName))
                stamp2.setImageResource(resources.getIdentifier(img2, "drawable", packageName))
                stamp3.setImageResource(resources.getIdentifier(img3, "drawable", packageName))

                stamp1.setOnClickListener{
                    send(img1)
                }
                stamp2.setOnClickListener{
                    send(img2)
                }
                stamp3.setOnClickListener{
                    send(img3)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }
}