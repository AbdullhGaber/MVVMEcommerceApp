package com.example.ecommerceappmvvm.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.ecommerceappmvvm.databinding.ViewPagerImageItemBinding

class ViewPagerImageAdapter : RecyclerView.Adapter<ViewPagerImageAdapter.ViewPagerHolder>() {

    private val diffUtil = object : DiffUtil.ItemCallback<String>(){
        override fun areItemsTheSame(oldItem: String, newItem: String) = (oldItem == newItem)
        override fun areContentsTheSame(oldItem: String, newItem: String) = (oldItem == newItem)
    }

    val differ = AsyncListDiffer(this , diffUtil)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewPagerHolder {
        return ViewPagerHolder(
            ViewPagerImageItemBinding.inflate(LayoutInflater.from(parent.context) , parent , false)
        )
    }

    override fun onBindViewHolder(holder: ViewPagerHolder, position: Int) {
        holder.bind(differ.currentList[position])
    }

    override fun getItemCount() = differ.currentList.size

    inner class ViewPagerHolder(val mBinding : ViewPagerImageItemBinding ) : RecyclerView.ViewHolder(
        mBinding.root
    ){
        fun bind(imagePath : String ){
            Glide.with(itemView).load(imagePath).into(mBinding.productDetailsIv)
        }
    }
}