package com.example.mystory.ui.adapter

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.mystory.databinding.StoryListBinding
import com.example.mystory.data.local.MyStoryModel
import com.example.mystory.ui.activity.DetailActivity
import com.example.mystory.ui.activity.DetailActivity.Companion.EXTRAS_STORY
import com.example.mystory.util.Util

class MyStoryAdapter :
    PagingDataAdapter<MyStoryModel, MyStoryAdapter.ListViewHolder>(DIFF_CALLBACK) {

    companion object {
        val DIFF_CALLBACK: DiffUtil.ItemCallback<MyStoryModel> =
            object : DiffUtil.ItemCallback<MyStoryModel>() {

                override fun areItemsTheSame(
                    oldItem: MyStoryModel,
                    newItem: MyStoryModel
                ): Boolean {
                    return oldItem.id == newItem.id
                }

                override fun areContentsTheSame(
                    oldItem: MyStoryModel,
                    newItem: MyStoryModel
                ): Boolean {
                    return oldItem == newItem
                }
            }
    }

    class ListViewHolder(private val binding: StoryListBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(story: MyStoryModel) {
            Glide.with(itemView.context)
                .load(story.photoUrl)
                .into(binding.ivStory)
            binding.apply {
                tvUsername.text = story.name
                tvDatepost.text = Util.dateFormat(story.createdAt)
                tvDetail.text = story.description
            }

            itemView.setOnClickListener {
                val intent = Intent(
                    itemView.context,
                    DetailActivity::class.java
                )
                intent.putExtra(EXTRAS_STORY, story.id)
                itemView.context.startActivity(
                    intent,
                    ActivityOptionsCompat.makeSceneTransitionAnimation(
                        itemView.context as Activity,
                        Pair(binding.ivStory, "photo"),
                        Pair(binding.tvUsername, "name"),
                        Pair(binding.tvDetail, "description")
                    ).toBundle()
                )
            }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val storyBinding =
            StoryListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ListViewHolder(storyBinding)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val item = getItem(position)
        if (item != null) {
            holder.bind(item)
        }
    }

}