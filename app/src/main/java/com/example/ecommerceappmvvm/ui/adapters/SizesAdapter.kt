package com.example.ecommerceappmvvm.ui.adapters


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.ecommerceappmvvm.databinding.SizeRvItemBinding

class SizesAdapter : RecyclerView.Adapter<SizesAdapter.SizesViewHolder>() {
    private var mSelectedPosition : Int = -1
    var onItemClick : ( (String) -> Unit)? = null

    private val diffUtil = object : DiffUtil.ItemCallback<String>(){
        override fun areItemsTheSame(oldItem: String, newItem: String) = (oldItem == newItem)
        override fun areContentsTheSame(oldItem: String, newItem: String) = (oldItem == newItem)
    }

    val differ = AsyncListDiffer(this , diffUtil)

    inner class SizesViewHolder(private val mBinding : SizeRvItemBinding) : RecyclerView.ViewHolder(mBinding.root){
        fun bind(size : String , position: Int) {
            mBinding.sizeTv.text = size

            if(mSelectedPosition == position){
                mBinding.imageShadow.visibility = View.VISIBLE
            }else{
                mBinding.imageShadow.visibility = View.GONE
            }
        }

        fun setOnClickListener(size : String){
            itemView.setOnClickListener {
                if(mSelectedPosition >= 0)
                    notifyItemChanged(mSelectedPosition)

                mSelectedPosition = adapterPosition
                notifyItemChanged(mSelectedPosition)

                onItemClick?.invoke(size)
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SizesViewHolder {
        return SizesViewHolder(
            SizeRvItemBinding.inflate(LayoutInflater.from(parent.context) , parent , false)
        )
    }

    override fun onBindViewHolder(holder: SizesViewHolder, position: Int) {
        val size = differ.currentList[position]
        holder.bind(size, position)
        holder.setOnClickListener(size)
    }

    override fun getItemCount() = differ.currentList.size
}