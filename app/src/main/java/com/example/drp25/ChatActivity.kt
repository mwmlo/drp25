package com.example.drp25

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity

import com.example.drp25.databinding.ActivityChatBinding
import com.google.firebase.database.FirebaseDatabase
import io.getstream.chat.android.client.api.models.querysort.QuerySortByField
import io.getstream.chat.android.client.call.Call
import io.getstream.chat.android.client.models.Channel
import io.getstream.chat.android.client.models.Filters
import io.getstream.chat.android.client.models.User
import io.getstream.chat.android.ui.channel.list.header.viewmodel.ChannelListHeaderViewModel
import io.getstream.chat.android.ui.channel.list.header.viewmodel.bindView
import io.getstream.chat.android.ui.channel.list.viewmodel.ChannelListViewModel
import io.getstream.chat.android.ui.channel.list.viewmodel.bindView
import io.getstream.chat.android.ui.channel.list.viewmodel.factory.ChannelListViewModelFactory

// our logged in user
val UNI_ID = "imperialId"
val USER_ID = "-NXGEo30rzoWUgTYoYi_"

class ChatActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChatBinding
    private lateinit var channelGlob: Call<Channel>

    // Get match for demo
    private val NAMES = listOf<String>("Pierre", "Kevin", "Martha", "India", "Jerry", "Simon")
    private var i = 0

    private fun getMatch(): String {
        val match = NAMES.get(i)
        i = (i+1)%(NAMES.size)
        return match
    }
    val matchesRef = FirebaseDatabase.getInstance().reference.child("matches")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val clientService = ChatClientService(applicationContext)

        // Step 0 - inflate binding
        binding = ActivityChatBinding.inflate(layoutInflater)

        // Step 3 - Authenticate and connect the user (MVP)
        val user = User(
            id = "demo-user",
            name = "Name",
            image = "https://bit.ly/2TIt8NR"
        )
        clientService.connectCurrentUser(user)

        val friend = User(
            id = "friend",
            name = "Friend",
            image = "https://bit.ly/2TIt8NR"
        )

        // Only allows the binding to be visible if the page is not opened from a match activity
        if (intent.hasExtra("fromMatch") && intent.getBooleanExtra("fromMatch", true)) {
            clientService.createChannel("1234", user.id, friend.id)
//            createAndRunChannel(client, user)
        } else {
            setContentView(binding.root)
            clientService.createChannel("5678", user.id, friend.id)
            // Note this channel is not showing up
//            createDemoChannel(client, user, "14355")
            // seems to be something wrong with channel.execute.data, causing issues
            // startActivity(ChannelActivity.newIntent(this, ch))
        }


        /* Create views for the list of channels */
        val channelListHeaderViewModel: ChannelListHeaderViewModel by viewModels()
        val channelListFactory: ChannelListViewModelFactory = ChannelListViewModelFactory(
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

        /* When a channel is clicked, the user is taken to the channel. */
        binding.channelListView.setChannelItemClickListener { channel ->
            startActivity(ChannelActivity.newIntent(this, channel))
        }

        /* Functionality of "Meet Someone New" button -> takes user to match page. */
        binding.homeMatchButton.setOnClickListener { _ ->
            val intent = Intent(this, MatchActivity::class.java)
            startActivity(intent)
        }

        // indicates this is the person logged in (currently Kevin)
        listenToUser(UNI_ID, USER_ID)

    }

//    companion object {
//        // Cannot access the application context of original chat activity
//        private var instance: ChatActivity? = null
//        fun getContext(): Context = instance!!.applicationContext
//    }
}

// Add members with ids "thierry" and "josh"
/*
channelClient.addMembers(Arrays.asList("thierry", "josh"), null).enqueue(result -> {
    if (result.isSuccess()) {
        Channel channel = result.data();
    } else {
       // Handle result.error()
   }
});

*/
