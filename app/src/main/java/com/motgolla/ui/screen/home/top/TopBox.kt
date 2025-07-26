package com.motgolla.ui.screen.home.top

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.motgolla.viewmodel.LocationViewModel

@Composable
fun TopBox(viewModel: LocationViewModel) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
    ) {
        Spacer(modifier = Modifier.height(20.dp))

        LocationSection(viewModel = viewModel)
        Spacer(modifier = Modifier.height(10.dp))
        UserInfoSection()
    }
}