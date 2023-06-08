package com.example.drp25

import com.google.firebase.database.DataSnapshot

class BasicMatcher : Matcher {
    override fun isMatch(user1Snapshot: DataSnapshot, user2Snapshot: DataSnapshot): Boolean {
        val interests1Ref = user1Snapshot.child("interests")
        val interests2Ref = user2Snapshot.child("interests")

        // return true iff share at least one interest
        for (child in interests1Ref.children) {
            if (child.key?.let { interests2Ref.hasChild(it) } == true) {
                return true
            }
        }
        return false
    }

}