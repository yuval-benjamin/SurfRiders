package com.example.surfriders.data.post

import android.content.Context
import android.util.Log
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.surfriders.SurfRidersApplication
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldValue
import java.io.Serializable

@Entity
data class Post(
    @PrimaryKey
    val id: String,
    val text: String,
    val grade: Int,
    val userId: String,
    val locationId: String,
    val locationName: String,
    var isDeleted: Boolean = false,
    var postImage: String? = null,
    var timestamp: Long? = null,
) : Serializable {

    companion object {
        var lastUpdated: Long
            get() {
                return SurfRidersApplication.Globals
                    .appContext?.getSharedPreferences("TAG", Context.MODE_PRIVATE)
                    ?.getLong(POST_LAST_UPDATED, 0) ?: 0
            }
            set(value) {
                SurfRidersApplication.Globals
                    ?.appContext
                    ?.getSharedPreferences("TAG", Context.MODE_PRIVATE)?.edit()
                    ?.putLong(POST_LAST_UPDATED, value)?.apply()
            }

        const val ID_KEY = "id"
        const val TEXT_KEY = "text"
        const val USER_ID_KEY = "userId"
        const val LOCATION_ID_KEY = "locationId"
        const val LOCATION_NAME_KEY = "locationName"
        const val GRADE_KEY = "grade"
        const val LAST_UPDATED_KEY = "timestamp"
        const val IS_DELETED_KEY = "is_deleted"
        const val POST_IMAGE_KEY = "postImage"
        private const val POST_LAST_UPDATED = "post_last_updated"

        fun fromJSON(json: Map<String, Any>): Post {
            Log.d(
                "Post",
                "fromJSON: $json score: ${json[GRADE_KEY]} scoreInt: ${(json[GRADE_KEY] as? Long) ?: 0}"
            )
            val id = json[ID_KEY] as? String ?: ""
            val text = json[TEXT_KEY] as? String ?: ""
            val grade = (json[GRADE_KEY] as? Long)?.toInt() ?: 0
            val isDeleted = json[IS_DELETED_KEY] as? Boolean ?: false
            val userId = json[USER_ID_KEY] as? String ?: ""
            val locationId = json[LOCATION_ID_KEY] as? String ?: ""
            val locationName = json[LOCATION_NAME_KEY] as? String ?: ""
            val postImage = json[POST_IMAGE_KEY] as? String
            val post = Post(id, text, grade, userId, locationId, locationName, isDeleted, postImage)

            val timestamp: Timestamp? = json[LAST_UPDATED_KEY] as? Timestamp
            timestamp?.let {
                post.timestamp = it.seconds
            }

            return post
        }
    }

    val json: HashMap<String, Any?>
        get() {
            return hashMapOf(
                ID_KEY to id,
                TEXT_KEY to text,
                USER_ID_KEY to userId,
                LOCATION_ID_KEY to locationId,
                LOCATION_NAME_KEY to locationName,
                GRADE_KEY to grade,
                LAST_UPDATED_KEY to FieldValue.serverTimestamp(),
                IS_DELETED_KEY to isDeleted,
                POST_IMAGE_KEY to postImage
            )
        }

    val deleteJson: Map<String, Any>
        get() {
            return hashMapOf(
                IS_DELETED_KEY to true,
                LAST_UPDATED_KEY to FieldValue.serverTimestamp(),
            )
        }

    val updateJson: Map<String, Any>
        get() {
            return hashMapOf(
                GRADE_KEY to grade,
                LAST_UPDATED_KEY to FieldValue.serverTimestamp(),
            )
        }
}