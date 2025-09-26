package lc.fungee.IngrediCheck.ui.screens.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.DropdownMenu
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import lc.fungee.IngrediCheck.R
import lc.fungee.IngrediCheck.data.model.DietaryPreference
import lc.fungee.IngrediCheck.data.repository.PreferenceViewModel
import lc.fungee.IngrediCheck.ui.theme.AppColors






@Composable
fun PreferencesList(
    viewModel: PreferenceViewModel,
    onEdit: (DietaryPreference) -> Unit
) {
    val clipboardManager = LocalClipboardManager.current

    Column(modifier = Modifier.fillMaxWidth()) {
        viewModel.preferences.forEach { preference ->
            var expanded by remember { mutableStateOf(false) }

            // Wrap row + dropdown in a Box
            Box(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp)
                        .pointerInput(Unit) {
                            detectTapGestures(
                                onLongPress = { expanded = true }
                            )
                        },
                    verticalAlignment = Alignment.Top
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .offset(y = 8.dp)
                            .background(AppColors.Brand, CircleShape)
                    )
                    Spacer(modifier = Modifier.width(16.dp))

                    Text(
                        text = viewModel.buildBoldAnnotatedString(preference.annotatedText),
                        color = AppColors.Neutral600,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Normal,
                        lineHeight = 21.sp,
                        letterSpacing = (-0.32).sp,
                        modifier = Modifier.weight(1f)
                    )
                }


                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    modifier = Modifier.width(140.dp).clip(RoundedCornerShape(26.dp))
                ) {
                    DropdownMenuItem(
                        leadingIcon = {
                            Image(
                                painter = painterResource(id = R.drawable.copyicon),
                                contentDescription = "Copy"
                            )
                        },
                        text = { Text("     Copy", fontSize = 16.sp) },
                        onClick = {
                            clipboardManager.setText(AnnotatedString(preference.annotatedText))
                            expanded = false
                        }
                    )

                    DropdownMenuItem(
                        leadingIcon = {
                            Image(
                                painter = painterResource(id = R.drawable.editicon),
                                contentDescription = "Copy"
                            )
                        },
                        text = { Text("     Edit", fontSize = 16.sp) },
                        onClick = {
                            onEdit(preference)
                            expanded = false
                        }
                    )

                    DropdownMenuItem(
                        leadingIcon = {
                            Image(
                                painter = painterResource(id = R.drawable.deketeicon),
                                contentDescription = "Copy"
                            )
                        },
                        text = { Text("     Delete", color = Color.Red, fontSize = 16.sp) },
                        onClick = {
                            viewModel.deletePreference(preference)
                            expanded = false
                        }
                    )
                }
            }

            // Divider after each row
            Divider(
                color = AppColors.Divider,
                thickness = 2.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 18.dp)
            )
        }
    }
}







//@Composable
//fun LoadingScreen() {
//    Box(
//        modifier = Modifier
//            .fillMaxSize()
//            .background(Color.White),
//        contentAlignment = Alignment.Center
//    ) {
//        Column(
//            horizontalAlignment = Alignment.CenterHorizontally,
//            verticalArrangement = Arrangement.Center
//        ) {
//            CircularProgressIndicator(
//                modifier = Modifier.size(80.dp),
//                color = PrimaryGreen100
//            )
//            Spacer(modifier = Modifier.height(16.dp))
//            Text(
//                text = "Loading...",
//                style = androidx.compose.ui.text.TextStyle(
//                    fontSize = 16.sp,
//                    fontWeight = FontWeight.Medium
//                )
//            )
//        }
//    }
//}

// Error Screen Component
@Composable
fun ErrorScreen(
    message: String,
    onRetry: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Something went wrong",
                style = androidx.compose.ui.text.TextStyle(
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold
                ),
                color = Color.Red
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = message,
                style = androidx.compose.ui.text.TextStyle(
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Normal
                ),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 32.dp)
            )
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = onRetry,
                colors = ButtonDefaults.buttonColors(
                    containerColor = AppColors.Brand
                )
            ) {
                Text(
                    text = "Retry",
                    color = Color.White
                )
            }
        }
    }
}
