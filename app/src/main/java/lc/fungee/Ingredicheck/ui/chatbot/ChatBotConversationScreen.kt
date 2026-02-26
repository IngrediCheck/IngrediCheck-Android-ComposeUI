package lc.fungee.Ingredicheck.ui.chatbot

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview

import lc.fungee.Ingredicheck.R
import lc.fungee.Ingredicheck.ui.components.NonDraggableBottomSheet
import lc.fungee.Ingredicheck.ui.components.buttons.primaryButtonEffect
import lc.fungee.Ingredicheck.ui.theme.Greyscale100
import lc.fungee.Ingredicheck.ui.theme.Greyscale110
import lc.fungee.Ingredicheck.ui.theme.Greyscale140
import lc.fungee.Ingredicheck.ui.theme.Greyscale40
import lc.fungee.Ingredicheck.ui.theme.Greyscale60
import lc.fungee.Ingredicheck.ui.theme.Manrope
import lc.fungee.Ingredicheck.ui.theme.Nunito

private data class ChatMessage(val isUser: Boolean, val text: String)

/**
 * AI chat conversation sheet. Tapping the input opens the keyboard; input stays above keyboard.
 * Sent messages appear on the user (right) side.
 */
@Composable
fun ChatBotConversationScreen(
    modifier: Modifier = Modifier,
    onSkip: () -> Unit = {}
) {
    val focusRequester = remember { FocusRequester() }
    var inputText by remember { mutableStateOf("") }
    val messages = remember {
        mutableStateListOf(
            ChatMessage(false, "Hello!!"),
            ChatMessage(false, "What else did you want to avoid?"),
            ChatMessage(true, "I usually avoid processed snacks."),
            ChatMessage(false, "That's wonderful!\nThanks for sharing. Anything else about\nyour food habits."),
            ChatMessage(true, "Yeah, I follow a mix of ayurvedic and\nseasonal eating.")
        )
    }
    val scrollState = rememberScrollState()

    LaunchedEffect(messages.size) {
        scrollState.animateScrollTo(scrollState.maxValue)
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .fillMaxHeight(0.7f)
            .padding(horizontal = 20.dp),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Text(
                    text = "Skip",
                    fontFamily = Manrope,
                    textDecoration = TextDecoration.Underline,
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp,
                    color = Greyscale110,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.clickable { onSkip() }
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.hugeicons_ai_magic),
                        tint = Color.Unspecified,
                        contentDescription = null,
                        modifier = Modifier.size(30.dp)
                    )
                    Text(
                        text = "Asking with AI suggestions",
                        fontFamily = Nunito,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp,
                        color = Greyscale110,
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Scrollable message list
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .verticalScroll(scrollState),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                val firstBotIndex = messages.indexOfFirst { !it.isUser }
                messages.forEachIndexed { index, msg ->
                    if (msg.isUser) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            UserBubble(text = msg.text)
                        }
                    } else {
                        val isFirstWithAvatar = (index == firstBotIndex)
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            if (isFirstWithAvatar) {
                                Image(
                                    painter = painterResource(id = R.drawable.ingredi_robo2),
                                    contentDescription = null,
                                    modifier = Modifier
                                        .size(28.dp)
//                                        .shadow(
//                                            elevation = 5.dp,
//                                            ambientColor = Color(0xFFBDBDBD),
//                                            spotColor = Color(0xFF949292)
//                                        )
                                        .clip(CircleShape),
                                    contentScale = ContentScale.Crop
                                )
                                ChatBubble(text = msg.text, isFirstWithAvatar = true)
                            } else {
                                Spacer(modifier = Modifier.size(28.dp))
                                ChatBubble(text = msg.text, isFirstWithAvatar = false)
                            }
                        }
                    }
                }
            }

            // Input row: stays above keyboard via imePadding(); tap opens keyboard
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top =  20.dp)
                    .imePadding()
                    ,
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color.White)
                        .border(
                            width = 0.5.dp,
                            color = Greyscale60,
                            shape = RoundedCornerShape(16.dp)
                        )
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) { focusRequester.requestFocus() }
                        .padding(horizontal = 20.dp, vertical = 14.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    BasicTextField(
                        value = inputText,
                        onValueChange = { inputText = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .focusRequester(focusRequester),
                        textStyle = TextStyle(
                            fontFamily = Nunito,
                            fontWeight = FontWeight.Normal,
                            fontSize = 14.sp,
                            color = Greyscale140
                        ),
                        cursorBrush = SolidColor(Greyscale140),
                        singleLine = false,
                        maxLines = 4,
                        decorationBox = { inner ->
                            if (inputText.isEmpty()) {
                                Text(
                                    text = "\"Type your answer...\"",
                                    fontFamily = Manrope,
                                    fontWeight = FontWeight.Normal,
                                    fontSize = 14.sp,
                                    color = Greyscale100
                                )
                            }
                            inner()
                        }
                    )
                }

                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .clip(CircleShape)
                        .primaryButtonEffect(
                            isDisabled = false,
                            shape = RoundedCornerShape(percent = 50),
                            disabledBackgroundColor = Greyscale40
                        )
                        .clickable {
                            val trimmed = inputText.trim()
                            if (trimmed.isNotEmpty()) {
                                messages.add(ChatMessage(isUser = true, text = trimmed))
                                inputText = ""
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.msg_send_arrow),
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun ChatBubble(
    text: String,
    isFirstWithAvatar: Boolean = false
) {
    val shape = if (isFirstWithAvatar) {
        RoundedCornerShape(topStart = 4.dp, topEnd = 16.dp, bottomEnd = 16.dp, bottomStart = 16.dp)
    } else {
        RoundedCornerShape(16.dp)
    }
    Box(
        modifier = Modifier
            .clip(shape)
            .background(Color(0xFF91B640))
            .padding(horizontal = 16.dp, vertical = 10.dp)
    ) {
        Text(
            text = text,
            fontFamily = Nunito,
            fontWeight = FontWeight.Medium,
            fontSize = 14.sp,
            color = Color.White
        )
    }
}

@Composable
private fun UserBubble(text: String) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape( topStart = 16.dp , bottomStart = 16.dp , topEnd = 16.dp , bottomEnd = 4.dp))
            .background(Greyscale40)
            .padding(horizontal = 16.dp, vertical = 10.dp)
    ) {
        Text(
            text = text,
            fontFamily = Nunito,
            fontWeight = FontWeight.Medium,
            fontSize = 14.sp,
            color = Greyscale140
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun ChatBotConversationScreenPreview() {
    NonDraggableBottomSheet(
        onDismissRequest = {},
        horizontalPaddingEnabled = true
    ) {
        ChatBotConversationScreen()
    }
}

