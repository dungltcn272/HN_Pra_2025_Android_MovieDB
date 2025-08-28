package com.sun.moviedb.screen.splash

import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.view.animation.AnimationUtils
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.sun.moviedb.R
import com.sun.moviedb.screen.MainActivity
import com.sun.moviedb.screen.login.LoginActivity
import com.sun.moviedb.utils.session.UserSession

class SplashActivity : AppCompatActivity() {
    var logoText: TextView? = null
    private var mediaPlayer: MediaPlayer? = null

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_splash)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, 0, systemBars.right, 0)
            insets
        }

        auth = FirebaseAuth.getInstance()

        logoText = findViewById(R.id.logoText)
        logoText?.let { it ->
            val fadeInAnimation = AnimationUtils.loadAnimation(this, R.anim.fade_in)
            it.startAnimation(fadeInAnimation)
        }

        try {
            mediaPlayer = MediaPlayer.create(this, R.raw.intro)
            mediaPlayer?.setOnCompletionListener {
                var intent: Intent?
                if (auth.currentUser == null) {
                    intent = Intent(this, LoginActivity::class.java)
                } else {
                    UserSession.updateSession(auth.currentUser!!)
                    Log.e("check", auth.currentUser!!.email.toString())
                    intent = Intent(this, MainActivity::class.java)
                }
                startActivity(intent)
                finish()
            }
            mediaPlayer?.start()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        releaseMediaPlayer()
    }

    private fun releaseMediaPlayer() {
        mediaPlayer?.release()
        mediaPlayer = null
    }
}
