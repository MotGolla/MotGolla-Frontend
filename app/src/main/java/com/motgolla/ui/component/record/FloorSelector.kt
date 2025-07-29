package com.motgolla.ui.component.record

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.motgolla.common.RetrofitClient
import com.motgolla.domain.record.data.response.BrandLocationResponse
import kotlinx.coroutines.launch

// ✅ ViewModel
class FloorSelectorViewModel(
    private val departmentStoreId: Long,
    private var brandName: String
) : ViewModel() {
    var locations by mutableStateOf<List<BrandLocationResponse>>(emptyList())
        private set

    init {
        fetchLocations(departmentStoreId, brandName)
    }

    fun fetchLocations(departmentId: Long, brandName: String) {
        viewModelScope.launch {
            try {
                locations = RetrofitClient.getRecordService().getDepartmentStoreLocations(
                    departmentStoreId = departmentId,
                    brandName = brandName
                )
                Log.d("FloorSelectorViewModel", "Fetched locations: $locations")
            } catch (e: Exception) {
                Log.e("FloorSelectorViewModel", "Error fetching locations", e)
            }
        }
    }
}

// ✅ Composable
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FloorSelector(
    departmentStoreId: Long,
    brandName: String,
    refreshTrigger: Any?, // 새로 추가
    onLocationSelected: (Long) -> Unit
) {
    val viewModel = remember {
        FloorSelectorViewModel(departmentStoreId, brandName)
    }


    // 택 사진 등 외부 변경이 있을 때 강제 새로고침
    LaunchedEffect(refreshTrigger, brandName) {
        viewModel.fetchLocations(departmentStoreId, brandName)
    }


    var expanded by remember { mutableStateOf(false) }
    var selectedLocation by remember { mutableStateOf("매장 위치 선택") }
    val focusManager = LocalFocusManager.current

    Column(
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            OutlinedTextField(
                value = selectedLocation,
                onValueChange = {},
                readOnly = true,
                label = { Text("매장 위치 선택") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp)),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = Color.Gray,
                    unfocusedBorderColor = Color(0xFFE6E8EB),
                    focusedLabelColor = Color.Gray,
                    unfocusedLabelColor = Color.Gray,
                    containerColor = Color.White
                )
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = {
                    expanded = false
                    focusManager.clearFocus()
                }
            ) {
                viewModel.locations.forEach { location ->
                    DropdownMenuItem(
                        text = { Text(location.location) },
                        onClick = {
                            selectedLocation = location.location
                            expanded = false
                            focusManager.clearFocus()
                            onLocationSelected(location.departmentStoreBrandId)
                        }
                    )
                }
            }
        }
    }
}
