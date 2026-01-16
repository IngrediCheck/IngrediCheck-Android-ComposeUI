package lc.fungee.Ingredicheck.components
import android.graphics.drawable.ColorDrawable
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.window.DialogWindowProvider
import lc.fungee.Ingredicheck.components.buttons.PrimaryButton
import lc.fungee.Ingredicheck.components.buttons.SecondaryButton
import lc.fungee.Ingredicheck.ui.theme.Greyscale110
import lc.fungee.Ingredicheck.ui.theme.Greyscale150
import lc.fungee.Ingredicheck.ui.theme.Greyscale40
import lc.fungee.Ingredicheck.ui.theme.Nunito

@Composable
fun NonDraggableBottomSheet(
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }

    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            dismissOnBackPress = true,
            dismissOnClickOutside = false
        )
    ) {
        val view = LocalView.current
        SideEffect {
            val window = (view.parent as? DialogWindowProvider)?.window
            window?.setDimAmount(0f)
            window?.setBackgroundDrawable(ColorDrawable(android.graphics.Color.TRANSPARENT))
            window?.decorView?.setPadding(0, 0, 0, 0)
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .clickable(
                    interactionSource = interactionSource,
                    indication = null
                ) { },
            contentAlignment = Alignment.BottomCenter
        ) {
            Box(
                modifier = modifier
                    .fillMaxWidth()
                    .height(243.dp)
                    .shadow(
                        elevation = 27.5.dp,
                        shape = RoundedCornerShape(topStart = 40.dp, topEnd = 40.dp),
                        ambientColor = Color(0x6BD9D9D9),
                        spotColor = Color(0x6BD9D9D9)
                    )
                    .background(
                        color = Color.White,
                        shape = RoundedCornerShape(topStart = 40.dp, topEnd = 40.dp)
                    )
                    .clickable(
                        interactionSource = interactionSource,
                        indication = null
                    ) { }
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 20.dp, top = 24.dp, end = 20.dp, bottom = 16.dp),
                    horizontalAlignment = Alignment.Start
                ) {
                    content()
                }
            }
        }
    }
}

@Composable
fun InviteCodeBottomSheet(
    onPrimaryClick: () -> Unit,
    onSecondaryClick: () -> Unit,
    onDismissRequest: () -> Unit
) {
    NonDraggableBottomSheet(
        onDismissRequest = onDismissRequest
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            androidx.compose.material3.Text(
                text = "Do you have an invite code?",
                style = TextStyle(
                    fontFamily = Nunito,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = Greyscale150
                )
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            SecondaryButton(
                title = "Enter invite code",
                modifier = Modifier.weight(1f),
                takeFullWidth = true,
                width = 0.dp,
                isDisabled = true,
                onClick = onSecondaryClick,
                textColor = Greyscale110,
                disabledTextColor = Greyscale110,
                disabledBackgroundColor = Greyscale40,
                borderColor = Greyscale40
            )

            PrimaryButton(
                title = "No, Continue",
                modifier = Modifier.weight(1f),
                takeFullWidth = true,
                width = 0.dp,
                onClick = onPrimaryClick
            )
        }
    }
}
