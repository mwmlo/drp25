package com.example.drp25

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity

import com.example.drp25.databinding.ActivityChatBinding
import io.getstream.chat.android.client.api.models.querysort.QuerySortByField
import io.getstream.chat.android.client.models.Channel
import io.getstream.chat.android.client.models.Filters
import io.getstream.chat.android.client.models.User
import io.getstream.chat.android.ui.channel.list.header.viewmodel.ChannelListHeaderViewModel
import io.getstream.chat.android.ui.channel.list.header.viewmodel.bindView
import io.getstream.chat.android.ui.channel.list.viewmodel.ChannelListViewModel
import io.getstream.chat.android.ui.channel.list.viewmodel.bindView
import io.getstream.chat.android.ui.channel.list.viewmodel.factory.ChannelListViewModelFactory

// our logged in user
const val UNI_ID = "imperialId"
const val USER_ID = "-NXPnWs-phdiaSN_S87V"
class ChatActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChatBinding

    val pierre = User(
        id = "Pierre",
        name = "Pierre",
        image = "upload.wikimedia.org/wikipedia/commons/6/62/Flag_of_France.png"
    )
    val kevin = User(
        id = "Kevin",
        name = "Kevin",
        image = "cdn.countryflags.com/thumbs/china/flag-round-250.png"
    )
    val max = User(
        id = "Max",
        name = "Max",
        image = "img.freepik.com/premium-vector/german-flag-vector_230920-1254.jpg"
    )
    val sasha = User(
        id = "Sasha",
        name = "Sasha",
        image = "upload.wikimedia.org/wikipedia/commons/thumb/b/bc/Flag_of_India.png/800px-Flag_of_India.png"
    )
    val david = User(
        id = "David",
        name = "David",
        image = "img.freepik.com/premium-vector/german-flag-vector_230920-1254.jpg"
    )

    // Get match for demo
    private val people = listOf(pierre, kevin, max, sasha, david)
    private var i = 0

    private fun getMatch(): String {
        val match = people[i]
        i++
        return match.id
    }

//    val matchesRef = FirebaseDatabase.getInstance().reference.child("matches")

    private fun buildChannelId(user1: String, user2: String): String {
        return "${user1}${user2}".lowercase()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val clientService = ChatClientService(applicationContext)

        // Authenticate and connect the user (MVP)

        if (savedInstanceState == null) {
            Backend.serverInit()
            Backend.createFriend(pierre.id, pierre.name)
            Backend.createFriend(max.id, max.name)
            Backend.createFriend(sasha.id, sasha.name)
            Backend.createFriend(david.id, david.name)

            clientService.connectCurrentUser(kevin).enqueue { result ->
                if (result.isSuccess) {
                    Log.e("connectUser", "success")
                } else {
                    Log.e("connectUser", result.toString())
                }
            }
        }

        // Create new channel if meeting new friend
        val fromMatch = intent.getBooleanExtra("fromMatch", false)
        if (fromMatch) {
            Log.e("main", "createChannel")
            val matchUserName: String = intent.getStringExtra("matchedName")!!
            val cid = buildChannelId(kevin.id, matchUserName)
            clientService.createChannel(cid, kevin.id, matchUserName)
        }

        // Step 0 - inflate binding
        binding = ActivityChatBinding.inflate(layoutInflater)

        val channelListHeaderViewModel: ChannelListHeaderViewModel by viewModels()
        val channelListFactory = ChannelListViewModelFactory(
            filter = Filters.and(
                Filters.eq("type", "messaging"),
                Filters.`in`("members", listOf(kevin.id)),
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
        binding.homeMatchButton.setOnClickListener {
            val intent = Intent(this, MatchActivity::class.java)
            startActivity(intent)
        }

        binding.channelListHeaderView.setOnUserAvatarClickListener{
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
        }
        setContentView(binding.root)

    }
}
