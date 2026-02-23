package lc.fungee.Ingredicheck.onboarding.ui

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.Context
import android.content.ContextWrapper
import android.net.Uri
import android.util.Log
import androidx.annotation.DrawableRes
import androidx.activity.compose.BackHandler
import lc.fungee.Ingredicheck.R
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import kotlinx.coroutines.delay

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import kotlin.math.absoluteValue
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageContent
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.ui.Alignment
import androidx.compose.ui.layout.Layout
import kotlinx.coroutines.runBlocking
import lc.fungee.Ingredicheck.auth.AppleLoginWebViewActivity
import lc.fungee.Ingredicheck.ui.theme.Nunito
import lc.fungee.Ingredicheck.ui.components.NonDraggableBottomSheet
import lc.fungee.Ingredicheck.ui.theme.Greyscale10
import lc.fungee.Ingredicheck.ui.theme.Greyscale30
import lc.fungee.Ingredicheck.ui.theme.Greyscale40
import lc.fungee.Ingredicheck.auth.AuthViewModel
import lc.fungee.Ingredicheck.auth.AuthEnv
import lc.fungee.Ingredicheck.auth.AuthState
import lc.fungee.Ingredicheck.auth.MemojiGenState
import lc.fungee.Ingredicheck.auth.GoogleAuthDataSource
import lc.fungee.Ingredicheck.auth.rememberAppleLoginLauncher
import lc.fungee.Ingredicheck.auth.rememberGoogleSignInLauncher
import lc.fungee.Ingredicheck.family.CreateFamilyRequest
import lc.fungee.Ingredicheck.family.FamilyMemberDto
import lc.fungee.Ingredicheck.memoji.GetStatedScreen
import lc.fungee.Ingredicheck.onboarding.model.OnboardingPersistence
import lc.fungee.Ingredicheck.onboarding.data.EVERYONE_MEMBER_ID
import lc.fungee.Ingredicheck.onboarding.data.OnboardingChipData
import lc.fungee.Ingredicheck.onboarding.data.DynamicStepsLoader
import lc.fungee.Ingredicheck.onboarding.data.avatarBackgroundColorForId
import lc.fungee.Ingredicheck.onboarding.model.OnboardingStep
import lc.fungee.Ingredicheck.onboarding.model.OnboardingViewModel
import lc.fungee.Ingredicheck.onboarding.model.OnboardingViewModelFactory
import lc.fungee.Ingredicheck.onboarding.ui.components.AnimatedProgressLine
import lc.fungee.Ingredicheck.onboarding.ui.components.CapsuleStep
import lc.fungee.Ingredicheck.onboarding.ui.components.CapsuleStepperRow
import lc.fungee.Ingredicheck.ui.theme.Greyscale100
import lc.fungee.Ingredicheck.ui.theme.Greyscale110
import lc.fungee.Ingredicheck.ui.theme.Greyscale150
import lc.fungee.Ingredicheck.ui.theme.Greyscale60
import lc.fungee.Ingredicheck.ui.theme.Manrope
import lc.fungee.Ingredicheck.ui.theme.Secondary200
import lc.fungee.Ingredicheck.ui.theme.Primary800
import kotlin.random.Random

private fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}

private fun shareInviteCode(context: Context, code: String) {
    val msg = "You've been invited to join my IngredientCheck family.\n\nInvite code: $code"
    val shareIntent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, msg)
    }
    context.startActivity(Intent.createChooser(shareIntent, "Invite"))
}

/** Builds the default "Bite Buddy" family request for the Just Me flow (matches iOS createBiteBuddyFamily). */
private fun buildBiteBuddyFamilyRequest(): CreateFamilyRequest {
    val selfId = java.util.UUID.randomUUID().toString()
    val selfMember = FamilyMemberDto(
        id = selfId,
        name = "Bite Buddy",
        color = "#FFFFBA",
        joined = true,
        invitePending = null,
        imageFileHash = "memoji_3"
    )
    return CreateFamilyRequest(
        name = "Bite Buddy",
        selfMember = selfMember,
        otherMembers = null
    )
}

private fun buildCreateFamilyRequestFromMembers(
    members: List<OnboardingViewModel.FamilyOverviewMember>
): CreateFamilyRequest? {
    if (members.isEmpty()) return null
    val self = members.first()
    val others = members.drop(1)

    fun toDto(m: OnboardingViewModel.FamilyOverviewMember, joined: Boolean): FamilyMemberDto {
        val imageFileHash = m.generatedAvatarUrl.trim().ifBlank { null }
        return FamilyMemberDto(
            id = m.id,
            name = m.name,
            color = m.colorHex.ifBlank { "#BAE1FF" },
            joined = joined,
            invitePending = null,
            imageFileHash = imageFileHash
        )
    }

    return CreateFamilyRequest(
        name = "${self.name}'s Family",
        selfMember = toDto(self, joined = true),
        otherMembers = if (others.isEmpty()) null else others.map { toDto(it, joined = false) }
    )
}

private fun familyPlaceholderColor(seed: String): Color {
    val palette = listOf(
        Color(0xFF9AD0FF),
        Color(0xFFFFB3C1),
        Color(0xFFB9F6CA),
        Color(0xFFFFE29A),
        Color(0xFFD7B9FF),
        Color(0xFFFFC59A)
    )
    val idx = (seed.hashCode().absoluteValue % palette.size)
    return palette[idx]
}

private val SelectedPillBackground = Secondary200
private val PillShape = RoundedCornerShape(30.dp)

/** Build a single preference string from onboarding chip selections for backend sync (same as iOS). */
private fun buildDietaryPreferenceText(selectedAllergiesByMember: Map<String, MutableSet<String>>): String {
    val allChipIds = selectedAllergiesByMember.values.flatMap { it.toList() }.toSet()
    return allChipIds.map { OnboardingChipData.labelForChipId(it) }.joinToString(", ")
}

@Composable
private fun SelectedChipPill(
    emoji: String,
    label: String,
    trailingAvatars: (@Composable () -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .clip(PillShape)
            .background(SelectedPillBackground)
            .padding(horizontal = 12.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = emoji.trim(),
            fontSize = 16.sp,
            color = Greyscale150
        )
        Text(
            text = label,
            fontFamily = Manrope,
            fontWeight = FontWeight.Medium,
            fontSize = 14.sp,
            color = Greyscale150,
            maxLines = 1
        )
        if (trailingAvatars != null) {
            Spacer(modifier = Modifier.width(6.dp))
            trailingAvatars()
        }
    }
}

@Composable
private fun CapsuleEveryoneAvatarSmall() {
    Box(
        modifier = Modifier
            .size(24.dp)
            .clip(CircleShape)
            .background(Color.White)
            .border(
                width = 1.dp,
                color = Greyscale40,
                shape = CircleShape
            ),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.everyone_seleted_home_icon),
            contentDescription = null,
            modifier = Modifier.size(18.dp),
            contentScale = ContentScale.Fit
        )
    }
}

@Composable
private fun CapsuleMemberAvatarSmall(member: OnboardingViewModel.FamilyOverviewMember) {
    val avatarRes = OnboardingChipData.avatarResOrNull(member.avatarId)
    Box(
        modifier = Modifier
            .size(24.dp)
            .clip(CircleShape)
            .background(Color.White)
            .border(
                width = 1.dp,
                color = Greyscale40,
                shape = CircleShape
            ),
        contentAlignment = Alignment.Center
    ) {
        when {
            member.generatedAvatarUrl.trim().isNotBlank() -> {
                SubcomposeAsyncImage(
                    model = member.generatedAvatarUrl.trim(),
                    contentDescription = null,
                    modifier = Modifier
                        .size(22.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
            }

            avatarRes != null -> {
                Image(
                    painter = painterResource(id = avatarRes),
                    contentDescription = null,
                    modifier = Modifier
                        .size(22.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
            }

            else -> {
                Box(
                    modifier = Modifier
                        .size(22.dp)
                        .clip(CircleShape)
                        .background(Greyscale40)
                )
            }
        }
    }
}

@Composable
private fun CapsuleChipMemberAvatars(
    memberIds: Set<String>,
    members: List<OnboardingViewModel.FamilyOverviewMember>
) {
    if (memberIds.isEmpty()) return

    val everyoneId = EVERYONE_MEMBER_ID
    val hasEveryone = memberIds.contains(everyoneId)
    val concreteMemberIds = memberIds.filter { it != everyoneId }.toSet()
    val concreteMembers = members.filter { concreteMemberIds.contains(it.id) }

    Row(
        horizontalArrangement = Arrangement.spacedBy(-8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        concreteMembers.forEach { m ->
            CapsuleMemberAvatarSmall(member = m)
        }
        if (hasEveryone) {
            CapsuleEveryoneAvatarSmall()
        }
    }
}

@Composable
private fun FlowRowChips(
    modifier: Modifier = Modifier,
    horizontalSpacing: Dp = 8.dp,
    verticalSpacing: Dp = 8.dp,
    content: @Composable () -> Unit
) {
    Layout(content = content, modifier = modifier) { measurables, constraints ->
        if (measurables.isEmpty()) {
            return@Layout layout(0, 0) {}
        }
        val density = this
        val spacingX = with(density) { horizontalSpacing.roundToPx() }
        val spacingY = with(density) { verticalSpacing.roundToPx() }
        val placeable = measurables.map { it.measure(constraints.copy(minWidth = 0, minHeight = 0)) }
        val maxWidth = constraints.maxWidth
        var x = 0
        var y = 0
        var rowHeight = 0
        val positions = placeable.map { p ->
            if (x > 0 && x + p.width > maxWidth) {
                x = 0
                y += rowHeight + spacingY
                rowHeight = 0
            }
            val pos = x to y
            x += p.width + spacingX
            rowHeight = maxOf(rowHeight, p.height)
            pos
        }
        val totalHeight = (y + rowHeight).coerceIn(constraints.minHeight, constraints.maxHeight)
        layout(maxWidth, totalHeight) {
            placeable.forEachIndexed { i, p ->
                val (px, py) = positions[i]
                p.placeRelative(px, py)
            }
        }
    }
}

@Composable
private fun CapsuleSkeletonBox(
    modifier: Modifier = Modifier,
    selectedChipIds: Set<String> = emptySet(),
    sectionTitle: String = "Allergies",
    @DrawableRes sectionIconRes: Int = R.drawable.ic_step_allergies,
    trailingAvatarsForChip: ((String) -> (@Composable () -> Unit)?)? = null
) {
    val showSelectedChips = selectedChipIds.isNotEmpty()
    val resolvedChips = remember(selectedChipIds) {
        selectedChipIds.mapNotNull { id -> OnboardingChipData.chipForId(id) }
    }
    val hasOtherSelection = remember(selectedChipIds) {
        // Any chip id containing "other" (e.g. "other", "other_sens", "region_*_other", etc.)
        selectedChipIds.any { it.contains("other", ignoreCase = true) }
    }

    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .then(
                if (showSelectedChips) Modifier.heightIn(min = 130.dp)
                else Modifier.height(130.dp)
            )
            .clip(RoundedCornerShape(20.dp))
            .border((0.25).dp, Greyscale60, RoundedCornerShape(20.dp))
            .background(Greyscale10)
            .padding(12.dp)
    ) {
        if (showSelectedChips && resolvedChips.isNotEmpty()) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.Start
            ) {
                Row(modifier = Modifier,
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        painter = painterResource(sectionIconRes),
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                        tint = Greyscale110
                    )
                    Text(
                        text = sectionTitle,
                        fontFamily = Manrope,
                        fontWeight = FontWeight.Medium,
                        fontSize = 16.sp,
                        color = Greyscale110
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                FlowRowChips(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalSpacing = 8.dp,
                    verticalSpacing = 8.dp
                ) {
                    resolvedChips.forEach { def ->
                        val trailing = trailingAvatarsForChip?.invoke(def.id)
                        SelectedChipPill(
                            emoji = def.iconPrefix,
                            label = def.label,
                            trailingAvatars = trailing
                        )
                    }
                }
                if (hasOtherSelection) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.emoji_warning),
                            contentDescription = null,
                            modifier = Modifier.size(14.dp),
                            tint = Color.Unspecified
                        )
                        Text(
                            text = "Something else too, don't worry we'll ask later!",
                            fontFamily = Manrope,
                            fontWeight = FontWeight.Normal,
                            fontSize = 12.sp,
                            color = Greyscale100
                        )
                    }
                }
            }
        } else {
            val maxWidth = maxWidth
            val spacing = 8.dp
            val minFirst = 90.dp
            val minSecond = 110.dp

            fun randomRowWidths(): Pair<Dp, Dp> {
                val maxExtra = (maxWidth - spacing - minFirst - minSecond).coerceAtLeast(0.dp)
                if (maxExtra == 0.dp) {
                    val second = (maxWidth - spacing - minFirst).coerceAtLeast(minSecond)
                    return minFirst to second
                }
                val extraFraction = Random.nextFloat()
                val first = minFirst + maxExtra * extraFraction
                val second = maxWidth - spacing - first
                return first to second
            }

            val (row1First, row1Second) = remember(maxWidth) { randomRowWidths() }
            val (row2First, row2Second) = remember(maxWidth) { randomRowWidths() }

            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.Start
            ) {
                Box(
                    modifier = Modifier
                        .width(165.dp)
                        .height(13.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(Greyscale30)
                )
                Spacer(modifier = Modifier.height(12.dp))
                CapsuleRow(firstWidth = row1First, secondWidth = row1Second)
                Spacer(modifier = Modifier.height(8.dp))
                CapsuleRow(firstWidth = row2First, secondWidth = row2Second)
            }
        }
    }
}

@Composable
private fun CapsuleRow(
    firstWidth: Dp,
    secondWidth: Dp
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .width(firstWidth)
                .height(36.dp)
                .clip(RoundedCornerShape(30.dp))
                .background(Greyscale30)
        )
        Box(
            modifier = Modifier
                .width(secondWidth)
                .height(36.dp)
                .clip(RoundedCornerShape(30.dp))
                .background(Greyscale30)
        )
    }
}

@Composable
private fun FamilyOverviewBackground(
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
                                            painter = painterResource(id = R.drawable.pen_line_icon) , tint = Greyscale150

                                            ,
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
                                    .border(1.dp, borderColor, RoundedCornerShape(18.dp) ,)
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

@SuppressLint("SuspiciousIndentation")
@Composable
fun OnboardingHost(
    authViewModel: AuthViewModel,
    onExitOnboarding: () -> Unit
) {
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    val savedStateOwner = androidx.savedstate.compose.LocalSavedStateRegistryOwner.current
    val persistence = remember(context) { OnboardingPersistence(context.applicationContext) }
    val factory = remember(savedStateOwner, persistence) {
        OnboardingViewModelFactory(owner = savedStateOwner, persistence = persistence)
    }

    val vm: OnboardingViewModel = viewModel(factory = factory)
    val step = vm.currentStep
    var isCreatingFamily by remember { mutableStateOf(false) }
    var isCreatingBiteBuddyFamily by remember { mutableStateOf(false) }
    var isInviting by remember { mutableStateOf(false) }
    val isRestored = vm.isRestored
    val authState by authViewModel.state.collectAsState()
    val emojiState by authViewModel.memojiState.collectAsState()
    val isAuthLoading = authState is AuthState.Loading
    val currentFamily by authViewModel.currentFamily.collectAsState()

    var sheetHeight by remember { mutableStateOf(0.dp) }
    var memberToInvite by remember { mutableStateOf<OnboardingViewModel.FamilyOverviewMember?>(null) }

    if (!isRestored) {
        Box(modifier = Modifier.fillMaxSize())
        return
    }

    LaunchedEffect(step) {
        if (isRestored) {
            authViewModel.syncOnboardingMetadata(step)
        }
        if (
            step == OnboardingStep.SIGN_IN_WHO_IS_THIS_FOR ||
            step == OnboardingStep.FALLING_CAPSULES ||
            step == OnboardingStep.ADD_FAMILY_WELCOME ||
            step == OnboardingStep.ADD_FAMILY_NAME ||
            step == OnboardingStep.ADD_FAMILY_AVATAR_PICKER ||
            step == OnboardingStep.ADD_FAMILY_AVATAR_GENERATING ||
            step == OnboardingStep.ADD_FAMILY_ALL_SET_OR_MORE ||
            step == OnboardingStep.ADD_FAMILY_EDIT_MEMBER
        ) {
            authViewModel.debugLogCurrentSession("Entered $step (before ensureAnonymousSession)")
            authViewModel.ensureAnonymousSession()
        }
        
        // Note: signInAsGuest() should NOT be called here because it sets authState to Success,
        // which causes MainActivity to immediately exit onboarding. Guest sign-in should happen
        // only when onboarding is actually completed (e.g., when user exits after completing allergies).

        // Restore memoji generation UI state after process death:
        // - If we are on the generating screen and have a saved image URL with
        //   memojiGenerationCompleted == true, restore Success state.
        if (step == OnboardingStep.ADD_FAMILY_AVATAR_GENERATING) {
            val currentEmoji = authViewModel.memojiState.value
            if (currentEmoji is MemojiGenState.Idle && vm.memojiGenerationCompleted) {
                val url = vm.addFamilyGeneratedAvatarUrl.trim()
                if (url.isNotBlank()) {
                    authViewModel.restoreMemojiSuccess(url)
                }
            }
        }
    }

    val activity = LocalContext.current.findActivity()
    val googleLauncher = rememberGoogleSignInLauncher(activity = activity, authViewModel = authViewModel)
    val appleLauncher = rememberAppleLoginLauncher(activity = activity, authViewModel = authViewModel)

    // Debounce back to prevent double-tap from going 2 screens back
    var lastBackTime by remember { mutableStateOf(0L) }
    val vmRef = rememberUpdatedState(vm)
    val onExitRef = rememberUpdatedState(onExitOnboarding)
    val handleBack: () -> Unit = handleBack@ {
        val now = System.currentTimeMillis()
        if (now - lastBackTime < 400L) return@handleBack
        lastBackTime = now
        val v = vmRef.value
        if (v.canGoBack()) {
            v.back()
        } else {
            onExitRef.value()
        }
    }

    val isAddMoreMemberNoBack = step == OnboardingStep.ADD_FAMILY_NAME && vm.familyOverviewMembers.size == 1
    val isAllSetOrMoreScreen = step == OnboardingStep.ADD_FAMILY_ALL_SET_OR_MORE
    val isFallingCapsulesScreen = step == OnboardingStep.FALLING_CAPSULES
    BackHandler(enabled = true) {
        if (!isAddMoreMemberNoBack && !isAllSetOrMoreScreen && !isFallingCapsulesScreen) {
            handleBack()
        }
    }

    // On first launch show "Everyone" as selected (ALL); user can switch to a member later.
    // Restore allergy selections state from persistence (per‑member chip ids + active member + step index).
    val (restoredSelections, restoredMemberId, restoredStepIndex) = remember {
        runBlocking {
            try {
                persistence.getAllergySelectionsState()
            } catch (e: Exception) {
                Log.w("OnboardingAllergies", "[RESTORE] getAllergySelectionsState failed", e)
                Triple(emptyMap<String, Set<String>>(), EVERYONE_MEMBER_ID, 0)
            }
        }
    }

    val selectedAllergyMemberIdState = remember(vm.familyOverviewMembers.size, restoredMemberId) {
        mutableStateOf(restoredMemberId.ifBlank { EVERYONE_MEMBER_ID })
    }
    // Initialize from restored state so first composition after restart shows correct chips (no wait for LaunchedEffect).
    val selectedAllergies = remember(restoredSelections) {
        mutableStateListOf<String>().apply {
            addAll(restoredSelections.values.flatten().toSet())
        }
    }
    // memberKey ("ALL" or member.id) -> set of chipIds selected for that member
    val selectedAllergiesByMember = remember(restoredSelections) {
        mutableStateMapOf<String, MutableSet<String>>().apply {
            // Restore persisted selections
            restoredSelections.forEach { (memberKey, chipIds) ->
                this[memberKey] = chipIds.toMutableSet()
            }
        }
    }
    // Bump on every chip toggle so the sheet reliably recomposes (workaround for SnapshotStateMap).
    var allergySelectionRevision by remember { mutableStateOf(0) }
    // Initialize from restored state so bottom sheet shows correct selections on first composition after restart.
    val activeKeyRestored = restoredMemberId.ifBlank { EVERYONE_MEMBER_ID }
    var activeMemberSelections by remember(restoredSelections, activeKeyRestored) {
        mutableStateOf(restoredSelections[activeKeyRestored] ?: emptySet())
    }

    // Rebuild flat union + active member selections from the restored map so UI shows the
    // same selected chips immediately after an app restart.
    LaunchedEffect(restoredSelections, restoredMemberId) {
        if (restoredSelections.isNotEmpty()) {
            val union = restoredSelections.values.flatten().toSet()
            selectedAllergies.clear()
            selectedAllergies.addAll(union)
            val activeKey = restoredMemberId.ifBlank { EVERYONE_MEMBER_ID }
            activeMemberSelections = restoredSelections[activeKey] ?: emptySet()
            Log.d(
                "OnboardingAllergies",
                "[RESTORE_APPLY] restoredSelections=$restoredSelections " +
                    "restoredMemberId=$restoredMemberId restoredStepIndex=$restoredStepIndex " +
                    "union=$union activeMemberSelections=$activeMemberSelections"
            )
        } else {
            Log.d(
                "OnboardingAllergies",
                "[RESTORE_APPLY] no restoredSelections found; keeping defaults"
            )
        }
    }

    // Progress tracking within the fine‑tune flow (allergies, intolerances, etc.)
    // These same steps drive both the CapsuleStepperRow and the AnimatedProgressLine.
    // Load dynamic JSON from assets (same as iOS) so step order/copy can be driven from dynamicJsonData.json.
    var dynamicStepsLoaded by remember { mutableStateOf(false) }
    LaunchedEffect(context) {
        DynamicStepsLoader.ensureLoaded(context)
        dynamicStepsLoaded = true
    }
    val allergySteps = remember(dynamicStepsLoaded) {
        val steps = DynamicStepsLoader.getSteps()?.map { s ->
            CapsuleStep(s.id, s.header.name, OnboardingChipData.iconResForStepId(s.id))
        } ?: emptyList()
        Log.d("DynamicJsonData", "JSON data: UI using ${steps.size} steps from dynamicJsonData.json (same after restart)")
        steps
    }

    // Restore allergy step index (moved after allergySteps definition)
    var allergyStepIndex by remember(restoredStepIndex) {
        mutableStateOf(restoredStepIndex)
    }

    // Persist allergy selections whenever they change
    LaunchedEffect(selectedAllergiesByMember, selectedAllergyMemberIdState.value, allergyStepIndex) {
        if (isRestored && step == OnboardingStep.ADD_FAMILY_ALLERGIES) {
            val snapshot = selectedAllergiesByMember.mapValues { it.value.toSet() }
            Log.d(
                "OnboardingAllergies",
                "[PERSIST_EFFECT] step=$step isRestored=$isRestored " +
                    "selectedMember=${selectedAllergyMemberIdState.value} " +
                    "stepIndex=$allergyStepIndex selections=$snapshot"
            )
            persistence.setAllergySelectionsState(
                selectedAllergiesByMember = snapshot,
                selectedAllergyMemberId = selectedAllergyMemberIdState.value,
                allergyStepIndex = allergyStepIndex
            )
        }
    }
    // When true, show the fine‑tune decision screen between Life Style and Nutrition
    var showFineTuneDecision by remember { mutableStateOf(false) }
    // When true, show the summary screen with floating robot after completing fine-tune flow
    var showSummaryScreen by remember { mutableStateOf(false) }

    // Exit onboarding after showing summary screen for 3 seconds
    LaunchedEffect(showSummaryScreen) {
        if (showSummaryScreen) {
            delay(3000) // Show summary screen for 3 seconds
            onExitOnboarding()
        }
    }

    if (step == OnboardingStep.GET_STARTED) {
        GetStatedScreen(
            onGetStarted = { vm.navigateTo(OnboardingStep.SIGN_IN_INITIAL) }
        )
        return
    }

    val backgroundKey = when (step) {
        OnboardingStep.SIGN_IN_INITIAL,
        OnboardingStep.SIGN_IN_SOCIAL_LOGIN -> 1
        OnboardingStep.SIGN_IN_INVITE_CODE,
        OnboardingStep.SIGN_IN_ENTER_INVITE_CODE -> 2
        OnboardingStep.SIGN_IN_WHO_IS_THIS_FOR -> 3
        OnboardingStep.ADD_FAMILY_WELCOME,
        OnboardingStep.ADD_FAMILY_NAME,
        OnboardingStep.ADD_FAMILY_AVATAR_PICKER,
        OnboardingStep.ADD_FAMILY_AVATAR_GENERATING,
        OnboardingStep.ADD_FAMILY_ALL_SET_OR_MORE,
        OnboardingStep.ADD_FAMILY_EDIT_MEMBER -> 4
        OnboardingStep.FALLING_CAPSULES -> 5
        OnboardingStep.ADD_FAMILY_ALLERGIES -> 6
        else -> 0
    }

    // Keep addFamilyGeneratedAvatarUrl in sync with memoji Success so it can be restored.
    LaunchedEffect(emojiState) {
        if (emojiState is MemojiGenState.Success) {
            vm.addFamilyGeneratedAvatarUrl = (emojiState as MemojiGenState.Success).imageUrl
            vm.memojiGenerationCompleted = true
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        OnboardingShell(
            onDismissRequest = onExitOnboarding,
            horizontalPaddingEnabled = step != OnboardingStep.ADD_FAMILY_AVATAR_PICKER && step != OnboardingStep.ADD_FAMILY_ALLERGIES,
            showFocusedShadow = step == OnboardingStep.SIGN_IN_INITIAL ||
                step == OnboardingStep.SIGN_IN_SOCIAL_LOGIN,
            baseBottomPaddingOverride = if (step == OnboardingStep.ADD_FAMILY_ALLERGIES) 8.dp else null,
            onSheetHeightChanged = { sheetHeight = it },
            backgroundContent = {
            AnimatedContent(
                targetState = backgroundKey,
                label = "onboardingBackground",
                transitionSpec = {
                    fadeIn(animationSpec = tween(300, easing = FastOutSlowInEasing)) togetherWith
                        fadeOut(animationSpec = tween(300, easing = FastOutSlowInEasing))
                }
            ) { k ->
                when (k) {
                    1 -> {
                        SignInBackground(imageRes = R.drawable.iphone_app_img, showLogo = true)
                    }
                    2 -> {
                        SignInBackground(
                            imageRes = R.drawable.welcome_family_img,
                            showLogo = false,
                            title = "Welcome to IngrediFam !",
                            subtitle = "Join your family space and personalize\nfood choices together.",
                            aspectRatio = 1f
                        )
                    }
                    3 -> {
                        SignInBackground(
                            imageRes = R.drawable.welcome_family_and_me_img,
                            showLogo = false,
                            title = "Welcome to Ingredicheck",
                            subtitle = "Create a space for yourself or the people\nyou care about.",
                            aspectRatio = 1f
                        )
                    }
                    4 -> {
                        val members = vm.familyOverviewMembers.toList()

                        if (members.isNotEmpty()) {
                            FamilyOverviewBackground(
                                members = members,
                                bottomSheetHeight = sheetHeight,
                                // In onboarding flow, do NOT allow immediate "Leave Family"
                                // so onLeaveFamily is left null here.
                                onInvite = { member ->
                                    memberToInvite = member
                                },
                                onEditMember = { member ->
                                    Log.d(
                                        "OnboardingHost",
                                        "Edit member tapped id=${member.id}, name=${member.name}"
                                    )
                                    vm.editingMemberId = member.id
                                    vm.addFamilyName = member.name
                                    vm.addFamilyAvatarId = member.avatarId
                                    vm.addFamilyGeneratedAvatarUrl = member.generatedAvatarUrl
                                    val selections = buildMap<Int, String> {
                                        if (member.avatarId.isNotBlank()) {
                                            put(0, member.avatarId)
                                        }
                                        if (member.backgroundColorId.isNotBlank()) {
                                            put(5, member.backgroundColorId)
                                        }
                                    }
                                    if (selections.isNotEmpty()) {
                                        vm.addFamilyAvatarSelections = selections
                                    }
                                    vm.navigateTo(OnboardingStep.ADD_FAMILY_EDIT_MEMBER)
                                }
                            )
                        } else {
                            SignInBackground(
                                imageRes = R.drawable.family_img_add_family,
                                showLogo = false,
                                title = "Getting Started!",
                                subtitle = "Add profiles so IngredientCheck can personalize results for each person.",
                                aspectRatio = 1f
                            )
                        }
                    }
                    5 -> {
                        FallingCapsulesScreen(
                            modifier = Modifier.fillMaxSize().background(Color.White),
                            bottomInset = sheetHeight
                        )
                    }
                    6 -> {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color(0xFFF2F2F7))
                        ) {
                            if (dynamicStepsLoaded && allergySteps.isNotEmpty()) {
                            Column(
                                modifier = Modifier.fillMaxSize()
                            ) {
                         Spacer(modifier = Modifier.height(40.dp))

                                // Animate progress based on current allergyStepIndex.
                                // NOTE: The fine‑tune decision screen between Life Style and Nutrition
                                // does NOT advance allergyStepIndex, so progress will not increase
                                // while that screen is shown.
                                val rawProgress =
                                    if (allergySteps.size <= 1) 1f
                                    else allergyStepIndex.toFloat() / (allergySteps.size - 1).coerceAtLeast(1)
                                val animatedProgress by animateFloatAsState(
                                    targetValue = rawProgress.coerceIn(0f, 1f),
                                    animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing),
                                    label = "allergyProgress"
                                )

                                   AnimatedProgressLine(
                                       progress = animatedProgress,
                                       modifier = Modifier.padding(horizontal = 20.dp)
                                   )
//                                Spacer(modifier = Modifier.height(10.dp))

                                   // Hide CapsuleStepperRow when summary screen is shown
                                   if (!showSummaryScreen) {
                                       CapsuleStepperRow(
                                           steps = allergySteps,
                                           activeIndex = allergyStepIndex,
                                           onStepClick = { clickedIndex ->
                                               // Only allow jumping to steps the user has already visited.
                                               val clamped = clickedIndex.coerceIn(0, allergySteps.lastIndex)
                                               if (clamped <= allergyStepIndex) {
                                                   allergyStepIndex = clamped
                                                   showFineTuneDecision = false
                                               }
                                           }
                                       )
                                   }

                                Spacer(modifier = Modifier.height(10.dp))

                                // Show a scrollable list of CapsuleSkeletonBox cards.
                                // Steps 0–9: Allergies, Intolerances, Health, Life Stage, Region, Avoid, LifeStyle, Nutrition, Ethical, Taste.
                                val hasAnySelections = selectedAllergies.isNotEmpty()
                                val maxStepIndex = allergySteps.lastIndex

                                // Decide which step indices should render cards.
                                val cardSteps: List<Int> = if (hasAnySelections) {
                                    // Only steps that have at least one selected chip (including Region, Avoid, LifeStyle, Nutrition).
                                    (0..maxStepIndex).filter { stepIndex ->
                                        val stepChipIds = OnboardingChipData
                                            .chipsForStep(stepIndex)
                                            .map { it.id }
                                            .toSet()
                                        selectedAllergies.any { it in stepChipIds }
                                    }
                                } else {
                                    // No selections yet – show only a few empty placeholders so the list is not scroll-heavy.
                                    val upper = minOf(maxStepIndex, 3)
                                    (0..upper).toList()
                                }

                                val cardsListState = rememberLazyListState()

                                // Small avatar(s) to show who this capsule applies to (Everyone or a member)
                                val activeMemberId = selectedAllergyMemberIdState.value
                                val everyoneIdCaps = EVERYONE_MEMBER_ID
                                val activeMember = vm.familyOverviewMembers
                                    .firstOrNull { it.id == activeMemberId }

                                val trailingAvatarContent: (@Composable () -> Unit)? =
                                    when {
                                        activeMemberId == everyoneIdCaps -> {
                                            { CapsuleEveryoneAvatarSmall() }
                                        }

                                        activeMember != null -> {
                                            { CapsuleMemberAvatarSmall(activeMember) }
                                        }

                                        else -> null
                                    }

                                LazyColumn(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .weight(1f, fill = false),
                                    state = cardsListState,
                                    verticalArrangement = Arrangement.spacedBy(16.dp),
                                    contentPadding = PaddingValues(horizontal = 20.dp),
                                    userScrollEnabled = hasAnySelections
                                ) {
                                    items(cardSteps) { stepIndex ->
                                        val stepChipIds = OnboardingChipData
                                            .chipsForStep(stepIndex)
                                            .map { it.id }
                                            .toSet()
                                        val selectedChipsForStep = selectedAllergies
                                            .filter { it in stepChipIds }
                                            .toSet()

                                        CapsuleSkeletonBox(
                                            modifier = Modifier.fillMaxWidth(),
                                            selectedChipIds = if (hasAnySelections) {
                                                // When anything is selected, only show chips for this step.
                                                selectedChipsForStep
                                            } else {
                                                // Initial empty state – show skeletons.
                                                emptySet()
                                            },
                                            sectionTitle = allergySteps
                                                .getOrNull(stepIndex)
                                                ?.title ?: "Step ${stepIndex + 1}",
                                            sectionIconRes = allergySteps
                                                .getOrNull(stepIndex)
                                                ?.iconRes ?: R.drawable.ic_step_allergies,
                                            trailingAvatarsForChip = { chipId ->
                                                // Derive which members have this chip selected
                                                val memberIds = selectedAllergiesByMember
                                                    .mapNotNull { (memberKey, chips) ->
                                                        if (chips.contains(chipId)) memberKey else null
                                                    }
                                                    .toSet()
                                                if (memberIds.isEmpty()) {
                                                    null
                                                } else {
                                                    {
                                                        CapsuleChipMemberAvatars(
                                                            memberIds = memberIds,
                                                            members = vm.familyOverviewMembers.toList()
                                                        )
                                                    }
                                                }
                                            }
                                        )
                                    }

                                    // When there are selections, add a bit of bottom spacing so the list
                                    // has room to scroll and bring the current step's card toward the top.
                                    if (hasAnySelections) {
                                        item {
                                            // Extra bottom space so later steps (e.g., Ethical, Taste)
                                            // have enough scroll range to move toward the top.
                                            Spacer(modifier = Modifier.height(140.dp))
                                        }
                                    }
                                }

                                // When user switches steps, auto-scroll so that the *current* step's card
                                // is visible – but only starting from Health Conditions and only after
                                // the user has actually selected chips for that step.
                                LaunchedEffect(allergyStepIndex, cardSteps, hasAnySelections, allergySelectionRevision) {
                                    if (!hasAnySelections || cardSteps.isEmpty()) return@LaunchedEffect

                                    val layoutInfo = cardsListState.layoutInfo
                                    // If everything fits in the viewport (all items visible), there's no scroll range.
                                    if (layoutInfo.totalItemsCount <= layoutInfo.visibleItemsInfo.size) {
                                        return@LaunchedEffect
                                    }

                                    // Find the index of the Health Conditions step from dynamic JSON.
                                    val healthConditionsIndex = allergySteps.indexOfFirst { it.id == "healthConditions" }
                                        .takeIf { it >= 0 } ?: 2

                                    // When we're still early in the flow (only Allergies/Intolerances filled),
                                    // keep the list static. But if the user has already progressed to later
                                    // steps (Health Conditions or beyond) and then taps back on a capsule
                                    // like Allergies or Intolerances, allow scrolling back down to those cards.
                                    val hasLaterCards = cardSteps.any { it >= healthConditionsIndex }
                                    if (allergyStepIndex < healthConditionsIndex && !hasLaterCards) {
                                        return@LaunchedEffect
                                    }

                                    val clampedStep = allergyStepIndex.coerceIn(0, allergySteps.lastIndex)

                                    // Only scroll when the current step actually has selected chips,
                                    // so we don't auto-scroll for an empty placeholder card.
                                    val currentStepChipIds = OnboardingChipData
                                        .chipsForStep(clampedStep)
                                        .map { it.id }
                                        .toSet()
                                    val stepHasSelection = selectedAllergies.any { it in currentStepChipIds }
                                    if (!stepHasSelection) return@LaunchedEffect

                                    // Map the active step index to its position in the filtered card list.
                                    val targetIndex = cardSteps.indexOf(clampedStep)
                                    if (targetIndex >= 0) {
                                        cardsListState.animateScrollToItem(targetIndex)
                                    }
                                }
                            }
                            } else {
                                // Don't render stepper/cards until dynamic JSON is loaded so restored selections are visible.
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator()
                                }
                            }
                        }
                    }
                }
            }
        },
        sheetContent = {
            AnimatedContent(
                targetState = step,
                label = "onboardingSheet",
                transitionSpec = {
                    val duration = 300
                    val easing = FastOutSlowInEasing
                    fadeIn(animationSpec = tween(duration, easing = easing)) togetherWith
                            fadeOut(animationSpec = tween(duration, easing = easing))
                }
            ) { s ->
                Column {
                    when (s) {
                        OnboardingStep.FALLING_CAPSULES -> {
                            AddFamilyLetsGoSheet(
                                onLetsGo = {
                                    vm.navigateTo(OnboardingStep.ADD_FAMILY_ALLERGIES)
                                }
                            )
                        }
                        OnboardingStep.ADD_FAMILY_ALLERGIES -> {
                            if (!dynamicStepsLoaded || allergySteps.isEmpty()) {
                                // Don't show sheet content until steps are loaded so chips and question are visible (e.g. after restart).
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(32.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator()
                                }
                            } else {
                            // Compute per-member selections for the bottom sheet:
                            // the sheet should reflect ONLY what the currently selected member
                            // (or Everyone) has chosen, not the union across all members.
                            val activeMemberId = selectedAllergyMemberIdState.value
                            val activeMemberKey = if (activeMemberId.isBlank()) EVERYONE_MEMBER_ID else activeMemberId

                            // Sync activeMemberSelections whenever activeMemberKey or revision changes
                            LaunchedEffect(activeMemberKey, allergySelectionRevision) {
                                val latest = selectedAllergiesByMember[activeMemberKey]?.toSet() ?: emptySet()
                                if (activeMemberSelections != latest) {
                                    activeMemberSelections = latest
                                    Log.d(
                                        "OnboardingAllergies",
                                        "[SYNC] activeMemberSelections updated to=$latest for memberKey=$activeMemberKey revision=$allergySelectionRevision"
                                    )
                                }
                            }

                            Log.d(
                                "OnboardingAllergies",
                                "[SHEET RECOMPOSE] memberKey=$activeMemberKey " +
                                    "activeMemberSelections=$activeMemberSelections " +
                                    "revision=$allergySelectionRevision " +
                                    "selectedAllergiesByMember.keys=${selectedAllergiesByMember.keys}"
                            )

                            // Key only by member so switching member resets the sheet; do NOT key by
                            // selections so that chip toggles do not recreate the sheet (preserves
                            // stacked card order when user selects chips on 2nd/3rd card).
                            key(activeMemberKey) {
                                AddAllergiesSheet(
                                    members = vm.familyOverviewMembers.toList(),
                                    selectedMemberId = selectedAllergyMemberIdState.value,
                                    selectedAllergies = activeMemberSelections,
                                    onMemberSelected = {
                                        val oldMemberKey = if (selectedAllergyMemberIdState.value.isBlank()) EVERYONE_MEMBER_ID else selectedAllergyMemberIdState.value
                                        val newMemberKey = if (it.isBlank()) EVERYONE_MEMBER_ID else it
                                        Log.d(
                                            "OnboardingAllergies",
                                            "[MEMBER SWITCH] from=$oldMemberKey to=$newMemberKey " +
                                                "selectionsForNewMember=${selectedAllergiesByMember[newMemberKey]?.toSet()}"
                                        )
                                        selectedAllergyMemberIdState.value = it
                                        // Update activeMemberSelections immediately when switching members
                                        activeMemberSelections = selectedAllergiesByMember[newMemberKey]?.toSet() ?: emptySet()
                                    },
                                    onToggleAllergy = { allergyId ->
                                        val activeMemberId = selectedAllergyMemberIdState.value
                                        val memberKey = if (activeMemberId.isBlank()) EVERYONE_MEMBER_ID else activeMemberId

                                        Log.d(
                                            "OnboardingAllergies",
                                            "[TAP] START chip=$allergyId memberKey=$memberKey " +
                                                "beforeChips=${selectedAllergiesByMember[memberKey]?.toSet()}"
                                        )

                                        // Copy out, mutate, then write back so SnapshotStateMap sees a change
                                        // and the sheet recomposes. Mutating the inner MutableSet in place
                                        // does not trigger recomposition.
                                        val chipsForMember =
                                            (selectedAllergiesByMember[memberKey]?.toMutableSet() ?: mutableSetOf())
                                        if (chipsForMember.contains(allergyId)) {
                                            chipsForMember.remove(allergyId)
                                            if (chipsForMember.isEmpty()) {
                                                selectedAllergiesByMember.remove(memberKey)
                                            } else {
                                                selectedAllergiesByMember[memberKey] = chipsForMember
                                            }
                                        } else {
                                            chipsForMember.add(allergyId)
                                            selectedAllergiesByMember[memberKey] = chipsForMember
                                        }

                                        // Rebuild the flat selectedAllergies list as the union of all chips
                                        // selected by any member (used only for background capsules).
                                        selectedAllergies.clear()
                                        selectedAllergies.addAll(
                                            selectedAllergiesByMember.values
                                                .flatMap { it }
                                                .toSet()
                                        )

                                        // Immediately update activeMemberSelections if this is for the active member
                                        // BEFORE incrementing revision so the key block sees the updated value
                                        if (memberKey == activeMemberKey) {
                                            activeMemberSelections = selectedAllergiesByMember[memberKey]?.toSet() ?: emptySet()
                                        }
                                        
                                        allergySelectionRevision++
                                        
                                        Log.d(
                                            "OnboardingAllergies",
                                            "[TAP] END chip=$allergyId memberKey=$memberKey " +
                                                "afterChips=${selectedAllergiesByMember[memberKey]?.toSet()} " +
                                                "revision=$allergySelectionRevision " +
                                                "activeMemberSelections=$activeMemberSelections"
                                        )
                                        },
                                    onNext = {
                                        // Between Life Style (index 6) and Nutrition (index 7),
                                        // show a dedicated fine‑tune decision screen that does
                                        // NOT advance progress until the user confirms.
                                        if (allergyStepIndex == 6 && !showFineTuneDecision) {
                                            showFineTuneDecision = true
                                        } else {
                                            showFineTuneDecision = false
                                            if (allergyStepIndex < allergySteps.lastIndex) {
                                                allergyStepIndex++
                                            } else {
                                                // Sync dietary preferences to backend (same as iOS) before showing summary
                                                val preferenceText = buildDietaryPreferenceText(selectedAllergiesByMember)
                                                Log.d("OnboardingAllergies", "[DietaryPreference] onNext complete: syncing textLength=${preferenceText.length}")
                                                authViewModel.syncDietaryPreferencesFromOnboarding(preferenceText)
                                                authViewModel.syncFoodNotesFromOnboarding(selectedAllergiesByMember.mapValues { it.value.toSet() })
                                                showSummaryScreen = true
                                            }
                                        }
                                    },
                                    onSkipPreferences = {
                                        // User tapped "All Set!" on the fine‑tune decision screen:
                                        // sync preferences to backend (same as iOS) then show summary screen.
                                        val preferenceText = buildDietaryPreferenceText(selectedAllergiesByMember)
                                        Log.d("OnboardingAllergies", "[DietaryPreference] onSkipPreferences: syncing textLength=${preferenceText.length}")
                                        authViewModel.syncDietaryPreferencesFromOnboarding(preferenceText)
                                        authViewModel.syncFoodNotesFromOnboarding(selectedAllergiesByMember.mapValues { it.value.toSet() })
                                        showFineTuneDecision = false
                                        showSummaryScreen = true
                                    },
                                    showFineTuneDecision = showFineTuneDecision,
                                    showSummaryScreen = showSummaryScreen,
                                    questionStepIndex = allergyStepIndex
                                )
                            }
                            }
                        }
                        OnboardingStep.SIGN_IN_INITIAL -> {
                            SignInInitialSheet(
                                onExistingUserContinue = {
                                    vm.navigateTo(OnboardingStep.SIGN_IN_SOCIAL_LOGIN)
                                },
                                onStartNew = {
                                    vm.navigateTo(OnboardingStep.SIGN_IN_INVITE_CODE)
                                }
                            )
                        }

                        OnboardingStep.SIGN_IN_SOCIAL_LOGIN -> {
                            SignInSocialLoginSheet(
                                onBackClick = handleBack,
                                onGoogleClick = {
                                    if (activity != null) {
                                        val client = GoogleAuthDataSource.getClient(activity)
                                        googleLauncher.launch(client.signInIntent)
                                    }
                                },
                                onAppleClick = {
                                    if (activity != null) {
                                        val redirectUri =
                                            "${AuthEnv.OAUTH_REDIRECT_SCHEME}://${AuthEnv.OAUTH_REDIRECT_HOST}"
                                        val authUrl = Uri.parse(AuthEnv.SUPABASE_URL).buildUpon()
                                            .appendPath("auth")
                                            .appendPath("v1")
                                            .appendPath("authorize")
                                            .appendQueryParameter("provider", "apple")
                                            .appendQueryParameter("redirect_to", redirectUri)
                                            .appendQueryParameter("flow_type", "implicit")
                                            .build()

                                        val intent = Intent(
                                            activity,
                                            AppleLoginWebViewActivity::class.java
                                        ).apply {
                                            putExtra("auth_url", authUrl.toString())
                                            putExtra("redirect_uri", redirectUri)
                                        }
                                        appleLauncher.launch(intent)
                                    }
                                }
                            )
                        }

                        OnboardingStep.SIGN_IN_INVITE_CODE -> {
                            SignInInviteCodeSheet(
                                onBackClick = handleBack,
                                onEnterInviteCode = {
                                    vm.navigateTo(OnboardingStep.SIGN_IN_ENTER_INVITE_CODE)
                                },
                                onNoContinue = {
                                    vm.navigateTo(OnboardingStep.SIGN_IN_WHO_IS_THIS_FOR)
                                }
                            )
                        }

                        OnboardingStep.SIGN_IN_ENTER_INVITE_CODE -> {
                            SignInEnterInviteCodeSheet(
                                inviteCode = vm.inviteCode,
                                isError = vm.inviteCodeError,
                                onInviteCodeChange = { vm.inviteCode = it },
                                onBackClick = handleBack,
                                onVerifyContinue = {
                                    if (vm.inviteCode == "ABCXYZ") {
                                        vm.inviteCodeError = true
                                    } else {
                                        vm.navigateTo(OnboardingStep.SIGN_IN_WHO_IS_THIS_FOR)
                                    }
                                }
                            )
                        }

                        OnboardingStep.SIGN_IN_WHO_IS_THIS_FOR -> {
                            SignInWhoIsThisForSheet(
                                onBackClick = handleBack,
                                isJustMeLoading = isCreatingBiteBuddyFamily,
                                isAddFamilyLoading = false,
                                isAuthLoading = isAuthLoading,
                                onJustMe = {
                                    Log.d("OnboardingHost", "Just Me: Creating Bite Buddy family then navigating to FALLING_CAPSULES")
                                    authViewModel.debugLogCurrentSession("Just Me clicked")
                                    isCreatingBiteBuddyFamily = true
                                    val req = buildBiteBuddyFamilyRequest()
                                    authViewModel.createFamily(req) { result ->
                                        isCreatingBiteBuddyFamily = false
                                        result.fold(
                                            onSuccess = {
                                                Log.d("OnboardingHost", "Just Me: Bite Buddy family created, navigating to FALLING_CAPSULES")
                                                vm.navigateTo(OnboardingStep.FALLING_CAPSULES)
                                            },
                                            onFailure = { e ->
                                                Log.e("OnboardingHost", "Just Me: createFamily (Bite Buddy) failed", e)
                                                Toast.makeText(
                                                    context,
                                                    e.localizedMessage ?: "Failed to create profile",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                        )
                                    }
                                },
                                onAddFamily = {
                                    authViewModel.debugLogCurrentSession("Add Family clicked")
                                    vm.navigateTo(OnboardingStep.ADD_FAMILY_WELCOME)
                                }
                            )
                        }

                        OnboardingStep.ADD_FAMILY_WELCOME -> {
                            AddFamilyWelcomeSheet(
                                onBackClick = handleBack,
                                onContinue = {
                                    authViewModel.debugLogCurrentSession("Add Family welcome continue")
                                    vm.navigateTo(OnboardingStep.ADD_FAMILY_NAME)
                                }
                            )
                        }

                        OnboardingStep.ADD_FAMILY_NAME -> {
                            AddFamilyNameSheet(
                                name = vm.addFamilyName,
                                selectedAvatarId = vm.addFamilyAvatarId,
                                generatedAvatarUrl = vm.addFamilyGeneratedAvatarUrl,
                                onNameChange = { vm.addFamilyName = it },
                                onAvatarSelect = {
                                    vm.addFamilyAvatarId = it
                                    vm.addFamilyAvatarSelections = mapOf(0 to it)
                                },
                                onAddAvatarClick = {
                                    vm.addFamilyAvatarId = ""
                                    vm.addFamilyAvatarSelections = emptyMap()
                                    vm.navigateTo(OnboardingStep.ADD_FAMILY_AVATAR_PICKER)
                                },
                                onBackClick = handleBack,
                                onContinue = {
                                    if (isCreatingFamily) {
                                        return@AddFamilyNameSheet
                                    }
                                    focusManager.clearFocus(force = true)
                                    authViewModel.debugLogCurrentSession("Add Family name continue")
                                    val editingId = vm.editingMemberId
                                    if (editingId != null) {
                                        val existing =
                                            vm.familyOverviewMembers.firstOrNull { it.id == editingId }
                                        val draft = vm.currentDraftFamilyMemberOrNull()
                                        if (existing == null || draft == null) {
                                            Toast.makeText(
                                                context,
                                                "Please enter a name",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                            return@AddFamilyNameSheet
                                        }

                                        val updated = existing.copy(
                                            name = draft.name,
                                            avatarId = draft.avatarId,
                                            generatedAvatarUrl = draft.generatedAvatarUrl,
                                            backgroundColorId = draft.backgroundColorId
                                        )
                                        vm.updateFamilyOverviewMember(updated)
                                        vm.clearAddFamilyDraft()
                                        vm.editingMemberId = null
                                        vm.navigateTo(OnboardingStep.ADD_FAMILY_ALL_SET_OR_MORE)
                                        return@AddFamilyNameSheet
                                    }
                                    val isFirstMember = vm.familyOverviewMembers.isEmpty()
                                    if (!isFirstMember) {
                                        val draft = vm.currentDraftFamilyMemberOrNull()
                                        if (draft == null) {
                                            Toast.makeText(
                                                context,
                                                "Please enter a name",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                            return@AddFamilyNameSheet
                                        }

                                        // Persist additional member to backend family before inviting.
                                        val memberDto = FamilyMemberDto(
                                            id = draft.id,
                                            name = draft.name,
                                            color = draft.colorHex.ifBlank { "#BAE1FF" },
                                            joined = false,
                                            invitePending = null,
                                            imageFileHash = draft.generatedAvatarUrl.trim().ifBlank { null }
                                        )

                                        isCreatingFamily = true
                                        authViewModel.addFamilyMember(memberDto) { result ->
                                            isCreatingFamily = false
                                            result.fold(
                                                onSuccess = {
                                                    Log.d(
                                                        "OnboardingHost",
                                                        "addMember persisted for id=${memberDto.id}, name=${memberDto.name}"
                                                    )
                                                    vm.commitDraftFamilyMember()
                                                    vm.clearAddFamilyDraft()
                                                    vm.navigateTo(OnboardingStep.ADD_FAMILY_ALL_SET_OR_MORE)
                                                },
                                                onFailure = { e ->
                                                    Log.e(
                                                        "OnboardingHost",
                                                        "addMember failed for id=${memberDto.id}, name=${memberDto.name}",
                                                        e
                                                    )
                                                    Toast.makeText(
                                                        context,
                                                        e.localizedMessage ?: "Failed to add member",
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                }
                                            )
                                        }
                                        return@AddFamilyNameSheet
                                    }

                                    val draft = vm.currentDraftFamilyMemberOrNull()
                                    if (draft == null) {
                                        Toast.makeText(
                                            context,
                                            "Please enter a name",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        return@AddFamilyNameSheet
                                    }

                                    val req = buildCreateFamilyRequestFromMembers(listOf(draft))
                                    if (req == null) {
                                        Toast.makeText(
                                            context,
                                            "Please enter a name",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        return@AddFamilyNameSheet
                                    }

                                    isCreatingFamily = true
                                    authViewModel.createFamily(req) { result ->
                                        isCreatingFamily = false
                                        result.fold(
                                            onSuccess = {
                                                vm.commitDraftFamilyMember()
                                                vm.clearAddFamilyDraft()
                                            },
                                            onFailure = { e ->
                                                Log.e(
                                                    "OnboardingHost",
                                                    "createFamily failed on first member",
                                                    e
                                                )
                                                Toast.makeText(
                                                    context,
                                                    e.localizedMessage ?: "Failed to create family",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                        )
                                    }
                                },
                                isAdditionalMember = vm.familyOverviewMembers.isNotEmpty(),
                                isLoading = isCreatingFamily,
                                showBackArrow = vm.familyOverviewMembers.size >= 2,
                                isEditing = vm.editingMemberId != null
                            )
                        }

                        OnboardingStep.ADD_FAMILY_ALL_SET_OR_MORE -> {
                            AddFamilyAllSetOrMoreSheet(
                                onAllSet = { vm.navigateTo(OnboardingStep.FALLING_CAPSULES) },
                                onAddMore = { vm.navigateTo(OnboardingStep.ADD_FAMILY_NAME) }
                            )
                        }

                        OnboardingStep.ADD_FAMILY_EDIT_MEMBER -> {
                            val editingMember =
                                vm.familyOverviewMembers.firstOrNull { it.id == vm.editingMemberId }
                            val draft = vm.currentDraftFamilyMemberOrNull()
                            val editAvatarBg =
                                if (editingMember != null) avatarBackgroundColorForId(editingMember.backgroundColorId)
                                else avatarBackgroundColorForId(vm.addFamilyAvatarSelections[5].orEmpty())
                            val isSaveEnabled = editingMember != null && draft != null && (
                                draft.name != editingMember.name ||
                                    draft.avatarId != editingMember.avatarId ||
                                    draft.generatedAvatarUrl != editingMember.generatedAvatarUrl
                                )
                            EditFamilyMemberSheet(
                                name = vm.addFamilyName,
                                selectedAvatarId = vm.addFamilyAvatarId,
                                generatedAvatarUrl = vm.addFamilyGeneratedAvatarUrl,
                                avatarBackgroundColor = editAvatarBg,
                                isSaveEnabled = isSaveEnabled,
                                onNameChange = { vm.addFamilyName = it },
                                onAvatarSelect = {
                                    vm.addFamilyAvatarId = it
                                    vm.addFamilyAvatarSelections = vm.addFamilyAvatarSelections + (0 to it)
                                },
                                onAddAvatarClick = {
                                    vm.addFamilyAvatarId = ""
                                    vm.addFamilyAvatarSelections = emptyMap()
                                    vm.navigateTo(OnboardingStep.ADD_FAMILY_AVATAR_PICKER)
                                },
                                onBackClick = handleBack,
                                onSave = {
                                    focusManager.clearFocus(force = true)
                                    val editingId = vm.editingMemberId
                                    if (editingId == null) return@EditFamilyMemberSheet
                                    val existing = vm.familyOverviewMembers.firstOrNull { it.id == editingId }
                                    val draft = vm.currentDraftFamilyMemberOrNull()
                                    if (existing == null || draft == null) {
                                        Toast.makeText(context, "Please enter a name", Toast.LENGTH_SHORT).show()
                                        return@EditFamilyMemberSheet
                                    }
                                    val updated = existing.copy(
                                        name = draft.name,
                                        avatarId = draft.avatarId,
                                        generatedAvatarUrl = draft.generatedAvatarUrl,
                                        backgroundColorId = draft.backgroundColorId
                                    )
                                    vm.updateFamilyOverviewMember(updated)
                                    vm.clearAddFamilyDraft()
                                    vm.editingMemberId = null
                                    vm.back()
                                },
                                largeAvatarContent = {
                                    val trimmedUrl = vm.addFamilyGeneratedAvatarUrl.trim()
                                    val res = lc.fungee.Ingredicheck.onboarding.data.OnboardingChipData.avatarResOrNull(vm.addFamilyAvatarId.trim())
                                    val circleBg = editAvatarBg
                                    when {
                                        trimmedUrl.isNotBlank() -> {
                                            SubcomposeAsyncImage(
                                                model = trimmedUrl,
                                                contentDescription = null,
                                                modifier = Modifier.fillMaxSize().clip(CircleShape)
                                            ) {
                                                when (painter.state) {
                                                    is coil.compose.AsyncImagePainter.State.Loading -> {
                                                        Box(
                                                            modifier = Modifier.fillMaxSize().background(circleBg),
                                                            contentAlignment = Alignment.Center
                                                        ) {
                                                            CircularProgressIndicator(
                                                                modifier = Modifier.size(24.dp),
                                                                strokeWidth = 2.dp,
                                                                color = Primary800
                                                            )
                                                        }
                                                    }
                                                    else -> SubcomposeAsyncImageContent()
                                                }
                                            }
                                        }
                                        res != null -> {
                                            Image(
                                                painter = painterResource(id = res),
                                                contentDescription = null,
                                                modifier = Modifier.fillMaxSize().clip(CircleShape),
                                                contentScale = ContentScale.Crop
                                            )
                                        }
                                        else -> {
                                            val bg = familyPlaceholderColor(vm.addFamilyName.ifBlank { "?" })
                                            Box(
                                                modifier = Modifier.fillMaxSize().clip(CircleShape).background(bg),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                val initial = vm.addFamilyName.trim().firstOrNull()?.uppercase() ?: "?"
                                                Text(
                                                    text = initial,
                                                    style = TextStyle(
                                                        fontFamily = Manrope,
                                                        fontWeight = FontWeight.Bold,
                                                        fontSize = 36.sp,
                                                        color = Color.White
                                                    )
                                                )
                                            }
                                        }
                                    }
                                }
                            )
                        }

                        OnboardingStep.ADD_FAMILY_AVATAR_PICKER -> {
                            AddFamilyAvatarPickerSheet(
                                displayName = vm.addFamilyName,
                                selections = vm.addFamilyAvatarSelections,
                                onBackClick = handleBack,
                                onAvatarSelected = {
                                    vm.addFamilyAvatarSelections = it
                                },
                                onGenerateClick = {
                                    vm.memojiGenerationCompleted = false
                                    authViewModel.generateAddFamilyMemoji(vm.addFamilyAvatarSelections)
                                    vm.navigateTo(OnboardingStep.ADD_FAMILY_AVATAR_GENERATING)
                                }
                            )
                        }

                        OnboardingStep.ADD_FAMILY_AVATAR_GENERATING -> {
                            AddFamilyAvatarGeneratingSheet(
                                state = emojiState,
                                selections = vm.addFamilyAvatarSelections,
                                onBackClick = handleBack,
                                onRetry = { authViewModel.generateAddFamilyMemoji(vm.addFamilyAvatarSelections) },
                                onRegenerate = { vm.back() },
                                onAssign = { imageUrl ->
                                    vm.addFamilyGeneratedAvatarUrl = imageUrl
                                    if (vm.editingMemberId != null) {
                                        vm.navigateTo(OnboardingStep.ADD_FAMILY_EDIT_MEMBER)
                                        return@AddFamilyAvatarGeneratingSheet
                                    }
                                    if (isCreatingFamily) return@AddFamilyAvatarGeneratingSheet
                                    val isFirstMember = vm.familyOverviewMembers.isEmpty()
                                    if (!isFirstMember) {
                                        vm.commitDraftFamilyMember()
                                        vm.clearAddFamilyDraft()
                                        vm.navigateTo(OnboardingStep.ADD_FAMILY_ALL_SET_OR_MORE)
                                        return@AddFamilyAvatarGeneratingSheet
                                    }
                                    val draft = vm.currentDraftFamilyMemberOrNull()
                                    if (draft == null) {
                                        Toast.makeText(
                                            context,
                                            "Please enter a name",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        return@AddFamilyAvatarGeneratingSheet
                                    }
                                    val req = buildCreateFamilyRequestFromMembers(listOf(draft))
                                    if (req == null) {
                                        Toast.makeText(
                                            context,
                                            "Please enter a name",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        return@AddFamilyAvatarGeneratingSheet
                                    }
                                    isCreatingFamily = true
                                    authViewModel.createFamily(req) { result ->
                                        isCreatingFamily = false
                                        result.fold(
                                            onSuccess = {
                                                vm.commitDraftFamilyMember()
                                                vm.clearAddFamilyDraft()
                                                vm.navigateTo(OnboardingStep.ADD_FAMILY_NAME)
                                            },
                                            onFailure = { e ->
                                                Log.e(
                                                    "OnboardingHost",
                                                    "createFamily failed on first member (Assign)",
                                                    e
                                                )
                                                Toast.makeText(
                                                    context,
                                                    e.localizedMessage ?: "Failed to create family",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                        )
                                    }
                                }
                            )
                        }

                        OnboardingStep.GET_STARTED -> {
                        }
                    }
                }
            }
        }
        )


        // Overlay with opacity when invite confirmation is shown
        if (memberToInvite != null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .alpha(0.5f)
                    .background(Color.Black)
                    .clickable { memberToInvite = null }
            )
        }

        // Invite confirmation bottom sheet
        memberToInvite?.let { member ->
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.BottomCenter
            ) {
                NonDraggableBottomSheet(
                    onDismissRequest = { memberToInvite = null },
                    horizontalPaddingEnabled = true
                ) {
                    InviteConfirmationSheet(
                        memberName = member.name,
                        onMayBeLater = {
                            if (!isInviting) {
                                vm.setInvitePending(member.id, true)
                                memberToInvite = null
                            }
                        },
                        onInvite = {
                            if (isInviting) return@InviteConfirmationSheet
                            isInviting = true
                            Log.d(
                                "OnboardingHost",
                                "Invite confirmed for memberId=${member.id}, name=${member.name}"
                            )
                            // Ensure family exists on backend before inviting.
                            if (currentFamily != null) {
                                authViewModel.inviteFamilyMember(member.id) { result ->
                                    val code = result.getOrNull()
                                    if (code != null) {
                                        vm.setInvitePending(member.id, true)
                                        memberToInvite = null
                                        shareInviteCode(context, code)
                                    } else {
                                        Toast.makeText(
                                            context,
                                            "Failed to create invite",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                    isInviting = false
                                }
                            } else {
                                val req = buildCreateFamilyRequestFromMembers(vm.familyOverviewMembers.toList())
                                if (req == null) {
                                    authViewModel.inviteFamilyMember(member.id) { result ->
                                        val code = result.getOrNull()
                                        if (code != null) {
                                            vm.setInvitePending(member.id, true)
                                            memberToInvite = null
                                            shareInviteCode(context, code)
                                        } else {
                                            Toast.makeText(
                                                context,
                                                "Failed to create invite",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                        isInviting = false
                                    }
                                } else {
                                    authViewModel.createFamily(req) { createResult ->
                                        createResult.fold(
                                            onSuccess = {
                                                authViewModel.inviteFamilyMember(member.id) { result ->
                                                    val code = result.getOrNull()
                                                    if (code != null) {
                                                        vm.setInvitePending(member.id, true)
                                                        memberToInvite = null
                                                        shareInviteCode(context, code)
                                                    } else {
                                                        Toast.makeText(
                                                            context,
                                                            "Failed to create invite",
                                                            Toast.LENGTH_SHORT
                                                        ).show()
                                                    }
                                                    isInviting = false
                                                }
                                            },
                                            onFailure = {
                                                val msg = it.localizedMessage.orEmpty()
                                                val isDuplicateMemberId =
                                                    msg.contains("members_pkey", ignoreCase = true) ||
                                                        msg.contains("duplicate key", ignoreCase = true)

                                                if (isDuplicateMemberId) {
                                                    Log.e(
                                                        "OnboardingHost",
                                                        "createFamily failed due to duplicate memberId; regenerating ids + retry",
                                                        it
                                                    )

                                                    val idMap = vm.regenerateFamilyOverviewMemberIds()
                                                    val regeneratedMemberId = idMap[member.id] ?: member.id
                                                    val retryReq = buildCreateFamilyRequestFromMembers(vm.familyOverviewMembers.toList())

                                                    if (retryReq == null) {
                                                        isInviting = false
                                                        Toast.makeText(
                                                            context,
                                                            "Failed to retry createFamily",
                                                            Toast.LENGTH_SHORT
                                                        ).show()
                                                        return@createFamily
                                                    }

                                                    authViewModel.createFamily(retryReq) { retryResult ->
                                                        retryResult.fold(
                                                            onSuccess = {
                                                                authViewModel.inviteFamilyMember(regeneratedMemberId) { result ->
                                                                    val code = result.getOrNull()
                                                                    if (code != null) {
                                                                        vm.setInvitePending(regeneratedMemberId, true)
                                                                        memberToInvite = null
                                                                        shareInviteCode(context, code)
                                                                    } else {
                                                                        Toast.makeText(
                                                                            context,
                                                                            "Failed to create invite",
                                                                            Toast.LENGTH_SHORT
                                                                        ).show()
                                                                    }
                                                                    isInviting = false
                                                                }
                                                            },
                                                            onFailure = { retryErr ->
                                                                Log.e(
                                                                    "OnboardingHost",
                                                                    "createFamily retry failed; not inviting memberId=$regeneratedMemberId",
                                                                    retryErr
                                                                )
                                                                isInviting = false
                                                                Toast.makeText(
                                                                    context,
                                                                    retryErr.localizedMessage
                                                                        ?: "Failed to create family",
                                                                    Toast.LENGTH_SHORT
                                                                ).show()
                                                            }
                                                        )
                                                    }

                                                    return@createFamily
                                                }

                                                isInviting = false
                                                Log.e(
                                                    "OnboardingHost",
                                                    "createFamily failed; not inviting memberId=${member.id}",
                                                    it
                                                )
                                                Toast.makeText(
                                                    context,
                                                    it.localizedMessage ?: "Failed to create family",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                        )
                                    }
                                }
                            }
                        },
                        isLoading = isInviting
                    )
                }
            }
        }
    }
}
