package com.example.closetcraft

import android.app.Application
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class App : Application() {
    override fun onCreate() {
        super.onCreate()

        // ✅ Enable Firebase Realtime Database Offline Sync (if using database)
        FirebaseDatabase.getInstance().setPersistenceEnabled(true)

        // ✅ FirebaseAuth automatically caches session - nothing more needed
    }
}
