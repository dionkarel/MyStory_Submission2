package com.example.mystory.ui.activity

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.mystory.databinding.ActivityLoginBinding
import com.example.mystory.data.response.LoginResult
import com.example.mystory.ui.viewmodel.LoginViewModel
import com.example.mystory.util.Result
import com.example.mystory.util.ViewModelFactory

class LoginActivity : AppCompatActivity() {

    private var _binding: ActivityLoginBinding? = null
    private val binding get() = _binding!!

    private val viewModel: LoginViewModel by viewModels {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        setAnimation()
        setClick()

    }

    private fun setAnimation() {
        ObjectAnimator.ofFloat(binding.ivLogoLogin, View.TRANSLATION_X, -30F, 30F).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()

        val tvEmail = ObjectAnimator.ofFloat(binding.tvLogEmail, View.ALPHA, 1f).setDuration(500)
        val edtEmail = ObjectAnimator.ofFloat(binding.edtLogEmail, View.ALPHA, 1f).setDuration(500)
        val tvPswd = ObjectAnimator.ofFloat(binding.tvLogPswd, View.ALPHA, 1f).setDuration(500)
        val edtPswd = ObjectAnimator.ofFloat(binding.edtLogPswd, View.ALPHA, 1f).setDuration(500)
        val btLogin = ObjectAnimator.ofFloat(binding.btLogin, View.ALPHA, 1f).setDuration(500)

        AnimatorSet().apply {
            playSequentially(tvEmail, edtEmail, tvPswd, edtPswd, btLogin)
            start()
        }
    }

    private fun setClick() {
        binding.btLogin.setOnClickListener {
            actionLogin()
        }

        binding.tvRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun actionLogin() {
        binding.btLogin.setOnClickListener {
            val email = binding.edtLogEmail.editText?.text.toString()
            val password = binding.edtLogPswd.editText?.text.toString()
            when {

                email.isEmpty() -> {
                    Toast.makeText(this@LoginActivity, "Masukan email", Toast.LENGTH_SHORT)
                        .show()
                }

                password.isEmpty() -> {
                    Toast.makeText(this@LoginActivity, "Masukan password", Toast.LENGTH_SHORT)
                        .show()
                }

                else -> {
                    postLogin(email, password)
                }
            }
        }
    }

    private fun postLogin(email: String, password: String) {
        viewModel.postLogin(email, password).observe(this@LoginActivity) {
            if (it != null) {
                when (it) {

                    is Result.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                    }

                    is Result.Success -> {
                        binding.progressBar.visibility = View.GONE
                        val user = it.data
                        userSave(user)

                        AlertDialog.Builder(this@LoginActivity).apply {
                            setTitle("Selamat datang!")
                            setMessage("Mulai bagi pengalamanmu!")
                            setPositiveButton("Lanjut") { _, _ ->
                                val intent = Intent(
                                    this@LoginActivity,
                                    MainActivity::class.java
                                )
                                intent.flags =
                                    Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                                startActivity(intent)
                                finish()
                            }
                            create()
                            show()
                        }
                    }

                    is Result.Error -> {
                        binding.progressBar.visibility = View.GONE
                        Toast.makeText(this@LoginActivity, it.error, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun userSave(user: LoginResult) {
        viewModel.userSave(user)
    }

}