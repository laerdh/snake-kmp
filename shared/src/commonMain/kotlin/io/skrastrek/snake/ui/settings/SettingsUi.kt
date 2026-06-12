package io.skrastrek.snake.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.em
import io.skrastrek.snake.domain.model.Difficulty
import io.skrastrek.snake.domain.model.PlayerProfile
import io.skrastrek.snake.ui.components.BrandWordmark
import io.skrastrek.snake.ui.components.GlassCard
import io.skrastrek.snake.ui.components.NeonBackground
import io.skrastrek.snake.ui.components.NeonIconButton
import io.skrastrek.snake.ui.components.PrimaryButton
import io.skrastrek.snake.ui.components.SecondaryButton
import io.skrastrek.snake.ui.theme.AppColors
import io.skrastrek.snake.ui.theme.AppDimensions
import io.skrastrek.snake.ui.theme.AppTheme
import kotlin.math.roundToInt

/** Stateless Circuit UI for [SettingsScreen]. */
@Composable
fun SettingsUi(state: SettingsUiState, modifier: Modifier = Modifier) {
    val eventSink = state.eventSink
    NeonBackground(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            // Top bar: back + wordmark, close.
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(AppColors.Background.copy(alpha = 0.7f))
                    .padding(horizontal = AppDimensions.SpacingMd, vertical = AppDimensions.SpacingSm),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    NeonIconButton(
                        icon = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        onClick = { eventSink(SettingsUiEvent.CloseClicked) },
                    )
                    BrandWordmark(modifier = Modifier.padding(start = AppDimensions.SpacingSm))
                }
                NeonIconButton(
                    icon = Icons.Filled.Close,
                    contentDescription = "Close",
                    onClick = { eventSink(SettingsUiEvent.CloseClicked) },
                    tint = AppColors.Primary,
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(AppDimensions.ContainerPadding),
                verticalArrangement = Arrangement.spacedBy(AppDimensions.SpacingLg),
            ) {
                ProfileCard(profile = state.profile)
                GameplayCard(
                    speed = state.speed,
                    onSpeedChange = { eventSink(SettingsUiEvent.SpeedChanged(it)) },
                )
                DataCard(onReset = { eventSink(SettingsUiEvent.ResetClicked) })
                Spacer(Modifier.size(AppDimensions.SpacingSm))
                PrimaryButton(
                    label = "SAVE SETTINGS",
                    onClick = { eventSink(SettingsUiEvent.SaveClicked) },
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
    }
}

/** Profile mini-card: glowing avatar ring + handle + rank. */
@Composable
private fun ProfileCard(profile: PlayerProfile, modifier: Modifier = Modifier) {
    GlassCard(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(AppDimensions.SpacingMd),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(AppDimensions.SpacingMd),
        ) {
            Box(
                modifier = Modifier
                    .size(AppDimensions.AvatarSize)
                    .clip(CircleShape)
                    .background(AppColors.SurfaceContainerHigh)
                    .border(AppDimensions.SecondaryButtonBorderWidth, AppColors.Primary, CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Filled.Person,
                    contentDescription = null,
                    tint = AppColors.Primary,
                    modifier = Modifier.size(AppDimensions.IconSizeLarge),
                )
            }
            Column(verticalArrangement = Arrangement.spacedBy(AppDimensions.SpacingXs)) {
                Text(
                    text = profile.name,
                    style = AppTheme.type.headlineLargeMobile,
                    color = AppColors.OnSurface,
                )
                Text(
                    text = profile.rank.uppercase(),
                    style = AppTheme.type.labelSmall.copy(letterSpacing = 0.2.em),
                    color = AppColors.Primary,
                )
            }
        }
    }
}

/** Gameplay card: section header + the GAME SPEED slider. */
@Composable
private fun GameplayCard(
    speed: Difficulty,
    onSpeedChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    GlassCard(modifier = modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(AppDimensions.SpacingLg),
            verticalArrangement = Arrangement.spacedBy(AppDimensions.SpacingLg),
        ) {
            SectionHeader(icon = Icons.Filled.SportsEsports, title = "Gameplay", tint = AppColors.Tertiary)
            Column(verticalArrangement = Arrangement.spacedBy(AppDimensions.SpacingSm)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom,
                ) {
                    Text(
                        text = "GAME SPEED",
                        style = AppTheme.type.labelSmall.copy(letterSpacing = 0.2.em),
                        color = AppColors.OnSurfaceVariant,
                    )
                    Text(
                        text = speed.displayName,
                        style = AppTheme.type.scoreDisplay,
                        color = speed.tierColor(),
                    )
                }
                Slider(
                    value = speed.sliderIndex.toFloat(),
                    onValueChange = { onSpeedChange(it.roundToInt()) },
                    valueRange = 0f..(Difficulty.entries.size - 1).toFloat(),
                    steps = Difficulty.entries.size - 2,
                    colors = SliderDefaults.colors(
                        thumbColor = AppColors.Primary,
                        activeTrackColor = AppColors.Primary,
                        inactiveTrackColor = AppColors.Tertiary.copy(alpha = 0.2f),
                        activeTickColor = Color.Transparent,
                        inactiveTickColor = Color.Transparent,
                    ),
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Difficulty.entries.forEach { tier ->
                        Text(
                            text = tier.displayName,
                            style = AppTheme.type.labelSmall,
                            color = AppColors.OnSurfaceVariant.copy(alpha = 0.6f),
                        )
                    }
                }
            }
        }
    }
}

/** Data / danger-zone card with the RESET PROGRESS action. */
@Composable
private fun DataCard(onReset: () -> Unit, modifier: Modifier = Modifier) {
    GlassCard(modifier = modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(AppDimensions.SpacingLg),
            verticalArrangement = Arrangement.spacedBy(AppDimensions.SpacingMd),
        ) {
            SectionHeader(icon = Icons.Filled.Warning, title = "Data", tint = AppColors.Error)
            SecondaryButton(
                label = "RESET PROGRESS",
                onClick = onReset,
                contentColor = AppColors.Error,
                borderColor = AppColors.Error.copy(alpha = 0.3f),
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@Composable
private fun SectionHeader(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    tint: Color,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(AppDimensions.SpacingSm),
    ) {
        Icon(imageVector = icon, contentDescription = null, tint = tint, modifier = Modifier.size(AppDimensions.IconSizeLarge))
        Text(
            text = title,
            style = AppTheme.type.headlineLargeMobile.copy(fontWeight = FontWeight.Bold),
            color = if (tint == AppColors.Error) AppColors.Error else AppColors.OnSurface,
        )
    }
}

/** The display colour for each speed tier (cyan → lime → pink). */
private fun Difficulty.tierColor(): Color = when (this) {
    Difficulty.Chill -> AppColors.Tertiary
    Difficulty.Fast -> AppColors.Primary
    Difficulty.Insane -> AppColors.Secondary
}
