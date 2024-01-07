import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore

class UserDAO {

    private val db = FirebaseFirestore.getInstance()
    private val usersCollection = db.collection("users")

    fun saveNewUserData(userId: String, email: String, callback: (Boolean, String) -> Unit) {
        val userData = mapOf(
            "email" to email,
            "userId" to userId
        )

        usersCollection.document(email)
            .set(userData)
            .addOnSuccessListener {
                callback(true, "User data saved successfully")
            }
            .addOnFailureListener { e ->
                callback(false, "Failed to save user data: ${e.message}")
            }
    }

    fun getUserData(userId: String, callback: (Map<String, Any>?, Exception?) -> Unit) {
        usersCollection.document(userId)
            .get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    callback(document.data, null)
                } else {
                    callback(null, null)
                }
            }
            .addOnFailureListener { e ->
                callback(null, e)
            }
    }

    fun getUserEmailByUserId(userId: String, callback: (String?, String?) -> Unit) {
        Log.d("UserDAO", "Fetching email for userId: $userId")

        usersCollection.whereEqualTo("userId", userId)
            .limit(1)
            .get()
            .addOnSuccessListener { querySnapshot ->
                if (!querySnapshot.isEmpty) {
                    val userDocument = querySnapshot.documents[0]
                    val userEmail = userDocument.getString("email")
                    Log.d("UserDAO", "Found user email: $userEmail for userId: $userId")
                    callback(userEmail, null)
                } else {
                    Log.d("UserDAO", "No user found for userId: $userId")
                    callback(null, "User not found")
                }
            }
            .addOnFailureListener { e ->
                Log.e("UserDAO", "Error fetching user email: ${e.message}")
                callback(null, "Error fetching user email: ${e.message}")
            }
    }


    fun updateUserData(userId: String, updatedData: Map<String, Any>, callback: (Boolean, Exception?) -> Unit) {
        usersCollection.document(userId)
            .update(updatedData)
            .addOnSuccessListener {
                callback(true, null)
            }
            .addOnFailureListener { e ->
                callback(false, e)
            }
    }
}
