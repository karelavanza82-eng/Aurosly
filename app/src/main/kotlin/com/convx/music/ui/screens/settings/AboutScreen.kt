/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 * Modified for Aurora by muhamadkarel.
 */

package com.convx.music.ui.screens.settings

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.monochrome.SFArrowDownApp
import com.composables.sfsymbols.monochrome.SFInfoCircle
import com.composables.sfsymbols.monochrome.SFMappin
import com.composables.sfsymbols.monochrome.SFPersonCropCircle
import com.convx.music.R
import com.convx.music.ui.component.IosSettingsGroup
import com.convx.music.ui.component.IosSettingsItem
import com.convx.music.ui.ios.compat.AppTheme
import com.convx.music.ui.ios.compat.Text
import com.convx.music.ui.theme.ChillaxFontFamily
import com.convx.music.ui.utils.safeOpenUri
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private const val DEVELOPER_NAME = "muhamadkarel"
private const val DEVELOPER_LOCATION = "Sragen, Jawa Tengah, Indonesia"
private const val WHATSAPP_DISPLAY = "085804667658"
private const val WHATSAPP_URL = "https://wa.me/6285804667658"
private const val INSTAGRAM_HANDLE = "@karelvnmry"
private const val INSTAGRAM_URL = "https://www.instagram.com/karelvnmry"
const val AURORA_DISPLAY_VERSION = "Android v1.0"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutScreen(
    navController: NavController,
    scrollBehavior: TopAppBarScrollBehavior,
    onBack: (() -> Unit)? = null,
) {
    val uriHandler = LocalUriHandler.current
    val context = LocalContext.current
    val unknownString = stringResource(R.string.unknown)

    val installedDate = remember {
        try {
            val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date(packageInfo.firstInstallTime))
        } catch (_: Exception) {
            unknownString
        }
    }

    SettingsPage(
        navController = navController,
        title = stringResource(R.string.about),
        onBack = { onBack?.invoke() ?: navController.navigateUp() },
        pageColor = AppTheme.colorScheme.background,
        horizontalPadding = 16.dp,
    ) {
        AuroraAboutHeader()

        Spacer(modifier = Modifier.height(24.dp))

        IosSettingsGroup(
            title = "PENGEMBANG",
            items = listOf(
                IosSettingsItem(
                    icon = rememberVectorPainter(SfSymbols.Monochrome.SFPersonCropCircle),
                    title = { Text(DEVELOPER_NAME) },
                    description = { Text("Pengembang Aurora", color = AppTheme.colorScheme.primary) },
                ),
                IosSettingsItem(
                    icon = rememberVectorPainter(SfSymbols.Monochrome.SFMappin),
                    title = { Text("Dibuat di") },
                    description = { Text(DEVELOPER_LOCATION) },
                ),
            )
        )

        Spacer(modifier = Modifier.height(27.dp))

        IosSettingsGroup(
            title = "KONTAK",
            items = listOf(
                IosSettingsItem(
                    icon = painterResource(R.drawable.ic_whatsapp),
                    title = { Text("WhatsApp") },
                    description = { Text(WHATSAPP_DISPLAY, color = AppTheme.colorScheme.primary) },
                    onClick = { uriHandler.safeOpenUri(context, WHATSAPP_URL) }
                ),
                IosSettingsItem(
                    icon = painterResource(R.drawable.ic_instagram),
                    title = { Text("Instagram") },
                    description = { Text(INSTAGRAM_HANDLE, color = AppTheme.colorScheme.primary) },
                    onClick = { uriHandler.safeOpenUri(context, INSTAGRAM_URL) }
                ),
            )
        )

        Spacer(modifier = Modifier.height(27.dp))

        IosSettingsGroup(
            title = stringResource(R.string.app_info_section),
            items = listOf(
                IosSettingsItem(
                    icon = rememberVectorPainter(SfSymbols.Monochrome.SFInfoCircle),
                    title = { Text("Versi") },
                    description = { Text(AURORA_DISPLAY_VERSION) }
                ),
                IosSettingsItem(
                    icon = rememberVectorPainter(SfSymbols.Monochrome.SFArrowDownApp),
                    title = { Text(stringResource(R.string.installed_date_title)) },
                    description = { Text(installedDate) }
                ),
            )
        )

        Spacer(modifier = Modifier.height(28.dp))

        Text(
            text = "Proyek ini berada di Indonesia",
            style = AppTheme.typography.bodyMedium,
            color = AppTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(modifier = Modifier.height(24.dp))
    }
}

/** Liquid-glass hero card: logo, wordmark and version pill. */
@Composable
private fun AuroraAboutHeader() {
    val onSurface = AppTheme.colorScheme.onSurface
    val glassShape = RoundedCornerShape(28.dp)
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp)
            .clip(glassShape)
            .background(
                Brush.verticalGradient(
                    listOf(
                        onSurface.copy(alpha = 0.08f),
                        onSurface.copy(alpha = 0.03f),
                    )
                )
            )
            .border(1.dp, onSurface.copy(alpha = 0.12f), glassShape)
            .padding(vertical = 32.dp, horizontal = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier
                .size(88.dp)
                .clip(CircleShape)
                .background(Color.White),
            contentAlignment = Alignment.Center,
        ) {
            Image(
                painter = painterResource(R.drawable.convx_logo),
                contentDescription = "Logo Aurora",
                colorFilter = ColorFilter.tint(Color.Black),
                modifier = Modifier.size(80.dp),
            )
        }

        Spacer(modifier = Modifier.height(18.dp))

        Text(
            text = "Aurora",
            fontFamily = ChillaxFontFamily,
            fontWeight = FontWeight.SemiBold,
            fontSize = 40.sp,
            letterSpacing = 1.sp,
            color = onSurface,
        )

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier
                .border(1.dp, AppTheme.colorScheme.primary.copy(alpha = 0.5f), CircleShape)
                .padding(horizontal = 16.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = AURORA_DISPLAY_VERSION,
                style = AppTheme.typography.titleMedium,
                color = AppTheme.colorScheme.primary,
            )
        }
    }
}
