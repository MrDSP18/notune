package echo.music.iad1tya.ui.component

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Shader
import android.graphics.Typeface
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil3.compose.AsyncImage
import coil3.imageLoader
import coil3.request.ImageRequest
import coil3.request.allowHardware
import coil3.toBitmap
import echo.music.iad1tya.R
import echo.music.iad1tya.utils.ComposeToImage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

sealed interface ShareItem {
    val id: String
    val shareUrl: String

    data class Song(
        override val id: String,
        val title: String,
        val artist: String,
        val coverUrl: String?,
        override val shareUrl: String = "https://share.notune.fun/watch?v=$id"
    ) : ShareItem

    data class Playlist(
        override val id: String,
        val title: String,
        val author: String? = null,
        val songCount: Int? = null,
        val coverUrl: String? = null,
        override val shareUrl: String = "https://share.notune.fun/playlist?list=$id"
    ) : ShareItem
}

@Composable
fun SnapCardShareDialog(
    item: ShareItem,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var isGeneratingImage by remember { mutableStateOf(false) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .padding(vertical = 16.dp),
            shape = RoundedCornerShape(28.dp),
            color = Color(0xFF141218),
            tonalElevation = 8.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.snap_share_title),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(
                            painter = painterResource(R.drawable.close),
                            contentDescription = stringResource(R.string.close),
                            tint = Color.White.copy(alpha = 0.7f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Interactive Snap Card Preview
                SnapCardPreview(item = item)

                Spacer(modifier = Modifier.height(20.dp))

                if (isGeneratingImage) {
                    CircularProgressIndicator(
                        modifier = Modifier.padding(16.dp),
                        color = Color(0xFFFFFC00)
                    )
                } else {
                    // Social Action Buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Snapchat "Snap Send"
                        SocialButton(
                            iconRes = R.drawable.ic_snapchat,
                            label = stringResource(R.string.snap_send),
                            backgroundColor = Color(0xFFFFFC00),
                            contentColor = Color.Black,
                            onClick = {
                                isGeneratingImage = true
                                coroutineScope.launch {
                                    shareToApp(context, item, "com.snapchat.android", "Snapchat")
                                    isGeneratingImage = false
                                }
                            }
                        )

                        // WhatsApp
                        SocialButton(
                            iconRes = R.drawable.ic_whatsapp,
                            label = stringResource(R.string.whatsapp_share),
                            backgroundColor = Color(0xFF25D366),
                            contentColor = Color.White,
                            onClick = {
                                isGeneratingImage = true
                                coroutineScope.launch {
                                    shareToApp(context, item, "com.whatsapp", "WhatsApp")
                                    isGeneratingImage = false
                                }
                            }
                        )

                        // Instagram
                        SocialButton(
                            iconRes = R.drawable.ic_instagram_new,
                            label = stringResource(R.string.instagram_stories),
                            backgroundColor = Color(0xFFE1306C),
                            contentColor = Color.White,
                            onClick = {
                                isGeneratingImage = true
                                coroutineScope.launch {
                                    shareToApp(context, item, "com.instagram.android", "Instagram")
                                    isGeneratingImage = false
                                }
                            }
                        )

                        // Copy Link
                        SocialButton(
                            iconRes = R.drawable.content_copy,
                            label = stringResource(R.string.copy_link),
                            backgroundColor = Color(0xFF2C2C36),
                            contentColor = Color.White,
                            onClick = {
                                copyToClipboard(context, item.shareUrl)
                            }
                        )

                        // Save Card
                        SocialButton(
                            iconRes = R.drawable.download,
                            label = stringResource(R.string.save_card),
                            backgroundColor = Color(0xFF2C2C36),
                            contentColor = Color.White,
                            onClick = {
                                isGeneratingImage = true
                                coroutineScope.launch {
                                    saveCardImage(context, item)
                                    isGeneratingImage = false
                                }
                            }
                        )

                        // More Apps
                        SocialButton(
                            iconRes = R.drawable.share,
                            label = stringResource(R.string.more_apps),
                            backgroundColor = Color(0xFF3E3C4A),
                            contentColor = Color.White,
                            onClick = {
                                isGeneratingImage = true
                                coroutineScope.launch {
                                    shareToApp(context, item, null, "Share")
                                    isGeneratingImage = false
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SnapCardPreview(item: ShareItem) {
    val title = when (item) {
        is ShareItem.Song -> item.title
        is ShareItem.Playlist -> item.title
    }
    val subtitle = when (item) {
        is ShareItem.Song -> item.artist
        is ShareItem.Playlist -> item.author ?: (item.songCount?.let { "$it songs" } ?: "Playlist")
    }
    val coverUrl = when (item) {
        is ShareItem.Song -> item.coverUrl
        is ShareItem.Playlist -> item.coverUrl
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(340.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF2A1F45),
                        Color(0xFF161224),
                        Color(0xFF09070F)
                    )
                )
            )
            .border(1.dp, Color.White.copy(alpha = 0.15f), RoundedCornerShape(20.dp))
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Album Artwork with Shadow & Glow
            Box(
                modifier = Modifier
                    .size(160.dp)
                    .shadow(16.dp, RoundedCornerShape(16.dp))
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFF22202E))
            ) {
                AsyncImage(
                    model = coverUrl,
                    contentDescription = title,
                    modifier = Modifier.matchParentSize(),
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Title
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(0.9f)
            )

            // Subtitle / Artist
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.7f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(0.85f)
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Animated Soundwave Visualizer Bars
            AnimatedSoundwave()

            Spacer(modifier = Modifier.height(8.dp))

            // Branding Badge
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.White.copy(alpha = 0.12f))
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text(
                    text = stringResource(R.string.listening_on_notune),
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFFFFFC00)
                )
            }
        }
    }
}

@Composable
private fun AnimatedSoundwave() {
    val transition = rememberInfiniteTransition(label = "soundwave")

    Row(
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.Bottom,
        modifier = Modifier.height(16.dp)
    ) {
        val barHeights = listOf(
            transition.animateFloat(
                initialValue = 0.3f, targetValue = 1.0f,
                animationSpec = infiniteRepeatable(tween(400, easing = FastOutSlowInEasing), RepeatMode.Reverse), label = "h1"
            ),
            transition.animateFloat(
                initialValue = 0.8f, targetValue = 0.2f,
                animationSpec = infiniteRepeatable(tween(550, easing = FastOutSlowInEasing), RepeatMode.Reverse), label = "h2"
            ),
            transition.animateFloat(
                initialValue = 0.4f, targetValue = 0.9f,
                animationSpec = infiniteRepeatable(tween(450, easing = FastOutSlowInEasing), RepeatMode.Reverse), label = "h3"
            ),
            transition.animateFloat(
                initialValue = 0.9f, targetValue = 0.3f,
                animationSpec = infiniteRepeatable(tween(600, easing = FastOutSlowInEasing), RepeatMode.Reverse), label = "h4"
            )
        )

        barHeights.forEach { heightFactor ->
            Box(
                modifier = Modifier
                    .width(3.dp)
                    .fillMaxHeight(heightFactor.value)
                    .clip(CircleShape)
                    .background(Color(0xFFFFFC00))
            )
        }
    }
}

@Composable
private fun SocialButton(
    iconRes: Int,
    label: String,
    backgroundColor: Color,
    contentColor: Color,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Box(
            modifier = Modifier
                .size(46.dp)
                .clip(CircleShape)
                .background(backgroundColor),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(iconRes),
                contentDescription = label,
                tint = contentColor,
                modifier = Modifier.size(22.dp)
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = Color.White.copy(alpha = 0.8f),
            fontSize = 10.sp,
            maxLines = 1
        )
    }
}

private fun copyToClipboard(context: Context, text: String) {
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clip = ClipData.newPlainText("NØTUNE Share Link", text)
    clipboard.setPrimaryClip(clip)
    Toast.makeText(context, context.getString(R.string.link_copied_to_clipboard), Toast.LENGTH_SHORT).show()
}

private suspend fun saveCardImage(context: Context, item: ShareItem) {
    try {
        val bitmap = createSnapCardBitmap(context, item)
        val timestamp = System.currentTimeMillis()
        ComposeToImage.saveBitmapAsFile(context, bitmap, "snap_card_$timestamp")
        withContext(Dispatchers.Main) {
            Toast.makeText(context, context.getString(R.string.card_saved_to_gallery), Toast.LENGTH_SHORT).show()
        }
    } catch (e: Exception) {
        withContext(Dispatchers.Main) {
            Toast.makeText(context, "Failed to save card: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
}

private suspend fun shareToApp(
    context: Context,
    item: ShareItem,
    targetPackage: String?,
    appName: String
) {
    try {
        val bitmap = createSnapCardBitmap(context, item)
        val timestamp = System.currentTimeMillis()
        val uri = ComposeToImage.saveBitmapAsFile(context, bitmap, "snap_card_$timestamp")

        withContext(Dispatchers.Main) {
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "image/png"
                putExtra(Intent.EXTRA_STREAM, uri)
                putExtra(Intent.EXTRA_TEXT, item.shareUrl)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                if (targetPackage != null) {
                    setPackage(targetPackage)
                }
            }

            try {
                if (targetPackage != null) {
                    context.startActivity(shareIntent)
                } else {
                    context.startActivity(Intent.createChooser(shareIntent, "Share via"))
                }
            } catch (_: Exception) {
                // Fallback to system share chooser if specific app is not installed
                val fallbackIntent = Intent(Intent.ACTION_SEND).apply {
                    type = "image/png"
                    putExtra(Intent.EXTRA_STREAM, uri)
                    putExtra(Intent.EXTRA_TEXT, item.shareUrl)
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                }
                context.startActivity(Intent.createChooser(fallbackIntent, "Share to $appName"))
            }
        }
    } catch (e: Exception) {
        withContext(Dispatchers.Main) {
            Toast.makeText(context, "Failed to generate share image: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
}

suspend fun createSnapCardBitmap(
    context: Context,
    item: ShareItem
): Bitmap = withContext(Dispatchers.Default) {
    val width = 1080
    val height = 1920
    val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)

    var coverBitmap: Bitmap? = null
    val coverUrl = when (item) {
        is ShareItem.Song -> item.coverUrl
        is ShareItem.Playlist -> item.coverUrl
    }
    if (!coverUrl.isNullOrEmpty()) {
        try {
            val request = ImageRequest.Builder(context)
                .data(coverUrl)
                .size(800)
                .allowHardware(false)
                .build()
            val result = context.imageLoader.execute(request)
            coverBitmap = result.image?.toBitmap()
        } catch (_: Exception) {}
    }

    // Background gradient
    val bgPaint = Paint().apply { isAntiAlias = true }
    val bgGradient = LinearGradient(
        0f, 0f, width.toFloat(), height.toFloat(),
        intArrayOf(0xFF0F0C20.toInt(), 0xFF15102A.toInt(), 0xFF05050A.toInt()),
        null, Shader.TileMode.CLAMP
    )
    bgPaint.shader = bgGradient
    canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), bgPaint)

    // Central card container
    val cardLeft = 100f
    val cardTop = 360f
    val cardRight = width - 100f
    val cardBottom = height - 420f
    val cardRect = RectF(cardLeft, cardTop, cardRight, cardBottom)

    val cardBgPaint = Paint().apply {
        isAntiAlias = true
        color = 0xEE1A1A2E.toInt()
    }
    canvas.drawRoundRect(cardRect, 48f, 48f, cardBgPaint)

    val borderPaint = Paint().apply {
        isAntiAlias = true
        style = Paint.Style.STROKE
        strokeWidth = 3f
        color = 0x40FFFFFF.toInt()
    }
    canvas.drawRoundRect(cardRect, 48f, 48f, borderPaint)

    // Artwork
    val artSize = 640f
    val artLeft = (width - artSize) / 2f
    val artTop = cardTop + 100f
    val artRect = RectF(artLeft, artTop, artLeft + artSize, artTop + artSize)

    coverBitmap?.let { art ->
        val artPaint = Paint().apply { isAntiAlias = true }
        val path = android.graphics.Path().apply {
            addRoundRect(artRect, 36f, 36f, android.graphics.Path.Direction.CW)
        }
        canvas.save()
        canvas.clipPath(path)
        canvas.drawBitmap(art, null, artRect, artPaint)
        canvas.restore()
    } ?: run {
        val placeholderPaint = Paint().apply {
            isAntiAlias = true
            color = 0xFF2A2A3D.toInt()
        }
        canvas.drawRoundRect(artRect, 36f, 36f, placeholderPaint)
    }

    // Typography
    val titleText = when (item) {
        is ShareItem.Song -> item.title
        is ShareItem.Playlist -> item.title
    }
    val subtitleText = when (item) {
        is ShareItem.Song -> item.artist
        is ShareItem.Playlist -> item.author ?: (item.songCount?.let { "$it songs" } ?: "Playlist")
    }

    val titlePaint = android.text.TextPaint().apply {
        isAntiAlias = true
        color = 0xFFFFFFFF.toInt()
        textSize = 54f
        typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        textAlign = Paint.Align.CENTER
    }

    val subPaint = android.text.TextPaint().apply {
        isAntiAlias = true
        color = 0xB3FFFFFF.toInt()
        textSize = 38f
        typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        textAlign = Paint.Align.CENTER
    }

    val titleY = artTop + artSize + 110f
    val subY = titleY + 65f

    val maxTextWidth = cardRight - cardLeft - 80f
    val truncatedTitle = android.text.TextUtils.ellipsize(
        titleText, titlePaint, maxTextWidth, android.text.TextUtils.TruncateAt.END
    ).toString()

    val truncatedSub = android.text.TextUtils.ellipsize(
        subtitleText, subPaint, maxTextWidth, android.text.TextUtils.TruncateAt.END
    ).toString()

    canvas.drawText(truncatedTitle, width / 2f, titleY, titlePaint)
    canvas.drawText(truncatedSub, width / 2f, subY, subPaint)

    // Branding badge
    val badgeY = cardBottom - 90f
    val badgePaint = android.text.TextPaint().apply {
        isAntiAlias = true
        color = 0xFFFFFC00.toInt()
        textSize = 36f
        typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        textAlign = Paint.Align.CENTER
    }
    canvas.drawText("🎵 Listening on NØTUNE", width / 2f, badgeY, badgePaint)

    // Footer
    val footerPaint = android.text.TextPaint().apply {
        isAntiAlias = true
        color = 0x80FFFFFF.toInt()
        textSize = 32f
        textAlign = Paint.Align.CENTER
    }
    canvas.drawText(item.shareUrl, width / 2f, height - 150f, footerPaint)

    bitmap
}
