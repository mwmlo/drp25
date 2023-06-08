package com.example.drp25

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.HorizontalScrollView
import android.widget.LinearLayout
import android.widget.RatingBar
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import com.example.drp25.ChatClient.client
import com.example.drp25.databinding.ActivityChatBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import io.getstream.chat.android.client.ChatClient
import io.getstream.chat.android.client.logger.ChatLogLevel
import io.getstream.chat.android.client.models.UserId
import kotlinx.coroutines.channels.Channel

class MatchActivity : AppCompatActivity() {
    private lateinit var parentLayout: LinearLayout
    private lateinit var context: Context
    private var selectedMatchId: String? = null

    private val observer = object : Observer {
        override fun notify(matchIds: Set<String>) {
            parentLayout.removeAllViews()
            for (matchId in matchIds) {
                val linearLayout = LinearLayout(context)
                linearLayout.orientation = LinearLayout.VERTICAL
                linearLayout.setPadding(30, 30, 30, 30)
                parentLayout.addView(linearLayout)
                val matchRef = FirebaseDatabase.getInstance().reference.child("universities")
                    .child(UNI_ID).child("users").child(matchId)
                matchRef.addListenerForSingleValueEvent(object: ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val name = snapshot.child("name").value
                        val nationality = snapshot.child("nationality").value
                        val course = snapshot.child("course").value
                        val year = snapshot.child("year").value

                        addText(linearLayout, "Name: $name")
                        addText(linearLayout, "Nationality: $nationality")
                        addText(linearLayout, "Course: $course")
                        addText(linearLayout, "Year: $year")

                        addText(linearLayout, "Interests:")
                        for (interest in snapshot.child("interests").children) {
                            addText(linearLayout, interest.key + " (" + interest.value + " stars)")

                            val ratingBar = RatingBar(context)
                            ratingBar.layoutParams = LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.WRAP_CONTENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT
                            )
                            ratingBar.numStars = 5
                            ratingBar.rating = interest.getValue(Float::class.java)!!
                            ratingBar.setIsIndicator(true)

                            linearLayout.addView(ratingBar)

                        }

                        val button = getButton(linearLayout, "Match with $name")
                        linearLayout.addView(button)
                        button.setOnClickListener {
                            selectedMatchId = matchId
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }

                })
            }
        }
    }

    private fun addText(linearLayout: LinearLayout, text: String) {
        val entry = TextView(context)
        entry.text = text
        entry.setPadding(16, 16, 16, 16)
        entry.textSize = 20f
        linearLayout.addView(entry)
    }

    private fun getButton(linearLayout: LinearLayout, text: String): Button {
        val btn = Button(context)
        btn.text = text
        btn.setPadding(16, 16, 16, 16)
        btn.textSize = 20f
        return btn
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_match)

        context = this
        parentLayout = findViewById(R.id.match_matches)
        addMatchObserver(observer)

        /* Functionality for SEND button -> takes user to chat page. */
        val sendBtn = findViewById<Button>(R.id.match_send_button)
        sendBtn.setOnClickListener { view ->
            val intent = Intent(this, ChatActivity::class.java)
            intent.putExtra("user", "Pierre")
            intent.putExtra("fromMatch", true)
            startActivity(intent)

            // Makes a new channel with a given person, say Pierre
            // Takes you to the chat of this person
            // aka ChannelActivity.newIntent(this, channel)

//            val binding = ActivityChatBinding.inflate(layoutInflater)
//            setContentView(binding.root)

         //   client = com.example.drp25.ChatClient.client

//            client = ChatClient.Builder("4tm42krd5mvf", applicationContext)
//                .withPlugin(offlinePluginFactory)
//                .logLevel(ChatLogLevel.ALL) // Set to NOTHING in prod
//                .build()

//binding.channelListView.
            // get the createDemo.... to return a channel
            //
            /*/* When a channel is clicked, the user is taken to the channel. */
            binding.channelListView.setChannelItemClickListener { channel ->
                startActivity(ChannelActivity.newIntent(this, channel))
            }
           */
            // make an intentval intent = Intent(ChannelActivity::class.java)
            // startActivity()
        }
    }

}