package com.anmerris.pwic.ui.home


import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.anmerris.pwic.GlideApp
import com.anmerris.pwic.databinding.PhotoHeaderItemBinding
import com.anmerris.pwic.databinding.PhotoItemBinding
import com.anmerris.pwic.utils.MainThreadExecutor
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import java.util.concurrent.Executor
import java.util.concurrent.Executors


class PhotoListAdapter() : RecyclerView.Adapter<PhotoListAdapter.ItemViewHolder>() {
    private var mItems: List<TweetPhotoItem> = mutableListOf()
    private val diffExecutor: Executor = Executors.newSingleThreadExecutor()
    private val mainThreadExecutor = MainThreadExecutor()

    init {
        setHasStableIds(true)
    }

    fun updateContents(newItems: List<TweetPhotoItem>) {
        diffExecutor.execute {
            val result = DiffUtil.calculateDiff(ItemDiffCallback(mItems, newItems))
            mainThreadExecutor.execute {
                mItems = newItems
                result.dispatchUpdatesTo(this)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)

        val type = TweetPhotoItem.Type.values()[viewType]

        return when (type) {
            TweetPhotoItem.Type.PHOTO -> {
                val binding = PhotoItemBinding.inflate(layoutInflater, parent, false)
                ItemViewHolder.PhotoHolder(binding)
            }
            TweetPhotoItem.Type.HEADER -> {
                val binding = PhotoHeaderItemBinding.inflate(layoutInflater, parent, false)
                ItemViewHolder.PhotoHeaderHolder(binding)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return mItems[position].type.ordinal
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = mItems[position]
        holder.bind(item)
        if (item.type == TweetPhotoItem.Type.PHOTO) {
            val photoHolder = holder as ItemViewHolder.PhotoHolder
            GlideApp.with(holder.itemView.context)
                    .load(item.tweetMedia.media.url + ":thumb")
                    .centerCrop()
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(photoHolder.binding.imageView)
        }
    }

    override fun getItemCount(): Int {
        return mItems.size
    }

    override fun getItemId(position: Int): Long {
        return mItems[position].id
    }

    sealed class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        abstract fun bind(item: TweetPhotoItem)

        class PhotoHeaderHolder(val binding: PhotoHeaderItemBinding) : ItemViewHolder(binding.root) {
            override fun bind(item: TweetPhotoItem) {
                binding.item = item
                binding.executePendingBindings()
            }
        }

        class PhotoHolder(val binding: PhotoItemBinding) : ItemViewHolder(binding.root) {
            override fun bind(item: TweetPhotoItem) {
                binding.item = item
                binding.executePendingBindings()
            }
        }
    }

    class ItemDiffCallback(val oldList: List<TweetPhotoItem>, val newList: List<TweetPhotoItem>) : DiffUtil.Callback() {

        override fun getOldListSize(): Int {
            return oldList.size
        }

        override fun getNewListSize(): Int {
            return newList.size
        }

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            val old = oldList[oldItemPosition];
            val new = newList[newItemPosition]
            if (old.type != new.type) {
                return false
            }

            return old.id == new.id
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition] == newList[newItemPosition]
        }
    }
}
