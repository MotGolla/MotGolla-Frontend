package com.motgolla.ui.component.record
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.runtime.*
import androidx.compose.material3.*
import androidx.compose.foundation.layout.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.motgolla.common.RetrofitClient
import kotlinx.coroutines.launch
import androidx.compose.ui.platform.LocalFocusManager
import com.motgolla.domain.record.data.response.BrandLocationResponse

class FloorSelectorViewModel(
    private val departmentStoreId: Long,
    private val brandName: String
) : ViewModel() {
    var locations by mutableStateOf<List<BrandLocationResponse>>(emptyList())
        private set

    init {
        fetchLocations(departmentStoreId, brandName)
    }

    private fun fetchLocations(departmentId: Long, brandName: String) {
        viewModelScope.launch {
            try {
                locations = RetrofitClient.getRecordService().getDepartmentStoreLocations(
                    departmentStoreId = departmentId,
                    brandName = brandName
                )
                Log.d("FloorSelectorViewModel", "Fetched locations: $locations")
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FloorSelector(
    departmentStoreId: Long,
    brandName: String,
    onLocationSelected: (Long) -> Unit // 변경
) {
    val viewModel = remember { FloorSelectorViewModel(departmentStoreId, brandName) }
    var expanded by remember { mutableStateOf(false) }
    var selectedLocation by remember { mutableStateOf("매장 위치 선택") }
    val focusManager = LocalFocusManager.current // 포커스 매니저 추가

    Column(
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            Box(
                modifier = Modifier
                    .width(170.dp)
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
                        .background(Color.White), // 여기에도 한 번 더
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        containerColor = Color.White,
                        focusedBorderColor = Color.Gray,
                        unfocusedBorderColor = Color.Gray,
                        focusedLabelColor = Color.Gray,
                        unfocusedLabelColor = Color.Gray
                    )
                )
            }

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = {
                    expanded = false
                    focusManager.clearFocus() // 포커스 해제
                }
            ) {
                viewModel.locations.forEach { location ->
                    DropdownMenuItem(
                        text = { Text(location.location) },
                        onClick = {
                            selectedLocation = location.location
                            expanded = false
                            focusManager.clearFocus() // 포커스 해제
                            onLocationSelected(location.departmentStoreBrandId) // 변경
                        }
                    )
                }
            }
        }
    }
}