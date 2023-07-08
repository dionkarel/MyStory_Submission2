package com.example.mystory.ui.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mystory.R
import com.example.mystory.databinding.ActivityMainBinding
import com.example.mystory.ui.adapter.MyStoryAdapter
import com.example.mystory.ui.viewmodel.MainViewModel
import com.example.mystory.util.ViewModelFactory

class MainActivity : AppCompatActivity() {

    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MainViewModel by viewModels {
        ViewModelFactory.getInstance(this)
    }

    private val adapter = MyStoryAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val layoutManager = LinearLayoutManager(this)
        binding.apply {
            rvStory.layoutManager = layoutManager
            rvStory.setHasFixedSize(true)
            rvStory.adapter = adapter
        }

        setClick()
        setupObserver()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {

            R.id.action_logout -> {
                viewModel.userLogout()
                val intent = Intent(this@MainActivity, HomeActivity::class.java)
                startActivity(intent)
                finish()
            }

            R.id.action_maps -> {
                val intent = Intent(this@MainActivity, MapsActivity::class.java)
                startActivity(intent)
                finish()
            }

        }
        return super.onOptionsItemSelected(item)
    }

    private fun setClick() {
        binding.swipeLayout.setOnRefreshListener {
            finish()
            overridePendingTransition(0, 0)
            startActivity(intent)
            overridePendingTransition(0, 0)
            binding.swipeLayout.isRefreshing = false
        }

        binding.fbAddStory.setOnClickListener {
            startActivity(Intent(this@MainActivity, AddStoryActivity::class.java))
        }
    }

    private fun setupObserver() {

        viewModel.getUserToken().observe(
            this@MainActivity
        ) { session ->
            if (session.token == "") {
                val intent = Intent(
                    this@MainActivity,
                    HomeActivity::class.java
                )
                startActivity(intent)
                finish()
            } else {
                getStoryList(session.token)
            }
        }
    }

    private fun getStoryList(token: String) {
        showLoading(true)
        viewModel.getStories(token).observe(
            this@MainActivity
        ) {
            showLoading(false)
            adapter.submitData(lifecycle, it)
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

}