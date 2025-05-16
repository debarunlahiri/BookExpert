package com.debarunlahiri.bookexpert

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.debarunlahiri.bookexpert.data.db.AppDatabase
import com.debarunlahiri.bookexpert.data.model.User
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun SignInScreen(navController: NavController) {
    val auth = FirebaseAuth.getInstance()
    val context = LocalContext.current

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        val data = result.data
        try {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            val account = task.getResult(ApiException::class.java)!!
            val credential = GoogleAuthProvider.getCredential(account.idToken!!, null)
            auth.signInWithCredential(credential)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val user = auth.currentUser
                        user?.let {
                            val dbUser = User(it.uid, it.displayName ?: "", it.email ?: "", it.photoUrl?.toString() ?: "")
                            CoroutineScope(Dispatchers.IO).launch {
                                AppDatabase.getDatabase(context).userDao().insertUser(dbUser)
                            }
                            navController.navigate("home") {
                                popUpTo("sign_in") { inclusive = true }
                            }
                        }
                    }
                }
        } catch (e: ApiException) {
            // Handle error
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(onClick = {
            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("WEB_CLIENT_ID") // Replace with your client ID
                .requestEmail()
                .build()
            val googleSignInClient = GoogleSignIn.getClient(context, gso)
            launcher.launch(googleSignInClient.signInIntent)
        }) {
            Text("Sign In with Google")
        }
    }
}