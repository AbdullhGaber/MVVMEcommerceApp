package com.example.ecommerceappmvvm.ui.adapters

import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.ecommerceappmvvm.R
import com.example.ecommerceappmvvm.databinding.OrderItemBinding
import com.example.ecommerceappmvvm.model.data_class.order.Order
import com.example.ecommerceappmvvm.model.data_class.order.OrderStatus
import com.example.ecommerceappmvvm.model.data_class.order.getOrderStatus

class AllOrdersRVAdapter : RecyclerView.Adapter<AllOrdersRVAdapter.OrderViewHolder>() {
    var onItemClick : ((Order) -> Unit)? = null

    private val differUtil = object : DiffUtil.ItemCallback<Order>(){
        override fun areItemsTheSame(oldItem: Order, newItem: Order): Boolean {
            return oldItem.products == newItem.products
        }

        override fun areContentsTheSame(oldItem: Order, newItem: Order): Boolean {
            return oldItem == newItem
        }

    }

    val differ = AsyncListDiffer(this , differUtil)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
       return OrderViewHolder(
            OrderItemBinding.inflate(LayoutInflater.from(parent.context) , parent , false)
        )
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        val order = differ.currentList[position]
        holder.bind(order)
        holder.onItemClick(order)
    }

    override fun getItemCount() = differ.currentList.size

    inner class OrderViewHolder(private val mBinding : OrderItemBinding) : ViewHolder(mBinding.root){
        fun bind(order : Order){
            mBinding.apply {
                tvOrderId.text = order.orderId.toString()
                tvOrderDate.text = order.date
                val resources = itemView.resources

               val colorDrawable =  when(getOrderStatus(order.orderStatus)){
                   is OrderStatus.Ordered -> {
                         ColorDrawable(resources.getColor(R.color.g_orange_yellow))
                   }

                   is OrderStatus.Confirmed -> {
                       ColorDrawable(resources.getColor(R.color.g_green))
                   }

                   is OrderStatus.Delivered -> {
                       ColorDrawable(resources.getColor(R.color.g_green))
                   }

                   is OrderStatus.Shipped -> {
                       ColorDrawable(resources.getColor(R.color.g_green))
                   }

                   is OrderStatus.Canceled -> {
                       ColorDrawable(resources.getColor(R.color.g_red))
                   }

                   is OrderStatus.Returned -> {
                       ColorDrawable(resources.getColor(R.color.g_red))
                   }
                }

                imageOrderState.setImageDrawable(colorDrawable)
            }
        }
        fun onItemClick(order : Order){
            itemView.setOnClickListener {
                onItemClick?.invoke(order)
            }
        }
    }
}