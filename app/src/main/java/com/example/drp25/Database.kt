package com.example.drp25

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.GenericTypeIndicator
import com.google.firebase.database.ValueEventListener

private val unisRef = FirebaseDatabase.getInstance().reference.child("universities")
private val matcher: Matcher = BasicMatcher()

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

private fun getList(ref: DatabaseReference): List<String>? {
    val snapshot = getSnapshot(ref)
    return snapshot?.getValue(object : GenericTypeIndicator<List<String>>() {})
}

private fun addMatch(uniId: String, user1Id: String, user2Id: String) {
    val usersRef = unisRef.child(uniId).child("users")
    val matches1Ref = usersRef.child(user1Id).child("matches")
    val matches2Ref = usersRef.child(user2Id).child("matches")

    val matches1 = getList(matches1Ref)?.toMutableList()
    val matches2 = getList(matches2Ref)?.toMutableList()
    matches1?.add(user2Id)
    matches2?.add(user1Id)

    matches1Ref.setValue(matches1)
    matches2Ref.setValue(matches2)
}

private fun removeMatch(uniId: String, user1Id: String, user2Id: String) {
    val usersRef = unisRef.child(uniId).child("users")
    val matches1Ref = usersRef.child(user1Id).child("matches")
    val matches2Ref = usersRef.child(user2Id).child("matches")

    val matches1 = getList(matches1Ref)?.toMutableList()
    val matches2 = getList(matches2Ref)?.toMutableList()
    matches1?.remove(user2Id)
    matches2?.remove(user1Id)

    matches1Ref.setValue(matches1)
    matches2Ref.setValue(matches2)
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
        userRef.child("interests").setValue(setOf<String>())
    }
    return userId
}

fun addInterest(uniId: String, userId: String, newInterest: String) {
    val interestsRef = unisRef.child(uniId).child("users").child(userId).child("interests")
    val interests = getList(interestsRef)?.toMutableList()
    interests?.add(newInterest)
    interestsRef.setValue(interests)
}

fun removeInterest(uniId: String, userId: String, oldInterest: String) {
    val interestsRef = unisRef.child(uniId).child("users").child(userId).child("interests")
    val interests = getList(interestsRef)?.toMutableList()
    interests?.remove(oldInterest)
    interestsRef.setValue(interests)
}

private fun modifyInterest(uniId: String, userId: String, interest: String, boolean: Boolean) {
    val usersRef = unisRef.child(uniId).child("users").child(userId).child("interests")
        .child(interest).setValue(boolean)
}