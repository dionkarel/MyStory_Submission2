package com.example.mystory.ui.activity

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import com.example.mystory.databinding.ActivityRegisterBinding
import com.example.mystory.ui.viewmodel.RegisterViewModel
import com.example.mystory.util.Result
import com.example.mystory.util.ViewModelFactory

class RegisterActivity : AppCompatActivity() {

    private var _binding: ActivityRegisterBinding? = null
    private val binding get() = _binding!!

    private val viewModel: RegisterViewModel by viewModels {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        setAnimation()

        binding.btRegister.setOnClickListener {
            actionRegister()
        }

        binding.tvLogin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }

    }

    private fun setAnimation() {

        ObjectAnimator.ofFloat(binding.ivLogoRegister, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()

        val desc = ObjectAnimator.ofFloat(binding.tvDesc, View.ALPHA, 1f).setDuration(500)
        val tvUsername =
            ObjectAnimator.ofFloat(binding.tvRegUsername, View.ALPHA, 1f).setDuration(500)
        val edtUsername =
            ObjectAnimator.ofFloat(binding.edtRegUsername, View.ALPHA, 1f).setDuration(500)
        val tvEmail = ObjectAnimator.ofFloat(binding.tvRegEmail, View.ALPHA, 1f).setDuration(500)
        val edtEmail = ObjectAnimator.ofFloat(binding.edtRegEmail, View.ALPHA, 1f).setDuration(500)
        val tvPswd = ObjectAnimator.ofFloat(binding.tvRegPswd, View.ALPHA, 1f).setDuration(500)
        val edtPswd = ObjectAnimator.ofFloat(binding.edtRegPswd, View.ALPHA, 1f).setDuration(500)
        val btReg = ObjectAnimator.ofFloat(binding.btRegister, View.ALPHA, 1f).setDuration(500)

        val username = AnimatorSet().apply {
            playTogether(tvUsername, edtUsername)
        }

        val email = AnimatorSet().apply {
            playTogether(tvEmail, edtEmail)
        }

        val pswd = AnimatorSet().apply {
            playTogether(tvPswd, edtPswd)
        }

        AnimatorSet().apply {
            playSequentially(desc, username, email, pswd, btReg)
            start()
        }

    }


    private fun actionRegister() {
        binding.edtRegUsername.isErrorEnabled = false
        binding.edtRegEmail.isErrorEnabled = false

        val name = binding.edtRegUsername.editText?.text.toString()
        val email = binding.edtRegEmail.editText?.text.toString()
        val password = binding.edtRegPswd.editText?.text.toString()
        when {
            name.isEmpty() -> {
                Toast.makeText(this@RegisterActivity, "Masukan username anda", Toast.LENGTH_SHORT)
                    .show()
            }
            email.isEmpty() -> {
                Toast.makeText(this@RegisterActivity, "Masukan email anda", Toast.LENGTH_SHORT)
                    .show()
            }
            password.isEmpty() -> {
                Toast.makeText(this@RegisterActivity, "Masukan password anda", Toast.LENGTH_SHORT)
                    .show()
            }

            else -> {
                postRegister(name, email, password)
            }
        }
    }

    private fun postRegister(name: String, email: String, password: String) {
        viewModel.postRegister(name, email, password).observe(this@RegisterActivity) {
            if (it != null) {
                when (it) {

                    is Result.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                    }

                    is Result.Success -> {
                        binding.progressBar.visibility = View.GONE
                        AlertDialog.Builder(this@RegisterActivity).apply {
                            setTitle("Berhasil!")
                            setMessage("Kamu berhasil mendaftar. Segera Login untuk membagikan pengalamanmu!")
                            setPositiveButton("Lanjut") { _, _ ->
                                finish()
                            }
                            create()
                            show()
                        }
                    }

                    is Result.Error -> {
                        binding.progressBar.visibility = View.GONE
                        Toast.makeText(
                            this@RegisterActivity,
                            it.error,
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                }
            }
        }
    }

}