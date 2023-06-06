package com.example.drp25

import io.getstream.chat.java.models.User

class Backend {

    fun generateUserToken(name: String) {
        var token = User.createToken(name, null, null);
    }

    companion object {
        fun createFriend(userId: String) {
            val user1 = User.UserRequestObject.builder().id(userId).role("user").build();
            User.upsert().user(user1).request();
        }
    }

}