package com.example.drp25

import com.google.firebase.database.DataSnapshot

class BasicMatcher : Matcher {
    override fun isMatch(user1Snapshot: DataSnapshot, user2Snapshot: DataSnapshot): Boolean {
        // get interests from both
        val interests1 = gson.fromJson(user1Snapshot.child("interests").getValue(String::class.java),
            Array<String>::class.java).toList()
        val interests2 = gson.fromJson(user2Snapshot.child("interests").getValue(String::class.java),
            Array<String>::class.java).toList()

        // return true iff share at least one interest
        return interests1.any { interests2.contains(it) }
    }
}