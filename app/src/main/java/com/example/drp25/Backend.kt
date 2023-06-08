package com.example.drp25

import io.getstream.chat.android.client.ChatClient
import io.getstream.chat.java.models.User
import io.getstream.chat.java.services.framework.DefaultClient
import java.util.*


class Backend {

    companion object {
        private fun serverInit() {
            val properties = Properties()
            properties.put(DefaultClient.API_KEY_PROP_NAME, "4tm42krd5mvf")
            properties.put(DefaultClient.API_SECRET_PROP_NAME,
                "fxkykpy58zm6tnr5a4d2pgzmmxd297xq4688gs9eddb8qju2a7xzucynntg49qk5")
            val client = DefaultClient(properties)
            DefaultClient.setInstance(client)
        }

        fun createFriend(userId: String, name: String, client: ChatClient) {
            serverInit()

            // MVP: Create a guest user who isn't logged in
//            val user2 = io.getstream.chat.android.client.models.User(
//                id = userId,
//                name = "Bob",
//                image = "https://bit.ly/2TIt8NR"
//            )
//            client.connectGuestUser(userId, name, null).enqueue()

            val user1 = User.UserRequestObject.builder().id(userId).name(name).role("user").build();
            Thread {
                // Do network action in this function
                User.upsert().user(user1).request()
            }.start()
        }
    }

}