package com.example.fintrack.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.TextButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Typography
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.TextStyle
import com.example.fintrack.ui.theme.semanticColors

enum class AppCardVariant {
    HERO,
    STANDARD,
    MINIMAL
}

val Typography.appTitle: TextStyle get() = titleLarge
val Typography.appSubtitle: TextStyle get() = titleMedium
val Typography.appBody: TextStyle get() = bodyMedium
val Typography.appCaption: TextStyle get() = labelMedium

@Composable
fun AppCard(
    modifier: Modifier = Modifier,
    variant: AppCardVariant = AppCardVariant.STANDARD,
    containerColor: Color = when (variant) {
        AppCardVariant.HERO -> MaterialTheme.colorScheme.primaryContainer
        AppCardVariant.STANDARD -> MaterialTheme.semanticColors.cardSurface
        AppCardVariant.MINIMAL -> MaterialTheme.colorScheme.surface
    },
    shape: RoundedCornerShape = when (variant) {
        AppCardVariant.HERO -> RoundedCornerShape(28.dp)
        AppCardVariant.STANDARD -> RoundedCornerShape(20.dp)
        AppCardVariant.MINIMAL -> RoundedCornerShape(16.dp)
    },
    content: @Composable () -> Unit
) {
    Card(
        modifier = modifier,
        shape = shape,
        colors = CardDefaults.cardColors(containerColor = containerColor)
    ) {
        content()
    }
}

@Composable
fun AppPrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    leadingIcon: @Composable (() -> Unit)? = null
) {
    Button(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary
        )
    ) {
        leadingIcon?.invoke()
        Text(text)
    }
}

@Composable
fun AppSecondaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    leadingIcon: @Composable (() -> Unit)? = null
) {
    FilledTonalButton(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(12.dp)
    ) {
        leadingIcon?.invoke()
        Text(text)
    }
}

@Composable
fun AppGhostButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    TextButton(
        onClick = onClick,
        modifier = modifier
    ) {
        Text(text)
    }
}

@Composable
fun AppEmptyState(
    icon: ImageVector,
    title: String,
    subtitle: String,
    actionText: String? = null,
    onAction: (() -> Unit)? = null
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp, horizontal = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = MaterialTheme.semanticColors.textSecondary
        )
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center
        )
        Text(
            text = subtitle,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.semanticColors.textSecondary,
            textAlign = TextAlign.Center
        )
        if (actionText != null && onAction != null) {
            Row {
                Button(onClick = onAction) {
                    Text(actionText)
                }
            }
        }
    }
}
