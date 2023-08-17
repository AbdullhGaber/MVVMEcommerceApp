package com.example.ecommerceappmvvm.ui.adapters

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.ecommerceappmvvm.databinding.ProductRvItemBinding
import com.example.ecommerceappmvvm.helper.getProductPrice
import com.example.ecommerceappmvvm.model.data_class.Product

class BestProductsAdapter : RecyclerView.Adapter<BestProductsAdapter.BestProductsViewHolder>() {
    var mOnItemClick : ( (Product) -> Unit )?  = null

    private val differUtilCallback = object : DiffUtil.ItemCallback<Product>(){
        override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem == newItem
        }

    }

    val differ = AsyncListDiffer(this , differUtilCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BestProductsViewHolder {
        return BestProductsViewHolder(
            ProductRvItemBinding.inflate(LayoutInflater.from(parent.context) , parent ,false)
        )
    }

    override fun onBindViewHolder(holder: BestProductsViewHolder, position: Int) {
        val product = differ.currentList[position]
        holder.bind(product)
        holder.setOnClickListener(product)
    }

    override fun getItemCount() = differ.currentList.size

    inner class BestProductsViewHolder(private val mBinding : ProductRvItemBinding) : RecyclerView.ViewHolder(mBinding.root) {
        fun bind(product : Product){
            mBinding.apply {
                Glide.with(itemView).load(product.images[0]).into(imgProduct)
                tvName.text = product.name
                    val finalPrice = product.offerPercentage.getProductPrice(product.price)
                    tvNewPrice.text = "$ ${String.format("%.2f" , finalPrice)}"
                    tvPrice.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG


                if(product.offerPercentage == null)
                    tvNewPrice.visibility = View.GONE

                tvPrice.text = "$ ${ product.price }"
            }
        }

        fun setOnClickListener(product: Product){
            itemView.setOnClickListener {
                mOnItemClick?.invoke(product)
            }
        }
    }
}