package lc.fungee.Ingredicheck.onboarding.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageContent
import lc.fungee.Ingredicheck.R
import lc.fungee.Ingredicheck.onboarding.data.OnboardingChipData
import lc.fungee.Ingredicheck.onboarding.data.avatarBackgroundColorForId
import lc.fungee.Ingredicheck.onboarding.model.OnboardingViewModel
import lc.fungee.Ingredicheck.onboarding.ui.components.familyPlaceholderColor
import lc.fungee.Ingredicheck.ui.theme.Greyscale40
import lc.fungee.Ingredicheck.ui.theme.Greyscale100
import lc.fungee.Ingredicheck.ui.theme.Greyscale110
import lc.fungee.Ingredicheck.ui.theme.Greyscale150
import lc.fungee.Ingredicheck.ui.theme.Manrope
import lc.fungee.Ingredicheck.ui.theme.Nunito
import lc.fungee.Ingredicheck.ui.theme.Primary800

@Composable
fun FamilyOverviewBackground(
    members: List<OnboardingViewModel.FamilyOverviewMember>,
    modifier: Modifier = Modifier,
    bottomSheetHeight: Dp = 0.dp,
    onLeaveFamily: (() -> Unit)? = null,
    onInvite: ((OnboardingViewModel.FamilyOverviewMember) -> Unit)? = null,
    onEditMember: ((OnboardingViewModel.FamilyOverviewMember) -> Unit)? = null
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(top = 64.dp, start = 20.dp, end = 20.dp)
    ) {

        Text(
            text = "Your Family Overview",
            style = TextStyle(
                fontFamily = Nunito,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = Greyscale150
            ),
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(18.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(bottom = bottomSheetHeight + 24.dp)
        ) {
            members.forEachIndexed { index, member ->
                if (index > 0) Spacer(modifier = Modifier.height(14.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(28.dp))
                        .border(1.dp, Greyscale110.copy(alpha = 0.28f), RoundedCornerShape(28.dp))
                        .background(Color.White)
                        .padding(horizontal = 18.dp, vertical = 16.dp)
                ) {
                    Box(

                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            val hasGenerated = member.generatedAvatarUrl.trim().isNotBlank()
                            val circleBackground = if (hasGenerated) {
                                avatarBackgroundColorForId(member.backgroundColorId)
                            } else {
                                Color.White
                            }

                            Box(
                                modifier = Modifier.size(54.dp),
                                contentAlignment = Alignment.BottomEnd
                            ) {
                                // Avatar circle
                                Box(
                                    modifier = Modifier
                                        .matchParentSize()
                                        .clip(CircleShape)
                                        .border(2.dp, Primary800.copy(alpha = 0.18f), CircleShape)
                                        .background(circleBackground),
                                    contentAlignment = Alignment.Center
                                ) {
                                    val trimmedUrl = member.generatedAvatarUrl.trim()
                                    val res = OnboardingChipData.avatarResOrNull(member.avatarId.trim())
                                    when {
                                        trimmedUrl.isNotBlank() -> {
                                            SubcomposeAsyncImage(
                                                model = trimmedUrl,
                                                contentDescription = null,
                                                modifier = Modifier
                                                    .size(50.dp)
                                                    .clip(CircleShape)
                                            ) {
                                                when (painter.state) {
                                                    is coil.compose.AsyncImagePainter.State.Loading -> {
                                                        Box(
                                                            modifier = Modifier
                                                                .fillMaxSize()
                                                                .background(circleBackground),
                                                            contentAlignment = Alignment.Center
                                                        ) {
                                                            CircularProgressIndicator(
                                                                modifier = Modifier.size(20.dp),
                                                                strokeWidth = 2.dp,
                                                                color = Primary800
                                                            )
                                                        }
                                                    }
                                                    else -> {
                                                        SubcomposeAsyncImageContent()
                                                    }
                                                }
                                            }
                                        }
                                        res != null -> {
                                            Image(
                                                painter = painterResource(id = res),
                                                contentDescription = null,
                                                modifier = Modifier
                                                    .size(50.dp)
                                                    .clip(CircleShape)
                                            )
                                        }
                                        else -> {
                                            val bg = familyPlaceholderColor(member.name)
                                            Box(
                                                modifier = Modifier
                                                    .size(50.dp)
                                                    .clip(CircleShape)
                                                    .background(bg),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                val initial = member.name.trim().firstOrNull()?.uppercase() ?: "?"
                                                Text(
                                                    text = initial,
                                                    style = TextStyle(
                                                        fontFamily = Manrope,
                                                        fontWeight = FontWeight.Bold,
                                                        fontSize = 18.sp,
                                                        color = Color.White
                                                    )
                                                )
                                            }
                                        }
                                    }
                                }

                                // Edit (pen) icon overlay
                                if (onEditMember != null) {
                                    Box(
                                        modifier = Modifier
                                            .offset(x = 2.dp, y = 2.dp)
                                            .size(20.dp)
                                            .clip(CircleShape)
                                            .background(color = Greyscale40)
                                            .clickable { onEditMember(member) },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            painter = painterResource(id = R.drawable.pen_line_icon),
                                            tint = Greyscale150,
                                            contentDescription = null,
                                            modifier = Modifier.size(12.dp)
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.width(14.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = member.name,
                                    style = TextStyle(
                                        fontFamily = Manrope,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 18.sp,
                                        color = Greyscale150
                                    )
                                )

                                if (member.invitePending) {
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(12.50.dp))
                                            .background(Color(0xFFFFF9ED)) // soft yellow pending pill
                                            .padding(horizontal = 8.dp, vertical = 4.dp)
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(
                                                painter = painterResource(id = R.drawable.exclamation_circle),
                                                contentDescription = null,
                                                tint = Color(0xFFFAB222),
                                                modifier = Modifier.size(12.dp)
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = "Pending",
                                                style = TextStyle(
                                                    fontFamily = Nunito,
                                                    fontWeight = FontWeight.SemiBold,
                                                    fontSize = 10.sp,
                                                    color = Color(0xFFFAB222)
                                                )
                                            )
                                        }
                                    }
                                } else {
                                    Text(
                                        text = if (member.joined) "(You)" else "Not joined yet !",
                                        style = TextStyle(
                                            fontFamily = Nunito,
                                            fontWeight = FontWeight.Normal,
                                            fontSize = 12.sp,
                                            color = Greyscale110
                                        )
                                    )
                                }
                            }

                            val isFirst = index == 0
                            val isPending = member.invitePending
                            val actionText = if (isFirst) {
                                "Leave Family"
                            } else if (isPending) {
                                "Re-invite"
                            } else {
                                "Invite"
                            }
                            val leaveFamilyColor = Color(0xFFFF3F31)
                            val inviteColor = Color(0xFF75990E)
                            val actionColor = if (isFirst) leaveFamilyColor else inviteColor
                            val borderColor = if (isFirst) leaveFamilyColor else Greyscale40

                            Box(
                                modifier = Modifier
                                    .height(36.dp)
                                    .clip(RoundedCornerShape(18.dp))
                                    .border(1.dp, borderColor, RoundedCornerShape(18.dp))
                                    .clickable {
                                        if (isFirst) {
                                            onLeaveFamily?.invoke()
                                        } else {
                                            onInvite?.invoke(member)
                                        }
                                    }
                                    .padding(horizontal = 16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    if (!isFirst) {
                                        Icon(
                                            painter = painterResource(id = R.drawable.share_icon),
                                            contentDescription = null,
                                            tint = actionColor,
                                            modifier = Modifier
                                                .size(16.dp)
                                                .padding(end = 3.dp)
                                        )
                                    }

                                    Text(
                                        text = actionText,
                                        style = TextStyle(
                                            fontFamily = Manrope,
                                            fontWeight = FontWeight.SemiBold,
                                            fontSize = 14.sp,
                                            color = actionColor
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

