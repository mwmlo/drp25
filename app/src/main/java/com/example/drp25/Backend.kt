package com.example.drp25

import io.getstream.chat.android.client.ChatClient
import io.getstream.chat.android.client.models.Channel
import io.getstream.chat.java.models.User
import io.getstream.chat.java.services.framework.DefaultClient
import java.util.Arrays
import java.util.Properties


class Backend {

    fun generateUserToken(name: String) {
        var token = User.createToken(name, null, null);
    }

    companion object {
        fun serverInit() {
            val properties = Properties()
            properties.put(DefaultClient.API_KEY_PROP_NAME, "4tm42krd5mvf")
            properties.put(DefaultClient.API_SECRET_PROP_NAME,
                "fxkykpy58zm6tnr5a4d2pgzmmxd297xq4688gs9eddb8qju2a7xzucynntg49qk5")
            val client = DefaultClient(properties)
            DefaultClient.setInstance(client)
        }
        fun createFriend(userId: String, client: ChatClient) {
            serverInit()

            // Add members with ids "thierry" and "josh"
            // Add members with ids "thierry" and "josh"
            // I think this tries to add these members but because they don't
            // exist in the server, nothing can be added. 123 is the bottom channel
            // 2... is the top channel, I think 234
            // 124 is the middle channel
            // Create a user with the given id
            // This breaks it

            // MVP: Create a guest user who isn't logged in
            val user2 = io.getstream.chat.android.client.models.User(
                id = userId,
                name = "Bob",
                image = "https://bit.ly/2TIt8NR"
            )
            client.connectGuestUser(userId, "Bob", null)
            // Add them using the following code
            client.addMembers("message", "123", listOf<String>(userId))
                .enqueue { result ->
                    if (result.isSuccess) {
                       val channel: Channel = result.data()
                    } else {
                        // Handle result.error()
                    }
                }

            //val user1 = User.UserRequestObject.builder().id(userId).role("user").build();
            //User.upsert().user(user1).request();
        }
    }

}