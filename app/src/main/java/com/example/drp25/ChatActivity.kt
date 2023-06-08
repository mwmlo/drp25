package com.example.drp25

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity

import com.example.drp25.databinding.ActivityChatBinding
import com.google.firebase.database.FirebaseDatabase
//import com.google.firebase.database.FirebaseDatabase
import io.getstream.chat.android.client.api.models.querysort.QuerySortByField
import io.getstream.chat.android.client.call.enqueue
import io.getstream.chat.android.client.models.Filters
import io.getstream.chat.android.client.models.User
import io.getstream.chat.android.ui.channel.list.header.viewmodel.ChannelListHeaderViewModel
import io.getstream.chat.android.ui.channel.list.header.viewmodel.bindView
import io.getstream.chat.android.ui.channel.list.viewmodel.ChannelListViewModel
import io.getstream.chat.android.ui.channel.list.viewmodel.bindView
import io.getstream.chat.android.ui.channel.list.viewmodel.factory.ChannelListViewModelFactory

// our logged in user
const val UNI_ID = "imperialId"
const val USER_ID = "-NXPnWryIGR2S5aJmSGH"

class ChatActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChatBinding
//    private var channelGlob: Call<Channel>

    // Get match for demo
    private val names = listOf("Pierre", "Kevin", "Martha", "India", "Jerry", "Simon")
    private var i = 0

    private fun getMatch(): String {
        val match = names[i]
        i = (i + 1) % (names.size)
        return match
    }

    val matchesRef = FirebaseDatabase.getInstance().reference.child("matches")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val clientService = ChatClientService(applicationContext)
        Backend.serverInit()

        // Step 0 - inflate binding
        binding = ActivityChatBinding.inflate(layoutInflater)

        // Step 3 - Authenticate and connect the user (MVP)
        val user = User(
            id = "demo-user",
            name = "Name",
            image = "https://bit.ly/2TIt8NR"
        )
        val friend1 = User(
            id = "friend-1",
            name = "Friend 1",
            image = "https://bit.ly/2TIt8NR"
        )
        val friend2 = User(
            id = "friend-2",
            name = "Friend 2",
            image = "https://bit.ly/2TIt8NR"
        )

        clientService.connectCurrentUser(user).enqueue { result ->
            if (result.isSuccess) {
                Log.e("connectUser", "success")
                Backend.createFriend(friend1.id, friend1.name)
                Backend.createFriend(friend2.id, friend2.name)
                clientService.createChannel("14355", user.id, friend1.id)
            } else {
                Log.e("connectUser", result.toString())
            }
        }

        // Only allows the binding to be visible if the page is not opened from a match activity
        if (intent.hasExtra("fromMatch") && intent.getBooleanExtra("fromMatch", true)) {
            clientService.createChannel("1234", user.id, friend2.id)
//            createAndRunChannel(client, user)
        } else {
            setContentView(binding.root)
        }

        val channelListHeaderViewModel: ChannelListHeaderViewModel by viewModels()
        val channelListFactory = ChannelListViewModelFactory(
            filter = Filters.and(
                Filters.eq("type", "messaging"),
                Filters.`in`("members", listOf(user.id)),
            ),
            sort = QuerySortByField.descByName("lastUpdated"),
            limit = 30,
        )
        val channelListViewModel: ChannelListViewModel by viewModels { channelListFactory }

        channelListHeaderViewModel.bindView(binding.channelListHeaderView, this)
        channelListViewModel.bindView(binding.channelListView, this)

        /* When the user avatar is clicked, the user is taken to their profile page. */
        binding.channelListHeaderView.setOnUserAvatarClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
        }

        /* When a channel is clicked, the user is taken to the channel. */
        binding.channelListView.setChannelItemClickListener { channel ->
            startActivity(ChannelActivity.newIntent(this, channel))
        }

        /* Functionality of "Meet Someone New" button -> takes user to match page. */
        binding.homeMatchButton.setOnClickListener {
            val intent = Intent(this, MatchActivity::class.java)
            startActivity(intent)
        }

        // indicates this is the person logged in (currently Kevin)
        listenToUser(UNI_ID, USER_ID)

    }
}
