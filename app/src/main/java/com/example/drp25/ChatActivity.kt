package com.example.drp25

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity

import com.example.drp25.databinding.ActivityChatBinding
import io.getstream.chat.android.client.api.models.querysort.QuerySortByField
import io.getstream.chat.android.client.models.Filters
import io.getstream.chat.android.client.models.User
import io.getstream.chat.android.ui.channel.list.header.viewmodel.ChannelListHeaderViewModel
import io.getstream.chat.android.ui.channel.list.header.viewmodel.bindView
import io.getstream.chat.android.ui.channel.list.viewmodel.ChannelListViewModel
import io.getstream.chat.android.ui.channel.list.viewmodel.bindView
import io.getstream.chat.android.ui.channel.list.viewmodel.factory.ChannelListViewModelFactory
import kotlin.random.Random

class ChatActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChatBinding

    companion object {
        private val pierre = User(
            id = "Pierre",
            name = "Pierre",
            image = "upload.wikimedia.org/wikipedia/commons/6/62/Flag_of_France.png"
        )
        private val kevin = User(
            id = "Kevin",
            name = "Kevin",
            image = "cdn.countryflags.com/thumbs/china/flag-round-250.png"
        )
        private val max = User(
            id = "Max",
            name = "Max",
            image = "img.freepik.com/premium-vector/german-flag-vector_230920-1254.jpg"
        )
        private val felix = User(
            id = "Felix",
            name = "Felix",
            image = "upload.wikimedia.org/wikipedia/commons/thumb/b/bc/Flag_of_India.png/800px-Flag_of_India.png"
        )
        private val david = User(
            id = "David",
            name = "David",
            image = "img.freepik.com/premium-vector/german-flag-vector_230920-1254.jpg"
        )

        private var currentUser: User = kevin
        private var potentialFriends: List<User> = listOf()
        fun setCurrentUser(name: String) {
            if (name == "kevin") {
                currentUser = kevin
                potentialFriends = listOf(pierre, max, felix, david)
            } else {
                currentUser = max
                potentialFriends = listOf(pierre, kevin, felix, david)
            }
        }
    }

    // Get match for demo
//    private var i = 0
//
//    private fun getMatch(): String {
//        val match = potentialFriends[i]
//        i++
//        return match.id
//    }

//    val matchesRef = FirebaseDatabase.getInstance().reference.child("matches")

    // Must be randomly generated to ensure chats aren't recreated post deletion
    private fun buildChannelId(user1: String, user2: String): String {
        val charPool : List<Char> = ('a'..'z') + ('A'..'Z') + ('0'..'9')
        return (1..20)
            .map { Random.nextInt(0, charPool.size).let { charPool[it] } }
            .joinToString("")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val clientService = ChatClientService(applicationContext)

        // Authenticate and connect the user (MVP)

        if (savedInstanceState == null) {
            Backend.serverInit()

            for (person in potentialFriends) {
                Backend.createFriend(person.id, person.name)
            }

            clientService.connectCurrentUser(currentUser).enqueue { result ->
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
            val matchUserName: String? = intent.getStringExtra("matchedName")
            if (matchUserName != null) {
                val cid = buildChannelId(currentUser.id, matchUserName)
                clientService.createChannel(cid, currentUser.id, matchUserName)
                Log.e("match", "created channel")
                val intent = Intent(this, SplashScreenActivity::class.java)
                startActivity(intent)
            }
        }

        // Step 0 - inflate binding
        binding = ActivityChatBinding.inflate(layoutInflater)

        val channelListHeaderViewModel: ChannelListHeaderViewModel by viewModels()
        val channelListFactory = ChannelListViewModelFactory(
            filter = Filters.and(
                Filters.eq("type", "messaging"),
                Filters.`in`("members", listOf(currentUser.id)),
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

        binding.channelListHeaderView.setOnlineTitle("Globe Chatter")

    }
}
