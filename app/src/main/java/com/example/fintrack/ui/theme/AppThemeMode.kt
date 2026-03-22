package com.example.fintrack.ui.theme

enum class AppThemeMode(val prefValue: String, val label: String) {
    SYSTEM("system", "Sistema"),
    LIGHT("light", "Claro"),
    DARK("dark", "Oscuro");

    companion object {
        fun fromPref(value: String?): AppThemeMode {
            return entries.firstOrNull { it.prefValue == value } ?: SYSTEM
        }
    }
}
