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
    private var selectedStampId: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stamp)

        val selectedMatchId:String? = intent.getStringExtra("selectedMatchId")
        val selectedMatchName:String? = intent.getStringExtra("selectedMatchName")

        /* Functionality for SEND button -> takes user to chat page. */
        val sendBtn = findViewById<Button>(R.id.match_send_button)
        sendBtn.setOnClickListener { view ->
            val intent = Intent(this, ChatActivity::class.java)
            intent.putExtra("fromMatch", true)
            intent.putExtra("matchedName", selectedMatchName)

            // send the stamp
            if (selectedMatchId != null && selectedStampId != null) {
                sendStamp(UNI_ID, selectedMatchId!!, selectedStampId!!)
            }

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
                var img1: Int? = null
                var img2: Int? = null
                var img3: Int? = null

                if (nationality == "Chinese") {
                    img1 = R.drawable.china_flag_stamp
                    img2 = R.drawable.china_tourist_stamp
                    img3 = R.drawable.china_food_stamp
                } else {
                    img1 = R.drawable.britain_flag_stamp
                    img2 = R.drawable.britain_tourist_stamp
                    img3 = R.drawable.britain_food_stamp
                }

                stamp1.setImageResource(img1)
                stamp2.setImageResource(img2)
                stamp3.setImageResource(img3)

                stamp1.setOnClickListener{
                    selectedStampId = img1
                }
                stamp2.setOnClickListener{
                    selectedStampId = img2
                }
                stamp3.setOnClickListener{
                    selectedStampId = img3
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }
}