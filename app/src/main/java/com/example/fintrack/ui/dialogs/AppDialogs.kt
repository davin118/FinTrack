package com.example.fintrack.ui.dialogs

import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.background
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Autorenew
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Paid
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.unit.dp
import com.example.fintrack.ui.components.ProfileAvatar
import com.example.fintrack.ui.finance.FinanceAccount
import com.example.fintrack.ui.finance.FinanceDebt
import com.example.fintrack.ui.finance.FinanceSavingGoal
import com.example.fintrack.ui.finance.RecurringTargetType

@Composable
fun AppPopupDialog(
    onDismissRequest: () -> Unit,
    icon: (@Composable () -> Unit)? = {
        Box(
            modifier = androidx.compose.ui.Modifier
                .size(34.dp)
                .background(
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.14f),
                    shape = CircleShape
                ),
            contentAlignment = androidx.compose.ui.Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Filled.AutoAwesome,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
        }
    },
    title: @Composable () -> Unit,
    text: @Composable () -> Unit,
    confirmButton: @Composable () -> Unit,
    dismissButton: @Composable (() -> Unit)? = null
) {
    val isDark = MaterialTheme.colorScheme.background.luminance() < 0.5f
    val popupBackground = if (isDark) {
        lerp(
            MaterialTheme.colorScheme.surface,
            MaterialTheme.colorScheme.primary,
            0.18f
        )
    } else {
        lerp(
            MaterialTheme.colorScheme.surface,
            MaterialTheme.colorScheme.primaryContainer,
            0.26f
        )
    }

    AlertDialog(
        onDismissRequest = onDismissRequest,
        icon = icon,
        title = title,
        text = text,
        confirmButton = confirmButton,
        dismissButton = dismissButton,
        shape = RoundedCornerShape(28.dp),
        containerColor = popupBackground.copy(alpha = if (isDark) 0.98f else 0.995f),
        iconContentColor = MaterialTheme.colorScheme.primary,
        titleContentColor = MaterialTheme.colorScheme.primary,
        textContentColor = if (isDark) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant,
        tonalElevation = 18.dp
    )
}

@Composable
fun PopupTitle(
    text: String,
    subtitle: String? = null
) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(
            text = text,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        if (!subtitle.isNullOrBlank()) {
            Text(
                text = subtitle,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun PopupConfirmButton(
    text: String,
    onClick: () -> Unit,
    enabled: Boolean = true
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = Color.White
        )
    ) {
        Text(text)
    }
}

@Composable
fun PopupDismissButton(
    text: String = "Cancelar",
    onClick: () -> Unit
) {
    TextButton(onClick = onClick) {
        Text(text, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
fun PopupHeaderIcon(
    imageVector: ImageVector,
    tint: Color = MaterialTheme.colorScheme.primary
) {
    Box(
        modifier = androidx.compose.ui.Modifier
            .size(34.dp)
            .background(
                color = tint.copy(alpha = 0.14f),
                shape = CircleShape
            ),
        contentAlignment = androidx.compose.ui.Alignment.Center
    ) {
        Icon(
            imageVector = imageVector,
            contentDescription = null,
            tint = tint
        )
    }
}

@Composable
fun PopupInputField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    singleLine: Boolean = true,
    visualTransformation: VisualTransformation = VisualTransformation.None
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        singleLine = singleLine,
        visualTransformation = visualTransformation,
        modifier = androidx.compose.ui.Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = Color(0xF7FFFFFF),
            unfocusedContainerColor = Color(0xEEFFFFFF),
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.45f),
            focusedLabelColor = MaterialTheme.colorScheme.primary
        )
    )
}

@Composable
fun BackupPasswordDialog(
    title: String,
    confirmText: String,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var password by remember { mutableStateOf("") }

    AppPopupDialog(
        onDismissRequest = onDismiss,
        icon = { PopupHeaderIcon(Icons.Filled.Lock) },
        title = { PopupTitle(title, "Protege tu archivo con una contrasena segura") },
        text = {
            PopupInputField(
                value = password,
                onValueChange = { password = it },
                label = "Contrasena",
                visualTransformation = PasswordVisualTransformation()
            )
        },
        confirmButton = {
            PopupConfirmButton(text = confirmText, onClick = { onConfirm(password) })
        },
        dismissButton = {
            PopupDismissButton(onClick = onDismiss)
        }
    )
}

@Composable
fun CreateAccountDialog(
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    AppPopupDialog(
        onDismissRequest = onDismiss,
        icon = { PopupHeaderIcon(Icons.Filled.AccountBalanceWallet) },
        title = { PopupTitle("Nueva cuenta", "Separa tu dinero por objetivos") },
        text = {
            PopupInputField(
                value = name,
                onValueChange = { name = it },
                label = "Nombre de cuenta"
            )
        },
        confirmButton = {
            PopupConfirmButton(text = "Guardar", onClick = { onConfirm(name) })
        },
        dismissButton = {
            PopupDismissButton(onClick = onDismiss)
        }
    )
}

@Composable
fun TransferDialog(
    accounts: List<FinanceAccount>,
    onDismiss: () -> Unit,
    onConfirm: (Long, Long, Double, String) -> Unit
) {
    val fromDefault = accounts.firstOrNull()?.id ?: 0L
    val toDefault = accounts.drop(1).firstOrNull()?.id ?: fromDefault
    var fromId by remember(accounts) { mutableStateOf(fromDefault) }
    var toId by remember(accounts) { mutableStateOf(toDefault) }
    var amount by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }

    AppPopupDialog(
        onDismissRequest = onDismiss,
        icon = { PopupHeaderIcon(Icons.Filled.SwapHoriz) },
        title = { PopupTitle("Transferir entre cuentas", "Mueve fondos de forma segura") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Desde")
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(accounts, key = { it.id }) { account ->
                        FilterChip(
                            selected = fromId == account.id,
                            onClick = { fromId = account.id },
                            label = { Text(account.name) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primary,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }
                Text("Hacia")
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(accounts, key = { it.id }) { account ->
                        FilterChip(
                            selected = toId == account.id,
                            onClick = { toId = account.id },
                            label = { Text(account.name) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primary,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }
                PopupInputField(
                    value = amount,
                    onValueChange = { amount = it },
                    label = "Monto"
                )
                PopupInputField(
                    value = note,
                    onValueChange = { note = it },
                    label = "Nota (opcional)"
                )
            }
        },
        confirmButton = {
            PopupConfirmButton(
                text = "Transferir",
                onClick = {
                    val parsed = amount.toDoubleOrNull()
                    if (parsed != null && parsed > 0 && fromId != toId) {
                        onConfirm(fromId, toId, parsed, note.trim())
                    }
                },
                enabled = accounts.size >= 2
            )
        },
        dismissButton = {
            PopupDismissButton(onClick = onDismiss)
        }
    )
}

@Composable
fun CreateSubscriptionDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, Double, Int) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var dayOfMonth by remember { mutableStateOf("1") }

    AppPopupDialog(
        onDismissRequest = onDismiss,
        icon = { PopupHeaderIcon(Icons.Filled.RocketLaunch) },
        title = { PopupTitle("Nueva suscripcion", "Controla cobros recurrentes mes a mes") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                PopupInputField(
                    value = name,
                    onValueChange = { name = it },
                    label = "Nombre"
                )
                PopupInputField(
                    value = amount,
                    onValueChange = { amount = it },
                    label = "Monto mensual"
                )
                PopupInputField(
                    value = dayOfMonth,
                    onValueChange = { dayOfMonth = it },
                    label = "Dia de cobro (1-31)"
                )
            }
        },
        confirmButton = {
            PopupConfirmButton(
                text = "Guardar",
                onClick = {
                    val parsedAmount = amount.toDoubleOrNull()
                    val parsedDay = dayOfMonth.toIntOrNull()
                    if (name.isNotBlank() && parsedAmount != null && parsedAmount > 0.0 && parsedDay != null) {
                        onConfirm(name.trim(), parsedAmount, parsedDay.coerceIn(1, 31))
                    }
                }
            )
        },
        dismissButton = {
            PopupDismissButton(onClick = onDismiss)
        }
    )
}

@Composable
fun CreateSavingGoalDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, Double) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var targetAmount by remember { mutableStateOf("") }

    AppPopupDialog(
        onDismissRequest = onDismiss,
        icon = { PopupHeaderIcon(Icons.Filled.Savings) },
        title = { PopupTitle("Nueva meta de ahorro", "Define tu objetivo y empieza a avanzar") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                PopupInputField(
                    value = name,
                    onValueChange = { name = it },
                    label = "Nombre de la meta"
                )
                PopupInputField(
                    value = targetAmount,
                    onValueChange = { targetAmount = it },
                    label = "Monto objetivo"
                )
            }
        },
        confirmButton = {
            PopupConfirmButton(
                text = "Guardar",
                onClick = {
                    val parsedTarget = targetAmount.toDoubleOrNull()
                    if (name.isNotBlank() && parsedTarget != null && parsedTarget > 0.0) {
                        onConfirm(name.trim(), parsedTarget)
                    }
                }
            )
        },
        dismissButton = {
            PopupDismissButton(onClick = onDismiss)
        }
    )
}

@Composable
fun ContributeSavingGoalDialog(
    goalName: String,
    onDismiss: () -> Unit,
    onConfirm: (Double) -> Unit
) {
    var amount by remember { mutableStateOf("") }

    AppPopupDialog(
        onDismissRequest = onDismiss,
        icon = { PopupHeaderIcon(Icons.Filled.Savings) },
        title = { PopupTitle("Aportar a meta", "Suma progreso a tu objetivo") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(goalName)
                PopupInputField(
                    value = amount,
                    onValueChange = { amount = it },
                    label = "Monto a aportar"
                )
            }
        },
        confirmButton = {
            PopupConfirmButton(
                text = "Aportar",
                onClick = {
                    val parsedAmount = amount.toDoubleOrNull()
                    if (parsedAmount != null && parsedAmount > 0.0) {
                        onConfirm(parsedAmount)
                    }
                }
            )
        },
        dismissButton = {
            PopupDismissButton(onClick = onDismiss)
        }
    )
}

@Composable
fun CreateDebtDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, Double) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var totalAmount by remember { mutableStateOf("") }

    AppPopupDialog(
        onDismissRequest = onDismiss,
        icon = { PopupHeaderIcon(Icons.Filled.CreditCard) },
        title = { PopupTitle("Nueva deuda o prestamo", "Lleva control de pagos pendientes") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                PopupInputField(
                    value = name,
                    onValueChange = { name = it },
                    label = "Nombre"
                )
                PopupInputField(
                    value = totalAmount,
                    onValueChange = { totalAmount = it },
                    label = "Monto total"
                )
            }
        },
        confirmButton = {
            PopupConfirmButton(
                text = "Guardar",
                onClick = {
                    val parsedTotal = totalAmount.toDoubleOrNull()
                    if (name.isNotBlank() && parsedTotal != null && parsedTotal > 0.0) {
                        onConfirm(name.trim(), parsedTotal)
                    }
                }
            )
        },
        dismissButton = {
            PopupDismissButton(onClick = onDismiss)
        }
    )
}

@Composable
fun PayDebtDialog(
    debtName: String,
    onDismiss: () -> Unit,
    onConfirm: (Double) -> Unit
) {
    var amount by remember { mutableStateOf("") }

    AppPopupDialog(
        onDismissRequest = onDismiss,
        icon = { PopupHeaderIcon(Icons.Filled.Paid) },
        title = { PopupTitle("Abonar deuda", "Reduce saldo pendiente de forma inmediata") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(debtName)
                PopupInputField(
                    value = amount,
                    onValueChange = { amount = it },
                    label = "Monto a abonar"
                )
            }
        },
        confirmButton = {
            PopupConfirmButton(
                text = "Abonar",
                onClick = {
                    val parsedAmount = amount.toDoubleOrNull()
                    if (parsedAmount != null && parsedAmount > 0.0) {
                        onConfirm(parsedAmount)
                    }
                }
            )
        },
        dismissButton = {
            PopupDismissButton(onClick = onDismiss)
        }
    )
}

@Composable
fun CreateRecurringPlanDialog(
    savingGoals: List<FinanceSavingGoal>,
    debts: List<FinanceDebt>,
    onDismiss: () -> Unit,
    onConfirm: (RecurringTargetType, Long, Double, Int) -> Unit
) {
    var targetType by remember { mutableStateOf(RecurringTargetType.SAVING_GOAL) }
    var amount by remember { mutableStateOf("") }
    var dayOfMonth by remember { mutableStateOf("1") }
    val options = if (targetType == RecurringTargetType.SAVING_GOAL) {
        savingGoals.map { it.id to it.name }
    } else {
        debts.map { it.id to it.name }
    }
    var targetId by remember(targetType, savingGoals, debts) {
        mutableStateOf(options.firstOrNull()?.first ?: 0L)
    }

    AppPopupDialog(
        onDismissRequest = onDismiss,
        icon = { PopupHeaderIcon(Icons.Filled.Autorenew) },
        title = { PopupTitle("Nueva automatizacion", "Programa aportes y pagos automaticamente") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Tipo")
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    RecurringTargetType.entries.forEach { option ->
                        FilterChip(
                            selected = option == targetType,
                            onClick = { targetType = option },
                            label = { Text(option.label) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primary,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }

                Text("Objetivo")
                if (options.isEmpty()) {
                    Text(
                        if (targetType == RecurringTargetType.SAVING_GOAL) {
                            "No hay metas disponibles."
                        } else {
                            "No hay deudas disponibles."
                        },
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(options, key = { it.first }) { option ->
                            FilterChip(
                                selected = option.first == targetId,
                                onClick = { targetId = option.first },
                                label = { Text(option.second) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                                    selectedLabelColor = Color.White
                                )
                            )
                        }
                    }
                }

                PopupInputField(
                    value = amount,
                    onValueChange = { amount = it },
                    label = "Monto mensual"
                )
                PopupInputField(
                    value = dayOfMonth,
                    onValueChange = { dayOfMonth = it },
                    label = "Dia del mes (1-31)"
                )
            }
        },
        confirmButton = {
            PopupConfirmButton(
                text = "Guardar",
                onClick = {
                    val parsedAmount = amount.toDoubleOrNull()
                    val parsedDay = dayOfMonth.toIntOrNull()
                    if (targetId > 0L && parsedAmount != null && parsedAmount > 0.0 && parsedDay != null) {
                        onConfirm(targetType, targetId, parsedAmount, parsedDay.coerceIn(1, 31))
                    }
                },
                enabled = options.isNotEmpty()
            )
        },
        dismissButton = {
            PopupDismissButton(onClick = onDismiss)
        }
    )
}

@Composable
fun UserSetupDialog(
    onConfirm: (String, String?) -> Unit
) {
    val context = LocalContext.current
    var name by remember { mutableStateOf("") }
    var avatarUri by remember { mutableStateOf<String?>(null) }
    val pickAvatarLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri != null) {
            runCatching {
                context.contentResolver.takePersistableUriPermission(
                    uri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
            }
            avatarUri = uri.toString()
        }
    }
    AppPopupDialog(
        onDismissRequest = {},
        icon = { PopupHeaderIcon(Icons.Filled.Person) },
        title = { PopupTitle("Crea tu usuario", "Tu perfil personaliza la experiencia") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                ) {
                    ProfileAvatar(
                        avatarUri = avatarUri,
                        name = name.ifBlank { "Usuario" },
                        size = 48.dp
                    )
                    Button(onClick = { pickAvatarLauncher.launch(arrayOf("image/*")) }) {
                        Text("Elegir foto")
                    }
                }
                PopupInputField(
                    value = name,
                    onValueChange = { name = it },
                    label = "Tu nombre"
                )
            }
        },
        confirmButton = {
            PopupConfirmButton(
                text = "Continuar",
                onClick = {
                    if (name.isNotBlank()) onConfirm(name.trim(), avatarUri)
                }
            )
        }
    )
}

@Composable
fun EditProfileDialog(
    currentName: String,
    currentAvatarUri: String?,
    onDismiss: () -> Unit,
    onConfirm: (String, String?) -> Unit
) {
    val context = LocalContext.current
    var name by remember(currentName) { mutableStateOf(currentName) }
    var avatarUri by remember(currentAvatarUri) { mutableStateOf(currentAvatarUri) }
    val pickAvatarLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri != null) {
            runCatching {
                context.contentResolver.takePersistableUriPermission(
                    uri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
            }
            avatarUri = uri.toString()
        }
    }
    AppPopupDialog(
        onDismissRequest = onDismiss,
        icon = { PopupHeaderIcon(Icons.Filled.Edit) },
        title = { PopupTitle("Editar perfil", "Actualiza tu nombre y foto") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                ) {
                    ProfileAvatar(
                        avatarUri = avatarUri,
                        name = name.ifBlank { "Usuario" },
                        size = 48.dp
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Button(onClick = { pickAvatarLauncher.launch(arrayOf("image/*")) }) {
                            Text("Elegir")
                        }
                        TextButton(onClick = { avatarUri = null }) {
                            Text("Quitar")
                        }
                    }
                }
                PopupInputField(
                    value = name,
                    onValueChange = { name = it },
                    label = "Tu nombre"
                )
            }
        },
        confirmButton = {
            PopupConfirmButton(
                text = "Guardar",
                onClick = {
                    if (name.isNotBlank()) onConfirm(name.trim(), avatarUri)
                }
            )
        },
        dismissButton = {
            PopupDismissButton(onClick = onDismiss)
        }
    )
}
