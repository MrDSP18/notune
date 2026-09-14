package com.music.echo.ui.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import echo.music.iad1tya.ui.theme.NothingFont

val NothingCrimson = Color(0xFFFF0031)
val NothingOledBlack = Color(0xFF000000)
val NothingDarkSurface = Color(0xFF0B0B0B)
val NothingCardSurface = Color(0xFF121212)
val NothingBorderColor = Color.White.copy(alpha = 0.12f)
val NothingCrimsonBorder = NothingCrimson.copy(alpha = 0.4f)

@Composable
fun NothingGridBackground(
    modifier: Modifier = Modifier,
    dotSpacingDp: Dp = 16.dp,
    dotColor: Color = Color.White.copy(alpha = 0.04f),
    content: @Composable () -> Unit
) {
    Box(modifier = modifier.background(NothingOledBlack)) {
        Canvas(modifier = Modifier.matchParentSize()) {
            val dotSpacing = dotSpacingDp.toPx()
            val radius = 1.dp.toPx()
            var x = 0f
            while (x < size.width) {
                var y = 0f
                while (y < size.height) {
                    drawCircle(
                        color = dotColor,
                        radius = radius,
                        center = Offset(x, y)
                    )
                    y += dotSpacing
                }
                x += dotSpacing
            }
        }
        content()
    }
}

@Composable
fun NothingHeader(
    title: String,
    subtitle: String? = null,
    modifier: Modifier = Modifier,
    showStatusDot: Boolean = true,
    statusDotColor: Color = NothingCrimson,
    action: (@Composable () -> Unit)? = null
) {
    Row(
        modifier = modifier.fillMaxWidth().padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (showStatusDot) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(statusDotColor)
                    )
                    Spacer(Modifier.width(8.dp))
                }
                Text(
                    text = title.uppercase(),
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontFamily = NothingFont,
                        color = Color.White,
                        letterSpacing = 2.sp,
                        fontWeight = FontWeight.Bold
                    )
                )
            }
            if (!subtitle.isNullOrBlank()) {
                Spacer(Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = Color.White.copy(alpha = 0.5f),
                        fontSize = 11.sp
                    )
                )
            }
        }
        if (action != null) {
            action()
        }
    }
}

@Composable
fun NothingCard(
    modifier: Modifier = Modifier,
    backgroundColor: Color = NothingCardSurface,
    borderColor: Color = NothingBorderColor,
    cornerRadius: Dp = 14.dp,
    onClick: (() -> Unit)? = null,
    content: @Composable () -> Unit
) {
    val cardModifier = if (onClick != null) {
        modifier.clickable { onClick() }
    } else {
        modifier
    }

    Card(
        modifier = cardModifier,
        shape = RoundedCornerShape(cornerRadius),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        border = androidx.compose.foundation.BorderStroke(1.dp, borderColor)
    ) {
        content()
    }
}

@Composable
fun NothingChip(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        color = if (isSelected) NothingCrimson.copy(alpha = 0.18f) else Color(0xFF161616),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isSelected) NothingCrimson else Color.White.copy(alpha = 0.15f)
        )
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (isSelected) {
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .clip(CircleShape)
                        .background(NothingCrimson)
                )
                Spacer(Modifier.width(6.dp))
            }
            Text(
                text = text.uppercase(),
                style = MaterialTheme.typography.labelSmall.copy(
                    fontFamily = NothingFont,
                    color = if (isSelected) NothingCrimson else Color.White.copy(alpha = 0.8f),
                    letterSpacing = 1.2.sp
                )
            )
        }
    }
}

@Composable
fun NothingButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isPrimary: Boolean = true
) {
    Button(
        onClick = onClick,
        modifier = modifier.height(46.dp),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isPrimary) NothingCrimson else Color(0xFF181818),
            contentColor = Color.White
        ),
        border = if (!isPrimary) androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.2f)) else null
    ) {
        Text(
            text = text.uppercase(),
            style = MaterialTheme.typography.labelLarge.copy(
                fontFamily = NothingFont,
                letterSpacing = 1.5.sp,
                fontWeight = FontWeight.Bold
            )
        )
    }
}
