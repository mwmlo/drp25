package com.example.drp25.matchers

import android.util.Log
import com.example.drp25.Matcher
import com.google.firebase.database.DataSnapshot

class MyMatcher : Matcher {
    override fun isMatch(user1Snapshot: DataSnapshot, user2Snapshot: DataSnapshot): Boolean {
        // Check nationality is different
        val nationality1 = user1Snapshot.child("nationality").value
        val nationality2 = user2Snapshot.child("nationality").value
        if (nationality1 == nationality2) return false

        // Check not already matched
        val user2Id = user2Snapshot.key
        val matched1Ref = user1Snapshot.child("matched")
        for (match in matched1Ref.children) {
            if (match.child("matchId").value == user2Id) return false
        }

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