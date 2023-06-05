package com.example.drp25

import com.google.firebase.database.FirebaseDatabase

class BasicMatchGenerator : MatchGenerator {
    private val unisRef = FirebaseDatabase.getInstance().getReference().child("universities")

    override fun getMatches(uniId: String, userId: String): List<String> {
        val matchesRef = unisRef.child(uniId).child("users").child(userId).child("matches")
        var retList: List<String> = listOf()
        getData(matchesRef) { dataList -> retList = dataList }
        return retList
    }
}