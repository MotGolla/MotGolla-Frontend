package com.motgolla.ui.component

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.compose.ui.geometry.Offset
import com.motgolla.ui.theme.MotgollaPrimary

@Composable
fun MotgollaNavBar(navController: NavController) {
    val navBackStackEntry = navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry.value?.destination?.route

    val selectedColor = MotgollaPrimary
    val unselectedColor = Color.Gray

    val items = listOf(
        MotGollaNavScreen.Home,
        MotGollaNavScreen.Record,
        MotGollaNavScreen.Vote,
        MotGollaNavScreen.My
    )

    Box {
        Surface(
            color = Color.White,
            tonalElevation = 0.dp,
            shadowElevation = 8.dp,
            modifier = Modifier
                .fillMaxWidth()
                .height(70.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                items.forEachIndexed { index, screen ->
                    if (index == 2) {
                        Spacer(modifier = Modifier.width(60.dp))
                    }
                    if(index != 0 && index !=4){
                        Spacer(modifier = Modifier.width(10.dp))
                    }
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.clickable {
                            if (currentRoute != screen.route) {
                                navController.navigate(screen.route) {
                                    popUpTo(navController.graph.startDestinationId) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        }
                    ) {
                        Image(
                            painter = painterResource(id = screen.iconRes),
                            contentDescription = screen.title,
                            modifier = Modifier.size(22.dp),
                            colorFilter = if (currentRoute == screen.route) {
                                ColorFilter.tint(selectedColor)
                            } else {
                                ColorFilter.tint(unselectedColor)
                            }
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = screen.title,
                            style = MaterialTheme.typography.labelSmall,
                            color = if (currentRoute == screen.route) selectedColor else unselectedColor
                        )
                    }
                }
            }
        }

        // 가운데 + 버튼
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .offset(y = (-15).dp)
                .size(55.dp)
                .shadow(
                    elevation = 8.dp,
                    shape = CircleShape,
                    ambientColor = Color(0x33000000),
                    spotColor = Color(0x33000000)
                )
                .background(
                    brush = Brush.linearGradient(
                        colorStops = arrayOf(
                            0.0f to Color(0xFF8D0AFF),
                            0.4f to Color(0xFF700AFF),
                            1.0f to Color(0xFF4A12FF)
                        ),
                        start = Offset(0f, 0f),
                        end = Offset(200f, 200f)
                    ),
                    shape = CircleShape
                )
                .clickable {
                    navController.navigate(MotGollaNavScreen.Plus.route)
                },
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = MotGollaNavScreen.Plus.iconRes),
                contentDescription = "등록",
                modifier = Modifier.size(20.dp),
                colorFilter = ColorFilter.tint(Color.White)
            )
        }
    }
}


@Preview(showBackground = true)
@Composable
fun MotgollaNavBarPreview() {
    val navController = rememberNavController()
    MotgollaNavBar(navController = navController)
}