package com.example.mystory.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.viewModels
import com.bumptech.glide.Glide
import com.example.mystory.databinding.ActivityDetailBinding
import com.example.mystory.ui.viewmodel.DetailViewModel
import com.example.mystory.util.Result
import com.example.mystory.util.Util
import com.example.mystory.util.ViewModelFactory

class DetailActivity : AppCompatActivity() {

    private var _binding: ActivityDetailBinding? = null
    private val binding get() = _binding!!

    private val viewModel: DetailViewModel by viewModels {
        ViewModelFactory.getInstance(this)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val story = intent.getStringExtra(EXTRAS_STORY).toString()
        setupAction(story)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setupAction(id: String) {
        viewModel.getDetailStories(id).observe(
            this@DetailActivity
        ) {
            if (it != null) {
                when (it) {

                    is Result.Loading -> {
                    }

                    is Result.Success -> {
                        Glide.with(applicationContext)
                            .load(it.data.photoUrl)
                            .into(binding.ivDetail)
                        binding.apply {
                            tvUsername.text = it.data.name
                            tvDetail.text = it.data.description
                            tvDatepost.text = Util.dateFormat(it.data.createdAt)
                        }
                    }

                    is Result.Error -> {
                        Toast.makeText(
                            this@DetailActivity,
                            it.error,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }

    companion object {
        const val EXTRAS_STORY = "extras_story"
    }
}