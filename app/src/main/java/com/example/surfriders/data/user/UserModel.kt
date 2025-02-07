package com.example.surfriders.data.user

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import com.example.surfriders.data.AppLocalDatabase
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import java.util.concurrent.Executors

class UserModel private constructor() {

    private val database = AppLocalDatabase.db
    private var usersExecutor = Executors.newSingleThreadExecutor()
    private val firebaseModel = UserFirebaseModel()
    private val users: LiveData<MutableList<User>>? = null

    companion object {
        val instance: UserModel = UserModel()
    }

    fun getAllUsers(): LiveData<MutableList<User>> {
        refreshAllUsers()
        return users ?: database.userDto().getAllUsers()
    }

    fun getCurrentUser(): LiveData<User> {
        Log.i("UserModel", "getCurrentUser: ${Firebase.auth.currentUser?.uid}")
        Log.i("UserModel", "getCurrentUser: ${Firebase.auth.currentUser?.uid}")
        return database.userDto().getUserById(Firebase.auth.currentUser?.uid!!)
    }

    private fun refreshAllUsers() {
        val lastModified: Long = User.lastModified

        firebaseModel.getAllUsers(lastModified) { list ->
            var time = lastModified
            for (user in list) {
                firebaseModel.getImage(user.id) { uri ->
                    usersExecutor.execute {
                        user.profileImageUrl = uri.toString()
                        database.userDto().insertUser(user)
                    }
                }

                user.lastModified?.let {
                    if (time < it)
                        time = user.lastModified ?: System.currentTimeMillis()
                }
                User.lastModified = time
            }
        }
    }

    fun updateUser(user: User?, callback: () -> Unit) {

        Log.d("UpdateUser", "inside UserModel: update user")


        firebaseModel.updateUser(user) {
            refreshAllUsers()
            callback()
        }
    }

    fun updateUserImage(userId: String, selectedImageUri: Uri, callback: () -> Unit) {
        firebaseModel.addUserImage(userId, selectedImageUri) {
            refreshAllUsers()
            callback()
        }
    }

    fun getUserImage(imageId: String, callback: (Uri) -> Unit) {
        firebaseModel.getImage(imageId, callback);
    }

    fun addUser(user: User, ImageUri: Uri, callback: () -> Unit) {

        firebaseModel.addUser(user) {
            Log.d("userModel", "Inside Add User")
            firebaseModel.addUserImage(user.id, ImageUri) {
                Log.d("userModel", "Inside Add Image")
                refreshAllUsers()
                callback()
            }
        }



//        try {
//            firebaseModel.addUser(user) {
//                Log.d("userModel", "Inside Add User")
//                firebaseModel.addUserImage(user.id, ImageUri) {
//                    Log.d("userModel", "Inside Add Image")
//                    refreshAllUsers()
//                    callback()
//                }
//            }
//        }  catch (e: Exception) {
//            Log.d("userModel", "Error: $e")
//        }
    }

}