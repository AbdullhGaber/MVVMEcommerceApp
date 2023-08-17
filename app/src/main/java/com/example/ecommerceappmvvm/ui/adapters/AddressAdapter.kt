package com.example.ecommerceappmvvm.ui.adapters

import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.ecommerceappmvvm.R
import com.example.ecommerceappmvvm.databinding.AddressRvItemBinding
import com.example.ecommerceappmvvm.model.data_class.Address

class AddressAdapter : RecyclerView.Adapter<AddressAdapter.AddressViewHolder>() {
    var onItemClick : ((Address?) -> Unit)? = null
    var mSelectedAddressIndex : Int = -1

    private val differUtil = object : DiffUtil.ItemCallback<Address>(){
        override fun areItemsTheSame(oldItem: Address, newItem: Address): Boolean {
            return oldItem.addressTitle == newItem.addressTitle
        }

        override fun areContentsTheSame(oldItem: Address, newItem: Address) = oldItem == newItem

    }

    val differ = AsyncListDiffer(this , differUtil)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddressAdapter.AddressViewHolder {
       return AddressViewHolder(
           AddressRvItemBinding.inflate(LayoutInflater.from(parent.context) , parent , false)
       )
    }

    override fun onBindViewHolder(holder: AddressAdapter.AddressViewHolder, position: Int) {
       val address = differ.currentList[position]

       holder.bind(address)
       holder.setOnViewHolderClickListener(address)

    }

    override fun getItemCount() = differ.currentList.size

    inner class AddressViewHolder(private var mBinding : AddressRvItemBinding) : ViewHolder(mBinding.root){
        private var mIsCurrentSelected = false

        fun bind(address : Address){
            mIsCurrentSelected = false
            mBinding.buttonAddress.background = ColorDrawable(itemView.context.resources.getColor(R.color.g_white))
            mBinding.buttonAddress.text = address.addressTitle
        }

        fun setOnViewHolderClickListener(address : Address){
            mBinding.buttonAddress.setOnClickListener {
                if(mIsCurrentSelected) {
                    it.background = ColorDrawable(itemView.context.resources.getColor(R.color.g_white))
                    mIsCurrentSelected = false
                    mSelectedAddressIndex = -1
                    onItemClick?.invoke(null)
                    return@setOnClickListener
                }
                else {
                    if(mSelectedAddressIndex >= 0)
                        notifyItemChanged(mSelectedAddressIndex) // uncheck selected address

                    it.background = ColorDrawable(itemView.context.resources.getColor(R.color.g_blue))
                    mIsCurrentSelected = true
                    mSelectedAddressIndex = adapterPosition
                }

                onItemClick?.invoke(address)
            }
        }
    }
}