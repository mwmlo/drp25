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
            client.addMembers("message", "124", listOf<String>(userId))
                .enqueue { result ->
                    if (result.isSuccess) {
                       val channel: Channel = result.data()
                    } else {
                        // Handle result.error()
                    }
                }


//            User.upsert()
//                .user(
//                    User.UserRequestObject.builder()
//                        .id(userId)
//                        .role("admin")
//                        .additionalField("book", "dune")
//                        .build())
//                .request();
            //val user1 = User.UserRequestObject.builder().id(userId).role("user").build();
            //User.upsert().user(user1).request();
        }
    }

}