package com.tomas.vocabquiztest

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.GoogleApi
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.games.Games
import com.google.android.gms.games.Games.getLeaderboardsClient
import kotlinx.android.synthetic.main.activity_game_over.*

class GameOverActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game_over)

        val points = intent.getIntExtra("Points", 0)

        pointsView.text = "$points"

        restartButton.setOnClickListener {
            val intent = Intent(this@GameOverActivity, PlayActivity::class.java)
            startActivity(intent)
            finish()
        }

        if(GoogleSignIn.getLastSignedInAccount(this) != null) {
            getLeaderboardsClient(this, GoogleSignIn.getLastSignedInAccount(this)!!)
                .submitScore(getString(R.string.leaderboard_id), points.toLong())
        }

        main_menu_button.setOnClickListener {
            val intent = Intent(this@GameOverActivity, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

    }
}

