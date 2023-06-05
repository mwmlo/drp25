package com.example.drp25

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

private val unisRef = FirebaseDatabase.getInstance().reference.child("universities")
private val matcher: Matcher = BasicMatcher()
private val INTERESTS = listOf("ballet", "books")

private fun snapshotListener(ref: DatabaseReference, callback: (DataSnapshot) -> Unit) {
    ref.addListenerForSingleValueEvent(object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            callback(snapshot)
        }

        override fun onCancelled(error: DatabaseError) {
            // Handle the error
        }
    })
}

private fun getSnapshot(ref: DatabaseReference): DataSnapshot? {
    var snapshot: DataSnapshot? = null
    snapshotListener(ref) { s -> snapshot = s}
    return snapshot
}

fun getValueData(ref: DatabaseReference): List<String> {
    val dataList = mutableListOf<String>()
    val snapshot = getSnapshot(ref)
    for (childSnapshot in snapshot?.children!!) {
        dataList.add(childSnapshot.value as String)
    }
    return dataList
}

fun getKeyData(ref: DatabaseReference): List<String> {
    val dataList = mutableListOf<String>()
    val snapshot = getSnapshot(ref)
    for (childSnapshot in snapshot?.children!!) {
        childSnapshot.key?.let { dataList.add(it) }
    }
    return dataList
}

private fun addMatch(uniId: String, user1Id: String, user2Id: String) {
    val usersRef = unisRef.child(uniId).child("users")
    val user1MatchesRef = usersRef.child(user1Id).child("matches")
    val user2MatchesRef = usersRef.child(user2Id).child("matches")
    user1MatchesRef.child(user2Id).setValue("")
    user2MatchesRef.child(user1Id).setValue("")
}

private fun removeMatch(uniId: String, user1Id: String, user2Id: String) {
    val usersRef = unisRef.child(uniId).child("users")
    val user1MatchesRef = usersRef.child(user1Id).child("matches")
    val user2MatchesRef = usersRef.child(user2Id).child("matches")
    user1MatchesRef.child(user2Id).removeValue()
    user2MatchesRef.child(user1Id).removeValue()
}

fun updateMatches(uniId: String, userId: String, snapshot: DataSnapshot) {
    val usersRef = unisRef.child(uniId).child("users")
    var userKeys = getKeyData(usersRef)

    for (otherUserId in userKeys) {
        if (otherUserId == userId) continue
        val otherUserRef = usersRef.child(otherUserId)
        if (getSnapshot(otherUserRef)?.let { matcher.isMatch(snapshot, it) } == true) {
            addMatch(uniId, userId, otherUserId)
        } else {
            removeMatch(uniId, userId, otherUserId)
        }
    }
}

fun getMatches(uniId: String, userId: String): List<String> {
    val matchesRef = unisRef.child(uniId).child("users").child(userId).child("matches")
    return getValueData(matchesRef)
}

// returns generated userId (or null if unsuccessful)
fun addUser(uniId: String, name: String, nationality: String): String? {
    val usersRef = unisRef.child(uniId).child("users")
    val userId = usersRef.push().key
    if (userId != null) {
        val userRef = usersRef.child(userId)
        userRef.child("name").setValue(name)
        userRef.child("nationality").setValue(nationality)
        userRef.child("interests").addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                updateMatches(uniId, userId, snapshot)
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
        INTERESTS.forEach { interest ->
            run {
                userRef.child("interests").child(interest).setValue(false)
            }
        }
    }
    return userId
}

fun addInterest(uniId: String, userId: String, interest: String) {
    modifyInterest(uniId, userId, interest, true)
}

fun removeInterest(uniId: String, userId: String, interest: String) {
    modifyInterest(uniId, userId, interest, false)
}

private fun modifyInterest(uniId: String, userId: String, interest: String, boolean: Boolean) {
    val usersRef = unisRef.child(uniId).child("users").child(userId).child("interests")
        .child(interest).setValue(boolean)
}