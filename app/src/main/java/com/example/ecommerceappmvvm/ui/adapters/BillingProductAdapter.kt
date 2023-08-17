package com.example.ecommerceappmvvm.ui.adapters

import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.example.ecommerceappmvvm.R
import com.example.ecommerceappmvvm.databinding.AddressRvItemBinding
import com.example.ecommerceappmvvm.databinding.BillingProductsRvItemBinding
import com.example.ecommerceappmvvm.helper.getProductPrice
import com.example.ecommerceappmvvm.model.data_class.Address
import com.example.ecommerceappmvvm.model.data_class.CartProduct

class BillingProductAdapter : RecyclerView.Adapter<BillingProductAdapter.BillingProductsViewHolder>() {
    var onItemClick : ((CartProduct) -> Unit)? = null

    private val differUtil = object : DiffUtil.ItemCallback<CartProduct>(){
        override fun areItemsTheSame(oldItem: CartProduct, newItem: CartProduct): Boolean {
            return oldItem.product == newItem.product
        }

        override fun areContentsTheSame(oldItem: CartProduct, newItem: CartProduct) = oldItem == newItem

    }

    val differ = AsyncListDiffer(this , differUtil)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BillingProductAdapter.BillingProductsViewHolder {
        return BillingProductsViewHolder(
            BillingProductsRvItemBinding.inflate(LayoutInflater.from(parent.context) , parent , false)
        )
    }

    override fun onBindViewHolder(holder: BillingProductAdapter.BillingProductsViewHolder, position: Int) {
        val billingProduct = differ.currentList[position]

        holder.bind(billingProduct)
        holder.setOnViewHolderClickListener(billingProduct)

    }

    override fun getItemCount() = differ.currentList.size

    inner class BillingProductsViewHolder(private var mBinding : BillingProductsRvItemBinding) : ViewHolder(mBinding.root){
        fun bind(billingProduct : CartProduct){
            mBinding.apply {
                Glide.with(itemView).load(billingProduct.product.images[0]).into(imageCartProduct)

                tvProductCartName.text = billingProduct.product.name
                tvBillingProductQuantity.text = billingProduct.quantity.toString()

                val finalPrice = billingProduct.product.offerPercentage.getProductPrice(billingProduct.product.price)

                tvProductCartPrice.text = "$ ${String.format("%.2f" , finalPrice)}"

            }
        }

        fun setOnViewHolderClickListener(billingProduct : CartProduct){
            itemView.setOnClickListener {
                onItemClick?.invoke(billingProduct)
            }
        }
    }
}