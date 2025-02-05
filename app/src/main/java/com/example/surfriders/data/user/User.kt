package com.example.surfriders.data.user

import android.content.Context
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.surfriders.SurfRidersApplication
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldValue

@Entity
class User(
    @PrimaryKey
    val id: String,
    val firstName: String,
    val lastName: String,
    var profileImageUrl: String? = null,
    var lastModified: Long? = null,
) {
    companion object {
        var lastModified: Long
            get() {
                return SurfRidersApplication.Globals
                    .appContext?.getSharedPreferences("TAG", Context.MODE_PRIVATE)
                    ?.getLong(USER_LAST_MODIFIED, 0) ?: 0
            }
            set(value) {
                SurfRidersApplication.Globals
                    ?.appContext
                    ?.getSharedPreferences("TAG", Context.MODE_PRIVATE)?.edit()
                    ?.putLong(USER_LAST_MODIFIED, value)?.apply()
            }

        private const val ID_KEY = "id"
        private const val FIRST_NAME_KEY = "firstName"
        private const val LAST_NAME_KEY = "lastName"
        const val LAST_MODIFIED_KEY = "lastModified"
        const val USER_LAST_MODIFIED = "user_last_modified"

        fun fromJSON(json: Map<String, Any>): User {
            val id = json[ID_KEY] as? String ?: ""
            val firstName = json[FIRST_NAME_KEY] as? String ?: ""
            val lastName = json[LAST_NAME_KEY] as? String ?: ""
            val user = User(id, firstName, lastName)

            val lastModified: Timestamp? = json[LAST_MODIFIED_KEY] as? Timestamp
            lastModified?.let {
                user.lastModified = it.seconds
            }
            return user
        }
    }

    val json: Map<String, Any>
        get() {
            return hashMapOf(
                ID_KEY to id,
                FIRST_NAME_KEY to firstName,
                LAST_NAME_KEY to lastName,
                LAST_MODIFIED_KEY to FieldValue.serverTimestamp(),
            )
        }

    val updateJson: Map<String, Any>
        get() {
            return hashMapOf(
                FIRST_NAME_KEY to firstName,
                LAST_NAME_KEY to lastName,
                LAST_MODIFIED_KEY to FieldValue.serverTimestamp(),
            )
        }
}