package com.ptit.theeyes

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.ptit.theeyes.databinding.ActivityLoginBinding
import com.ptit.theeyes.utils.viewBinding
import timber.log.Timber

class LoginActivity : AppCompatActivity() {
    private val binding by viewBinding(ActivityLoginBinding::inflate)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val auth = FirebaseAuth.getInstance()
        auth.signOut()
        if(auth.currentUser != null){
            goToMainActivity()
        } else{
            activateAuthUI()
        }
    }

    private fun activateAuthUI() {
        val provider = arrayListOf(
            AuthUI.IdpConfig.EmailBuilder().build(),
            AuthUI.IdpConfig.GoogleBuilder().build(),
            AuthUI.IdpConfig.FacebookBuilder().build(),
            AuthUI.IdpConfig.GitHubBuilder().build()
        )

        startActivityForResult(
            AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(provider)
                .build(),
            RC_SIGN_IN
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == RC_SIGN_IN){
            val response = IdpResponse.fromResultIntent(data)

            if(resultCode == RESULT_OK){
                val user = FirebaseAuth.getInstance().currentUser
                Toast.makeText(this, user?.email, Toast.LENGTH_LONG).show()
                goToMainActivity()
            } else{
                if(response != null) {
                    Toast.makeText(this, response.error?.message, Toast.LENGTH_LONG).show()
                    Timber.e(response.error.toString())
                }
                binding.loadingProgress.visibility = View.GONE
                binding.buttonRetry.visibility = View.VISIBLE
                Snackbar.make(binding.root,
                    "Login Failed! Please try again",
                    Snackbar.LENGTH_LONG).show()
            }
        }
    }

    private fun goToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    companion object{
        const val RC_SIGN_IN = 54321
    }
}