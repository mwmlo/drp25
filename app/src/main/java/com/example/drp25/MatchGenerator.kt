package com.example.drp25

interface MatchGenerator {
    fun getMatches(uniId: String, userId: String): List<String>
}