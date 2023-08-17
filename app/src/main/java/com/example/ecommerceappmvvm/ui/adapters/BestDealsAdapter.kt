package com.example.ecommerceappmvvm.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.ecommerceappmvvm.databinding.BestDealsRvItemBinding
import com.example.ecommerceappmvvm.model.data_class.Product

class BestDealsAdapter : RecyclerView.Adapter<BestDealsAdapter.BestDealsViewHolder>() {

    var mOnItemClick : ( (Product) -> Unit )? = null

    private val differUtilCallback = object : DiffUtil.ItemCallback<Product>(){
        override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem == newItem
        }

    }

    val differ = AsyncListDiffer(this , differUtilCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BestDealsViewHolder {
        return BestDealsViewHolder(
            BestDealsRvItemBinding.inflate(LayoutInflater.from(parent.context) , parent ,false)
        )
    }

    override fun onBindViewHolder(holder: BestDealsViewHolder, position: Int) {
        val product = differ.currentList[position]
        holder.bind(product)
        holder.setOnClickListener(product)
    }

    override fun getItemCount() = differ.currentList.size

    inner class BestDealsViewHolder(private val mBinding : BestDealsRvItemBinding) : RecyclerView.ViewHolder(mBinding.root) {
        fun bind(product : Product){
            mBinding.apply {
                Glide.with(itemView).load(product.images[0]).into(imgBestDeal)
                tvDealProductName.text = product.name

                product.offerPercentage?.let {
                    val remainingPercentage = 1f - it
                    val finalPrice = remainingPercentage * product.price
                    tvNewPrice.text = "$ ${String.format("%.2f" , finalPrice)}"
                }

                tvOldPrice.text = "$ ${ product.price }"
            }
        }

        fun setOnClickListener(product: Product){
            itemView.setOnClickListener {
                mOnItemClick?.invoke(product)
            }
        }
    }

}