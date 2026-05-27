package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.viewmodel.EbonyViewModel
import com.example.viewmodel.PremiumTheme

@Composable
fun ThemeShowcaseScreen(
    viewModel: EbonyViewModel,
    modifier: Modifier = Modifier
) {
    val currentTheme by viewModel.currentTheme.collectAsState()
    val isLockscreen by viewModel.isLockscreenPreview.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "AFRICAN ICON PACK THEMES",
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            fontSize = 11.sp,
            letterSpacing = 2.sp,
            modifier = Modifier.padding(start = 4.dp)
        )

        // Horizontal Row of Theme Cards
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(viewModel.themesList) { theme ->
                val isSelected = theme.id == currentTheme.id
                ThemeSelectionTab(
                    theme = theme,
                    isSelected = isSelected,
                    onClick = { viewModel.selectTheme(theme.id) }
                )
            }
        }

        // Active Theme Specs Detail
        ActiveThemeMetaBlock(currentTheme = currentTheme)

        // Live Virtual Phone Simulator Title
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "INTERACTIVE MOCKUP SIMULATOR",
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                fontSize = 11.sp,
                letterSpacing = 1.8.sp
            )

            // Switch previews
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .border(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f), RoundedCornerShape(20.dp))
                    .clickable { viewModel.togglePreviewScreen() }
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (isLockscreen) Icons.Default.Lock else Icons.Default.Home,
                        contentDescription = "Toggle Preview Mode",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = if (isLockscreen) "Lock screen" else "Home screen",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
            }
        }

        // The Smart Phone Body Container
        PhoneSimulatorFrame(
            currentTheme = currentTheme, 
            isLockscreen = isLockscreen,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun ThemeSelectionTab(
    theme: PremiumTheme,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(140.dp)
            .height(100.dp)
            .clickable { onClick() }
            .testTag("theme_tab_${theme.id}")
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = if (isSelected) MaterialTheme.colorScheme.primary else Color.LightGray.copy(alpha = 0.4f),
                shape = RoundedCornerShape(16.dp)
            ),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else Color.White
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(16.dp)
                        .clip(CircleShape)
                        .background(Color(theme.primaryColorHex))
                )
                val tag = when {
                    theme.platform.contains("Material") -> "M3"
                    theme.platform.contains("Themed") -> "ICN"
                    theme.platform.contains("AMOLED") -> "AML"
                    else -> "ADP"
                }
                val tagColor = when {
                    theme.platform.contains("Material") -> Color(0xFF6750A4)
                    theme.platform.contains("Themed") -> Color(0xFFD35230)
                    theme.platform.contains("AMOLED") -> Color(0xFF1C6E8C)
                    else -> Color(0xFF81B29A)
                }
                Text(
                    text = tag,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = tagColor
                )
            }

            Column {
                Text(
                    text = theme.name,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = if (isSelected) MaterialTheme.colorScheme.onSecondaryContainer else MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = theme.conceptName,
                    fontSize = 10.sp,
                    color = if (isSelected) MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.8f) else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun ActiveThemeMetaBlock(currentTheme: PremiumTheme) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = currentTheme.name,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.primary
                )
                
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = currentTheme.status,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Text(
                text = "Concept: " + currentTheme.conceptName,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = currentTheme.description,
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Text(
                text = "Target platform layout: " + currentTheme.platform,
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun PhoneSimulatorFrame(
    currentTheme: PremiumTheme,
    isLockscreen: Boolean,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .border(6.dp, Color(0xFF2B2521), RoundedCornerShape(28.dp))
            .clip(RoundedCornerShape(24.dp))
            .background(Color(0xFF0A0908)),
        contentAlignment = Alignment.Center
    ) {
        // Render Custom Background Wallpaper on Canvas!
        Canvas(modifier = Modifier.fillMaxSize()) {
            val canvasWidth = size.width
            val canvasHeight = size.height

            when (currentTheme.id) {
                "kente_glow" -> {
                    // Kente Pattern Painting ( Ghana interlocking bands)
                    val brush = Brush.verticalGradient(
                        listOf(Color(0xFFEAA627), Color(0xFFD35230), Color(0xFF1E1916))
                    )
                    drawRect(brush = brush)
                    
                    // Draw geometric grid checkers
                    val stepY = canvasHeight / 10
                    val stepX = canvasWidth / 5
                    for (i in 0..10) {
                        if (i % 2 == 0) {
                            drawRect(
                                color = Color(0xFFA52A2A).copy(alpha = 0.2f),
                                topLeft = Offset(0f, i * stepY),
                                size = Size(canvasWidth, stepY / 2)
                            )
                        } else {
                            drawCircle(
                                color = Color(0xFFEAA627).copy(alpha = 0.15f),
                                radius = 25f,
                                center = Offset(canvasWidth / 2, i * stepY)
                            )
                        }
                    }
                }
                "sahara_sunset" -> {
                    // Sahara painting (Smooth sand dunes with golden rise)
                    val desertBrush = Brush.verticalGradient(
                        listOf(Color(0xFFD35230), Color(0xFFC84B22), Color(0xFF0F0E0D))
                    )
                    drawRect(brush = desertBrush)

                    // Dune line 1
                    val dunePath1 = Path().apply {
                        moveTo(0f, canvasHeight * 0.7f)
                        quadraticTo(canvasWidth * 0.4f, canvasHeight * 0.6f, canvasWidth, canvasHeight * 0.75f)
                        lineTo(canvasWidth, canvasHeight)
                        lineTo(0f, canvasHeight)
                        close()
                    }
                    drawPath(dunePath1, color = Color(0xFFD35230).copy(alpha = 0.8f))

                    // Dune line 2
                    val dunePath2 = Path().apply {
                        moveTo(0f, canvasHeight * 0.8f)
                        quadraticTo(canvasWidth * 0.6f, canvasHeight * 0.85f, canvasWidth, canvasHeight * 0.79f)
                        lineTo(canvasWidth, canvasHeight)
                        lineTo(0f, canvasHeight)
                        close()
                    }
                    drawPath(dunePath2, color = Color(0xFFC24018))

                    // Rising sun
                    drawCircle(
                        color = Color(0xFFF2CC8F),
                        radius = 80f,
                        center = Offset(canvasWidth * 0.7f, canvasHeight * 0.5f)
                    )
                }
                "nile_dusk" -> {
                    // Nile Painting (Midnight Blue Slate & repeating Chevrons)
                    val nileBrush = Brush.verticalGradient(
                        listOf(Color(0xFF051125), Color(0xFF1C6E8C), Color(0xFF000000))
                    )
                    drawRect(brush = nileBrush)

                    // Draw golden lines chevron style
                    val chevronStep = 120f
                    for (y in 100..canvasHeight.toInt() step chevronStep.toInt()) {
                        val path = Path().apply {
                            moveTo(0f, y.toFloat())
                            lineTo(canvasWidth / 2, y.toFloat() + 40f)
                            lineTo(canvasWidth, y.toFloat())
                        }
                        drawPath(
                            path = path,
                            color = Color(0xFFEAA627).copy(alpha = 0.25f),
                            style = Stroke(width = 4f)
                        )
                    }
                }
                "ananse_gold" -> {
                    // Ananse complex webbing (Amber lines over Charcoal)
                    val charcoalBrush = Brush.radialGradient(
                        colors = listOf(Color(0xFF2C241E), Color(0xFF0F0E0D)),
                        center = Offset(canvasWidth / 2, canvasHeight / 2)
                    )
                    drawRect(brush = charcoalBrush)

                    val borderPath = Path().apply {
                        moveTo(40f, 40f)
                        lineTo(canvasWidth - 40f, 40f)
                        lineTo(canvasWidth - 40f, canvasHeight - 40f)
                        lineTo(40f, canvasHeight - 40f)
                        close()
                        moveTo(40f, 40f)
                        lineTo(canvasWidth - 40f, canvasHeight - 40f)
                        moveTo(canvasWidth - 40f, 40f)
                        lineTo(40f, canvasHeight - 40f)
                    }
                    drawPath(
                        path = borderPath,
                        color = Color(0xFFEAA627).copy(alpha = 0.15f),
                        style = Stroke(width = 3f)
                    )

                    val scale = 0.6f
                    drawRect(
                        color = Color(0xFFEAA627).copy(alpha = 0.2f),
                        topLeft = Offset(canvasWidth * 0.2f, canvasHeight * 0.3f),
                        size = Size(canvasWidth * scale, canvasHeight * 0.4f),
                        style = Stroke(width = 2f)
                    )
                }
            }
        }

        // Live status pill at the notch
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 10.dp)
                .width(110.dp)
                .height(20.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(Color.Black.copy(alpha = 0.6f)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "EBONY NOTCH",
                fontSize = 8.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White.copy(alpha = 0.7f),
                letterSpacing = 1.sp
            )
        }

        // Clock widget inside Phone
        Column(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 48.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "11:05",
                fontSize = 44.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White,
                letterSpacing = 2.sp
            )
            Text(
                text = "Wednesday, May 27",
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = Color.White.copy(alpha = 0.8f)
            )
        }

        if (!isLockscreen) {
            // Home screen Mode: Grid of simulated African-inspired Adaptive Icons!
            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 32.dp, start = 12.dp, end = 12.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Icon Matrix Grid (2 rows x 3 columns)
                val items = listOf(
                    PhoneIconSim("WhatsApp", Icons.Default.Email, currentTheme.primaryColorHex),
                    PhoneIconSim("Instagram", Icons.Default.Camera, currentTheme.accentColorHex),
                    PhoneIconSim("Dialer", Icons.Default.Call, currentTheme.primaryColorHex),
                    PhoneIconSim("Messages", Icons.Default.Send, currentTheme.accentColorHex),
                    PhoneIconSim("Maps", Icons.Default.LocationOn, currentTheme.primaryColorHex),
                    PhoneIconSim("Spotify", Icons.Default.PlayArrow, currentTheme.accentColorHex)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    items.take(3).forEach { iconSim ->
                        SimulatedIconItem(icon = iconSim)
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    items.drop(3).forEach { iconSim ->
                        SimulatedIconItem(icon = iconSim)
                    }
                }
            }
        } else {
            // Lock screen Mode: Slide to unlock
            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 40.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Quick shortcut icons
                Row(
                    modifier = Modifier.width(160.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(Color.Black.copy(alpha = 0.5f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Lightbulb, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                    }

                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(Color.Black.copy(alpha = 0.5f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Camera, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                    }
                }

                // Unlock Bar
                Box(
                    modifier = Modifier
                        .width(200.dp)
                        .height(36.dp)
                        .clip(RoundedCornerShape(18.dp))
                        .background(Color.White.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ChevronRight,
                            contentDescription = null,
                            tint = Color.White.copy(alpha = 0.8f),
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "Slide to Unlock",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White.copy(alpha = 0.8f),
                            letterSpacing = 1.sp
                        )
                    }
                }
            }
        }
    }
}

data class PhoneIconSim(
    val name: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val colorHex: Long
)

@Composable
fun SimulatedIconItem(icon: PhoneIconSim) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        // Render beautiful tribal frame around icon
        Box(
            modifier = Modifier
                .size(42.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(Color(0xFF0F0E0D))
                .border(1.5.dp, Color(icon.colorHex), RoundedCornerShape(10.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon.icon,
                contentDescription = icon.name,
                tint = Color(icon.colorHex),
                modifier = Modifier.size(20.dp)
            )
        }
        Text(
            text = icon.name,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
    }
}
