package com.capstone.cleanup.data.repository

import android.content.Context
import android.net.Uri
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
import com.capstone.cleanup.data.response.ArticleItem
import com.capstone.cleanup.data.retrofit.ApiService
import com.firebase.ui.firestore.BuildConfig
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
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.storage
import retrofit2.HttpException

class MainRepository private constructor(
    private val context: Context,
    private val apiService: ApiService
){
    private val auth: FirebaseAuth = Firebase.auth
    val currentUser = auth.currentUser

    private val fireStore: FirebaseFirestore = Firebase.firestore
    val reportRef = fireStore.collection(REPORT_CHILD)

    val storage: FirebaseStorage = Firebase.storage

    private val _isSuccess = MutableLiveData<Boolean>()
    val isSuccess: LiveData<Boolean> get() = _isSuccess

    private val _isUploading = MutableLiveData<Boolean>()
    val isUploading: LiveData<Boolean> get() = _isUploading

    private val _liveProfilePic = MutableLiveData<Uri>()
    val liveProfilePic: LiveData<Uri> get() = _liveProfilePic

    private val _articles = MutableLiveData<List<ArticleItem>>()
    val articles: LiveData<List<ArticleItem>> get() = _articles

    private val _errMsg = MutableLiveData<String>()
    val errMsg: LiveData<String> get() = _errMsg

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
                    }
                        .addOnFailureListener {
                            Log.e(TAG, it.message.toString())
                            _errMsg.value = it.message
                        }
                } else {
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    _isSuccess.value = false
                    _errMsg.value = task.exception?.message
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
                        }
                        .addOnFailureListener {
                            Log.e(TAG, it.message.toString())
                            _errMsg.value = it.message
                        }
                } else {
                    _isSuccess.value = false
                    Log.w(TAG, "signInWithEmailAndPassword:failure", task.exception)
                    // Handle login failure (e.g., display error message)
                    _errMsg.value = task.exception?.message
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
                } else {
                    Log.w(TAG, "createUserWithEmailAndPassword:failure", task.exception)
                    _isSuccess.value = false
                    _errMsg.value = task.exception?.message
                }
            }
    }

    suspend fun signOut() {
        val credentialManager = CredentialManager.create(context)
        auth.signOut()
        credentialManager.clearCredentialState(ClearCredentialStateRequest())
    }

    fun changeProfilePic(imageUri: Uri) {
        val userDataRef = storage.reference.child(USER_DATA_CHILD).child(currentUser?.displayName.toString())

        val uploadTask = userDataRef.putFile(imageUri)


        uploadTask.addOnProgressListener {
            _isUploading.value = true
        }

        uploadTask.addOnFailureListener {
            _isUploading.value = false
            _errMsg.value = it.message
        }

        uploadTask.addOnSuccessListener {
            if (it.task.isSuccessful) {
                val resultURL = userDataRef.downloadUrl
                if (BuildConfig.DEBUG) Log.d(TAG, "Link Before Update: ${currentUser?.photoUrl}")

                resultURL.addOnSuccessListener { uri ->
                    val setProfilePic = UserProfileChangeRequest.Builder()
                        .setPhotoUri(uri)
                        .build()
                    currentUser?.updateProfile(setProfilePic)?.addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Log.d(TAG, "Profile update Success")
                            auth.updateCurrentUser(currentUser)
                            if (BuildConfig.DEBUG) Log.d(TAG, "Link After Update: ${currentUser.photoUrl}")
                            currentUser.reload()
                            _liveProfilePic.value = currentUser.photoUrl
                            Log.d(TAG, "Profile reloaded")
                            if (BuildConfig.DEBUG) Log.d(TAG, "Link After Reload: ${currentUser.photoUrl}")
                        }
                    }
                    _isUploading.value = false
                }
            } else {
                _isUploading.value = false
                _errMsg.value = it.task.exception?.message
            }
        }
    }

    suspend fun getArticles(){
        try {
            val response = apiService.getArticles()
            val responseData = response.article

            if (BuildConfig.DEBUG) Log.d(TAG, responseData.toString())
            _articles.value = responseData
        } catch (e: HttpException) {
            _errMsg.value = e.message
        }
    }

    companion object {
        private val TAG = MainRepository::class.java.simpleName

        private const val REPORT_CHILD = "report"
        private const val USER_DATA_CHILD = "user_data"

        fun getInstance(
            context: Context,
            apiService: ApiService
        ) = MainRepository(context, apiService)
    }
}