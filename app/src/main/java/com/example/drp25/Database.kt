package com.example.drp25

import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.GenericTypeIndicator
import com.google.firebase.database.ValueEventListener
import com.google.gson.Gson
import org.json.JSONArray

private val unisRef = FirebaseDatabase.getInstance().reference.child("universities")
private val matcher: Matcher = BasicMatcher()
private val gson = Gson()

//private fun addMatch(uniId: String, user1Id: String, user2Id: String) {
//    val usersRef = unisRef.child(uniId).child("users")
//    val matches1Ref = usersRef.child(user1Id).child("matches")
//    val matches2Ref = usersRef.child(user2Id).child("matches")
//
//    val matches1 = getList(matches1Ref)?.toMutableList()
//    val matches2 = getList(matches2Ref)?.toMutableList()
//    matches1?.add(user2Id)
//    matches2?.add(user1Id)
//
//    matches1Ref.setValue(matches1)
//    matches2Ref.setValue(matches2)
//}

//private fun removeMatch(uniId: String, user1Id: String, user2Id: String) {
//    val usersRef = unisRef.child(uniId).child("users")
//    val matches1Ref = usersRef.child(user1Id).child("matches")
//    val matches2Ref = usersRef.child(user2Id).child("matches")
//
//    val matches1 = getList(matches1Ref)?.toMutableList()
//    val matches2 = getList(matches2Ref)?.toMutableList()
//    matches1?.remove(user2Id)
//    matches2?.remove(user1Id)
//
//    matches1Ref.setValue(matches1)
//    matches2Ref.setValue(matches2)
//}

fun updateMatches(uniId: String, userId: String, snapshot: DataSnapshot) {
    val usersRef = unisRef.child(uniId).child("users")
    usersRef.addListenerForSingleValueEvent(object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            for (child : dataSnapshot.children) {
                val otherKey = child.key
                //
            }
        }

        override fun onCancelled(databaseError: DatabaseError) {
            //TODO
        }
    })

//    var userKeys = getKeyData(usersRef)
//
//    for (otherUserId in userKeys) {
//        if (otherUserId == userId) continue
//        val otherUserRef = usersRef.child(otherUserId)
//        if (getSnapshot(otherUserRef)?.let { matcher.isMatch(snapshot, it) } == true) {
//            addMatch(uniId, userId, otherUserId)
//        } else {
//            removeMatch(uniId, userId, otherUserId)
//        }
//    }
}

//fun getMatches(uniId: String, userId: String): List<String> {
//    val matchesRef = unisRef.child(uniId).child("users").child(userId).child("matches")
//    return getValueData(matchesRef)
//}

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
        userRef.child("interests").setValue(gson.toJson(listOf<String>()))
    }
    return userId
}

fun addInterest(uniId: String, userId: String, newInterest: String) {
    val interestsRef = unisRef.child(uniId).child("users").child(userId).child("interests")
    interestsRef.addListenerForSingleValueEvent(object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            val value = dataSnapshot.getValue(String::class.java)
            val interests = gson.fromJson(value, Array<String>::class.java).toMutableList()
            interests.add(newInterest)
            interestsRef.setValue(gson.toJson(interests))
        }

        override fun onCancelled(databaseError: DatabaseError) {
           //TODO
        }
    })
}

fun removeInterest(uniId: String, userId: String, oldInterest: String) {
    val interestsRef = unisRef.child(uniId).child("users").child(userId).child("interests")
    interestsRef.addListenerForSingleValueEvent(object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            val value = dataSnapshot.getValue(String::class.java)
            val interests = gson.fromJson(value, Array<String>::class.java).toMutableList()
            interests.remove(oldInterest)
            interestsRef.setValue(gson.toJson(interests))
        }

        override fun onCancelled(databaseError: DatabaseError) {
            //TODO
        }
    })
}

//private fun modifyInterest(uniId: String, userId: String, interest: String, boolean: Boolean) {
//    val usersRef = unisRef.child(uniId).child("users").child(userId).child("interests")
//        .child(interest).setValue(boolean)
//}