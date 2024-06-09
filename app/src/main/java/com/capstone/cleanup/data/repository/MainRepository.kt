package com.capstone.cleanup.data.repository

import android.content.Context
import android.util.Log
import androidx.core.content.ContextCompat.getString
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.exceptions.GetCredentialException
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.capstone.cleanup.R
import com.capstone.cleanup.utils.showToast
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import com.google.firebase.Firebase
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore

class MainRepository private constructor(
    private val context: Context
){
    private val auth: FirebaseAuth = Firebase.auth
    val currentUser = auth.currentUser

    private val fireStore: FirebaseFirestore = Firebase.firestore
    val articlesRef = fireStore.collection(ARTICLE_CHILD).orderBy("id")

    private val _isSuccess = MutableLiveData<Boolean>()
    val isSuccess: LiveData<Boolean> get() = _isSuccess

    suspend fun signIn(){
        val credentialManager = CredentialManager.create(context)

        val googleIdOption = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(false)
            .setServerClientId(getString(context, R.string.your_web_client_id))
            .build()

        val request = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

        try {
            val result: GetCredentialResponse = credentialManager.getCredential(
                request = request,
                context = context
            )
            handleSignIn(result)
        } catch (e: GetCredentialException){
            _isSuccess.value = false
            Log.d("Error", e.message.toString())
        }
    }

    private fun handleSignIn(result: GetCredentialResponse) {
        // Handle the successfully returned credential.
        when (val credential = result.credential) {
            is CustomCredential -> {
                if (credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                    try {
                        val googleIdTokenCredential =
                            GoogleIdTokenCredential.createFrom(credential.data)
                        firebaseAuthWithGoogle(googleIdTokenCredential.idToken)
                    } catch (e: GoogleIdTokenParsingException) {
                        Log.e(TAG, "Received an invalid google id token response", e)
                    }
                } else {
                    // Catch any unrecognized custom credential type here.
                    _isSuccess.value = false
                    Log.e(TAG, "Unexpected type of credential")
                }
            }

            else -> {
                // Catch any unrecognized credential type here.
                _isSuccess.value = false
                Log.e(TAG, "Unexpected type of credential")
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential: AuthCredential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "signInWithCredential:success")
                    val user: FirebaseUser? = auth.currentUser
                    auth.updateCurrentUser(user!!)
                        .addOnSuccessListener {
                            _isSuccess.value = true
                            showToast(context, "Login Success")
                    }
                        .addOnFailureListener {
                            Log.e(TAG, it.message.toString())
                            showToast(context, "Login Fail")
                        }
                } else {
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    _isSuccess.value = false
                    showToast(context, "Login Fail")
                }
            }
    }

    fun loginWithEmailAndPassword(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "signInWithEmailAndPassword:success")
                    val user = auth.currentUser
                    auth.updateCurrentUser(user!!)
                        .addOnSuccessListener {
                            _isSuccess.value = true
                            showToast(context, "Login Success")
                        }
                        .addOnFailureListener {
                            Log.e(TAG, it.message.toString())
                            showToast(context, "Login Fail")
                        }
                } else {
                    _isSuccess.value = false
                    Log.w(TAG, "signInWithEmailAndPassword:failure", task.exception)
                    // Handle login failure (e.g., display error message)
                    showToast(context, "Login failed!")
                }
            }
    }

    fun register(email: String, password: String, name: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val setUsername = UserProfileChangeRequest.Builder()
                        .setDisplayName(name)
                        .build()

                    task.result.user?.updateProfile(setUsername) // set displayName of the user to server
                    _isSuccess.value = true
                    showToast(context, "Registration successful!")
                } else {
                    Log.w(TAG, "createUserWithEmailAndPassword:failure", task.exception)
                    _isSuccess.value = false
                    showToast(context, "Registration failed!")
                }
            }
    }

    suspend fun signOut() {
        val credentialManager = CredentialManager.create(context)
        auth.signOut()
        credentialManager.clearCredentialState(ClearCredentialStateRequest())
    }

    companion object {
        private val TAG = MainRepository::class.java.simpleName

        private const val ARTICLE_CHILD = "article"

        fun getInstance(
            context: Context
        ) = MainRepository(context)
    }
}