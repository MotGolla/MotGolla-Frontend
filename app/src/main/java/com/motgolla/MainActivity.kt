package com.motgolla

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import com.motgolla.domain.record.view.ui.MemoViewModel
import com.motgolla.domain.record.view.ui.RecordViewModel
import com.motgolla.domain.record.view.ui.ShoppingRecordScreen

class MainActivity : ComponentActivity() {

    private val viewModel: RecordViewModel by viewModels()
    private val memoViewModel: MemoViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ShoppingRecordScreen(viewModel = viewModel, memoViewModel = memoViewModel)
        }
    }
}
