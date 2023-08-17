package com.example.ecommerceappmvvm.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.ecommerceappmvvm.databinding.SpecialRvItemBinding
import com.example.ecommerceappmvvm.model.data_class.Product

class SpecialProductsRVAdapter : RecyclerView.Adapter<SpecialProductsRVAdapter.ProductsViewHolder>() {

    var mOnItemClick : ( (Product) -> Unit )?  = null

    val diffUtil = object : DiffUtil.ItemCallback<Product>(){

        override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem == newItem
        }

    }

    val diff = AsyncListDiffer(this , diffUtil)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductsViewHolder {
        return ProductsViewHolder(
            SpecialRvItemBinding.inflate(LayoutInflater.from(parent.context) , parent , false)
        )
    }

    override fun onBindViewHolder(holder: ProductsViewHolder, position: Int) {
        val product = diff.currentList[position]
        holder.bind(product)
        holder.setOnClickListener(product)
    }

    override fun getItemCount() = diff.currentList.size

    inner class ProductsViewHolder(private val mBinding : SpecialRvItemBinding ) : RecyclerView.ViewHolder(mBinding.root) {
        fun bind(product: Product){
            mBinding.apply {
                Glide.with(itemView).load(product.images[0]).into(ivProductAd)
                tvAdName.text = product.name
                tvAdPrice.text = product.price.toString()
            }
        }

        fun setOnClickListener(product: Product){
            itemView.setOnClickListener {
                mOnItemClick?.invoke(product)
            }
        }
    }
}