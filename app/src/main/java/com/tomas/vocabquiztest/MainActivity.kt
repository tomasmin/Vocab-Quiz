package com.tomas.vocabquiztest

import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.games.Games
import com.google.android.gms.games.PlayersClient
import com.google.android.gms.auth.api.Auth.*


class MainActivity : FragmentActivity(), MainMenuFragment.Listener,
    MainMenuFragment.OnFragmentInteractionListener {
    override fun onFragmentInteraction(uri: Uri) {

    }

    private var mGoogleSignInClient: GoogleSignInClient? = null
    private var mPlayersClient: PlayersClient? = null

    private val mMainMenuFragment = MainMenuFragment()

    private val RC_SIGN_IN = 9001
    private val RC_LEADERBOARD_UI = 9004

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mGoogleSignInClient = GoogleSignIn.getClient(
            this,
            GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_GAMES_SIGN_IN)
                .build()
        )

        mMainMenuFragment.setListener(this)

        supportFragmentManager.beginTransaction().add(
            R.id.fragment_container,
            mMainMenuFragment
        ).commit()
    }

    private fun isSignedIn(): Boolean {
        return GoogleSignIn.getLastSignedInAccount(this) != null
    }

    private fun signInSilently() {
        val signInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_GAMES_SIGN_IN)
            .build()
        val account = GoogleSignIn.getLastSignedInAccount(this)
        if (GoogleSignIn.hasPermissions(account, *signInOptions.scopeArray)) {
            // Already signed in.
            // The signed in account is stored in the 'account' variable.
            if (account != null) {
                onConnected(account)
            }
        } else {
            // Haven't been signed-in before. Try the silent sign-in first.
            val signInClient = GoogleSignIn.getClient(this, signInOptions)
            signInClient
                .silentSignIn()
                .addOnCompleteListener(
                    this
                ) { task ->
                    if (task.isSuccessful) {
                        // The signed in account is stored in the task's result.
                        val signedInAccount = task.result
                        if (signedInAccount != null) {
                            onConnected(signedInAccount)
                        }
                    } else {
                        // Player will need to sign-in explicitly using via UI.
                        // See [sign-in best practices](http://developers.google.com/games/services/checklist) for guidance on how and when to implement Interactive Sign-in,
                        // and [Performing Interactive Sign-in](http://developers.google.com/games/services/android/signin#performing_interactive_sign-in) for details on how to implement
                        // Interactive Sign-in.
                    }
                }
        }
    }

    private fun startSignInIntent() {
        val signInClient = GoogleSignIn.getClient(
            this,
            GoogleSignInOptions.DEFAULT_GAMES_SIGN_IN
        )
        val intent = signInClient.signInIntent
        startActivityForResult(intent, RC_SIGN_IN)
    }

    override fun onResume() {
        super.onResume()

        // Since the state of the signed in user can change when the activity is not active
        // it is recommended to try and sign in silently from when the app resumes.
        signInSilently()
    }

    private fun signOut() {

        if (!isSignedIn()) {
            return
        }

        mGoogleSignInClient!!.signOut().addOnCompleteListener(
            this
        ) {
            onDisconnected()
        }
    }

    private fun onConnected(googleSignInAccount: GoogleSignInAccount) {

        mPlayersClient = Games.getPlayersClient(this, googleSignInAccount)

        // Show sign-out button on main menu
        mMainMenuFragment.setShowSignInButton(false)

        // Set the greeting appropriately on main menu
        mPlayersClient!!.currentPlayer
            .addOnCompleteListener { task ->
                val displayName: String
                if (task.isSuccessful) {
                    displayName = task.result!!.displayName
                } else {
                    val e = task.exception
                    if (e != null) {
                        handleException(e, getString(R.string.players_exception))
                    }
                    displayName = "???"
                }
            }

    }

    private fun onDisconnected() {
        mPlayersClient = null

        // Show sign-in button on main menu
        mMainMenuFragment.setShowSignInButton(true)

    }

    override fun onSignInButtonClicked() {
        startSignInIntent()
    }

    override fun onSignOutButtonClicked() {
        signOut()
    }

    private fun handleException(e: Exception, details: String) {
        var status = 0

        if (e is ApiException) {
            status = e.statusCode
        }

        val message = getString(R.string.status_exception_error, details, status, e)

        AlertDialog.Builder(this@MainActivity)
            .setMessage(message)
            .setNeutralButton(R.string.ok, null)
            .show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val result = GoogleSignInApi.getSignInResultFromIntent(data)
            if (result.isSuccess) {
                // The signed in account is stored in the result.
                val signedInAccount = result.signInAccount

                if (signedInAccount != null) {
                    onConnected(signedInAccount)
                }
            } else {
                var message = result.status.statusMessage
                if (message == null || message.isEmpty()) {
                    message = getString(R.string.signin_other_error)
                }
                AlertDialog.Builder(this).setMessage(message)
                    .setNeutralButton(android.R.string.ok, null).show()
            }
        }
    }

    override fun onShowLeaderboardsRequested() {
        Games.getLeaderboardsClient(this, GoogleSignIn.getLastSignedInAccount(this)!!)
            .getLeaderboardIntent(getString(R.string.leaderboard_id))
            .addOnSuccessListener { intent -> startActivityForResult(intent, RC_LEADERBOARD_UI) }
    }
}
