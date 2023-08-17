package com.example.ecommerceappmvvm.ui.adapters

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.ecommerceappmvvm.databinding.CartProductItemBinding
import com.example.ecommerceappmvvm.helper.getProductPrice
import com.example.ecommerceappmvvm.model.data_class.CartProduct


class CartProductAdapter : RecyclerView.Adapter<CartProductAdapter.CartProductViewHolder>() {

    var mOnItemClick : ( (CartProduct) -> Unit )? = null
    var onPlusClick : ((CartProduct) -> Unit)? = null
    var onMinusClick : ((CartProduct) -> Unit)? = null

    val diffUtil = object : DiffUtil.ItemCallback<CartProduct>(){

        override fun areItemsTheSame(oldItem: CartProduct, newItem: CartProduct): Boolean {
            return oldItem.product.id == newItem.product.id
        }

        override fun areContentsTheSame(oldItem: CartProduct, newItem: CartProduct): Boolean {
            return oldItem == newItem
        }

    }

    val diff = AsyncListDiffer(this , diffUtil)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartProductViewHolder {
        return CartProductViewHolder(
            CartProductItemBinding.inflate(LayoutInflater.from(parent.context) , parent , false)
        )
    }

    override fun onBindViewHolder(holder: CartProductViewHolder, position: Int) {
        val cartProduct = diff.currentList[position]
        holder.bind(cartProduct)
        holder.setOnClickListener(cartProduct)
        holder.setOnMinusClick(cartProduct)
        holder.setOnPlusClick(cartProduct)
    }

    override fun getItemCount() = diff.currentList.size

    inner class CartProductViewHolder(private val mBinding : CartProductItemBinding) : RecyclerView.ViewHolder(mBinding.root) {
        fun bind(cartProduct: CartProduct){
            mBinding.apply {
                Glide.with(itemView).load(cartProduct.product.images[0]).into(imageCartProduct)

                tvProductCartName.text = cartProduct.product.name

                val finalPrice = cartProduct.product.offerPercentage.getProductPrice(cartProduct.product.price)

                tvProductCartPrice.text = "$ ${String.format("%.2f" , finalPrice)}"

                imageCartProductColor.setImageDrawable(ColorDrawable(cartProduct.selectedColor?: Color.TRANSPARENT))

                tvCartProductSize.text = cartProduct.selectedSize?:"".also {
                    imageCartProductSize.setImageDrawable(ColorDrawable(Color.TRANSPARENT))
                }

                tvCartProductQuantity.text = cartProduct.quantity.toString()
            }
        }

        fun setOnClickListener(cartProduct: CartProduct){
            itemView.setOnClickListener {
                mOnItemClick?.invoke(cartProduct)
            }
        }
        fun setOnPlusClick(cartProduct: CartProduct){
            mBinding.imagePlus.setOnClickListener {
                onPlusClick?.invoke(cartProduct)
            }
        }

        fun setOnMinusClick(cartProduct: CartProduct){
            mBinding.imageMinus.setOnClickListener {
                onMinusClick?.invoke(cartProduct)
            }
        }
    }
}