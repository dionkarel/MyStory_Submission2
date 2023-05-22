package com.example.mystory.ui.activity

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.example.mystory.databinding.ActivityHomeBinding

class HomeActivity : AppCompatActivity() {

    private var _binding: ActivityHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        setAnimation()

        binding.btLogin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }

        binding.btRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

    }

    private fun setAnimation() {

        ObjectAnimator.ofFloat(binding.ivLogo, View.TRANSLATION_X, -30f, 30f).apply {

            duration = 4000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE

        }.start()

        val mulai = ObjectAnimator.ofFloat(binding.tvMulai, View.ALPHA, 1f)
            .setDuration(500)
        val cerita = ObjectAnimator.ofFloat(binding.tvCerita, View.ALPHA, 1f)
            .setDuration(500)
        val login = ObjectAnimator.ofFloat(binding.btLogin, View.ALPHA, 1f)
            .setDuration(500)
        val register = ObjectAnimator.ofFloat(binding.btRegister, View.ALPHA, 1f)
            .setDuration(500)

        val together = AnimatorSet().apply {
            playTogether(login, register)
        }

        AnimatorSet().apply {
            playSequentially(mulai, cerita, together)
            start()
        }
    }

}