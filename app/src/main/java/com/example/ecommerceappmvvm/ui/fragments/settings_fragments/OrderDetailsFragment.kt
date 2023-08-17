package com.example.ecommerceappmvvm.ui.fragments.settings_fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ecommerceappmvvm.databinding.FragmentOrderDetailBinding
import com.example.ecommerceappmvvm.model.data_class.order.OrderStatus
import com.example.ecommerceappmvvm.model.data_class.order.getOrderStatus
import com.example.ecommerceappmvvm.ui.adapters.BillingProductAdapter
import com.example.ecommerceappmvvm.util.VerticalItemDecoration
import com.example.ecommerceappmvvm.util.closeFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OrderDetailsFragment : Fragment() {
    private lateinit var mBinding : FragmentOrderDetailBinding
    private val mBillingAdapter by lazy { BillingProductAdapter() }
    private val mNavArgs by navArgs<OrderDetailsFragmentArgs>()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentOrderDetailBinding.inflate(inflater , container , false)
        return  mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setUpOrderRVAdapter()
        bindUI()
        onCloseOrderDetailsIconClick()
    }

    private fun onCloseOrderDetailsIconClick() {
        mBinding.imageCloseOrder.setOnClickListener {
            closeFragment()
        }
    }

    private fun bindUI() {
        val order = mNavArgs.order

        mBinding.apply {
            tvOrderId.text ="Order #${order.orderId}"

            stepView.setSteps(
                mutableListOf(
                    OrderStatus.Ordered.status,
                    OrderStatus.Confirmed.status,
                    OrderStatus.Shipped.status,
                    OrderStatus.Delivered.status,
                )
            )

            val currentOrderStateIdx = when(getOrderStatus(order.orderStatus)){
                is OrderStatus.Ordered -> 0
                is OrderStatus.Confirmed -> 1
                is OrderStatus.Shipped -> 2
                is OrderStatus.Delivered -> 3
                else -> 0
            }

            stepView.go(currentOrderStateIdx,false)

            if(currentOrderStateIdx == 3)
                stepView.done(true)

            tvFullName.text = order.address.fullName
            tvAddress.text = "${order.address.street} ${order.address.city}"
            tvPhoneNumber.text = order.address.phone
            tvTotalPrice.text = order.totalPrice.toString()

            mBillingAdapter.differ.submitList(order.products)
        }
    }

    private fun setUpOrderRVAdapter() {
        mBinding.rvProducts.apply {
            adapter = mBillingAdapter
            layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL , false)
            addItemDecoration(VerticalItemDecoration())
        }
    }
}