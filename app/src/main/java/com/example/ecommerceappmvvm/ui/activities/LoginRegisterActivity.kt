package com.example.ecommerceappmvvm.ui.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.ecommerceappmvvm.databinding.ActivityLoginRegisterBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginRegisterActivity : AppCompatActivity() {
    private lateinit var mBinding : ActivityLoginRegisterBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityLoginRegisterBinding.inflate(layoutInflater)
        setContentView(mBinding.root)
    }

    override fun onBackPressed() {
        finishAffinity()
    }
}