package com.example.chatapp.ui.theme

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.runtime.currentCompositionErrors
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.chatapp.navigateTo
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import dagger.hilt.android.lifecycle.HiltViewModel
import data.Event
import data.USER_NODE
import data.UserData
import java.lang.Exception
import javax.inject.Inject
import kotlin.math.sign

@HiltViewModel
class LCViewModel @Inject constructor(
    val auth: FirebaseAuth,
    var db : FirebaseFirestore
) : ViewModel() {



    var inProgress = mutableStateOf(false)
    var signIn = mutableStateOf(false)
    val eventMutableState = mutableStateOf<Event<String>?>(null)
    val userData = mutableStateOf<UserData?>(null)
    init {

        val currentUser = auth.currentUser
        signIn.value = currentUser !=null
        currentUser?.uid?.let{
            getUserData(it)
        }
    }


    fun signUp(name: String, number: String, email: String, password: String) {
        inProgress.value = true
        if(name.isEmpty() or number.isEmpty() or email.isEmpty()or password.isEmpty()){
            handleException(customMessage = "Please fill all the fields")
            return
        }

        inProgress.value = true
        db.collection(USER_NODE).whereEqualTo("number",number).get().addOnSuccessListener {
            if(it.isEmpty){
                auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {
                    if (it.isSuccessful) {
                        signIn.value = true
                        createOrUpdateProfile(name, number)
                        Log.d("TAG", "signUp: User Logged In")
                    }
                    else{
                        handleException(it.exception, customMessage = "Sign Up Failed")
                    }
                }
            }else{
                handleException(customMessage = "Number Already Exists")
                inProgress.value = false
            }
        }

    }

    fun createOrUpdateProfile(name:String?=null, number:String?=null, imageurl :String?=null){
        var uid = auth.currentUser?.uid
        val userData = UserData(
            userId = uid,
            name = name?:userData.value?.name,
            number = number?:userData.value?.number,
            imageurl = imageurl?:userData.value?.imageurl

        )
        uid?.let{
            inProgress.value = true
            db.collection(USER_NODE).document(uid).get().addOnSuccessListener {
                if(it.exists()){

                }else{
                    db.collection(USER_NODE).document(uid).set(userData)
                    inProgress.value = false
                    getUserData(uid)
                }
            }
                .addOnFailureListener {
                    handleException(it,"Cannot Retrieve User")
                }
        }
}

    private fun getUserData(uid:String) {
        inProgress.value = true
        db.collection(USER_NODE).document(uid).addSnapshotListener{
            value,error->
            if(error!=null){
                handleException(error,"Cannot Retrieve User")
            }
            if(value!=null){
                var user = value.toObject<UserData>()
                userData.value = user
                inProgress.value = false
            }
        }
    }

    fun handleException(exception: Exception?=null,customMessage :String = ""){
        Log.d("LiveChatApp","live chat exception:",exception)
        exception?.printStackTrace()
        val errorMsg = exception?.localizedMessage?:""
        val message = if(customMessage.isNullOrEmpty()) errorMsg else customMessage
        eventMutableState.value = Event(message)
        inProgress.value = false
    }
}
