package com.example.ecommerceappmvvm.ui.adapters

import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.ecommerceappmvvm.databinding.ColorRvItemBinding

class ColorsAdapter : RecyclerView.Adapter<ColorsAdapter.ColorsViewHolder>() {
    private var mSelectedPosition : Int = -1
    var onItemClick : ( (Int) -> Unit)? = null

    private val diffUtil = object : DiffUtil.ItemCallback<Int>(){
        override fun areItemsTheSame(oldItem: Int, newItem: Int) = (oldItem == newItem)
        override fun areContentsTheSame(oldItem: Int, newItem: Int) = (oldItem == newItem)
    }

    val differ = AsyncListDiffer(this , diffUtil)

    inner class ColorsViewHolder(private val mBinding : ColorRvItemBinding) : RecyclerView.ViewHolder(mBinding.root){
        fun bind(color : Int , position: Int) {
            val colorDrawable = ColorDrawable(color)

            mBinding.imageColor.setImageDrawable(colorDrawable)

            if(mSelectedPosition == position){
                mBinding.imageShadow.visibility = View.VISIBLE
                mBinding.pickedImage.visibility = View.VISIBLE
            }else{
                mBinding.imageShadow.visibility = View.GONE
                mBinding.pickedImage.visibility = View.GONE
            }
        }

        fun setOnClickListener(color : Int){
            itemView.setOnClickListener {
                if(mSelectedPosition >= 0)
                    notifyItemChanged(mSelectedPosition)

                mSelectedPosition = adapterPosition
                notifyItemChanged(mSelectedPosition)

                onItemClick?.invoke(color)
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ColorsViewHolder {
        return ColorsViewHolder(
            ColorRvItemBinding.inflate(LayoutInflater.from(parent.context) , parent , false)
        )
    }

    override fun onBindViewHolder(holder: ColorsViewHolder, position: Int) {
        val color = differ.currentList[position]
        holder.bind(color, position)
        holder.setOnClickListener(color)
    }

    override fun getItemCount() = differ.currentList.size
}