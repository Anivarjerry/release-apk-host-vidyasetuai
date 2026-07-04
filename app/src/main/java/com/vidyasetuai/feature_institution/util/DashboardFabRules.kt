package com.vidyasetuai.feature_institution.util

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.icons.lucide.*

/**
 * Rules and configuration for the Floating Action Button (FAB) Speed Dial.
 * This class maps the active tab and user roles to specific action items that appear above the FAB.
 */
object DashboardFabRules {

    data class SpeedDialItem(
        val id: String,
        val labelEn: String,
        val labelHi: String,
        val icon: ImageVector,
        val route: String,
        val requiresToastOnly: Boolean = false
    )

    /**
     * Resolves the list of Speed Dial items for a specific active tab and institution role.
     */
    fun getSpeedDialItemsForTab(activeTab: String, role: String): List<SpeedDialItem> {
        return when (activeTab.lowercase().trim()) {
            "home" -> listOf(
                SpeedDialItem(
                    id = "add_case_study",
                    labelEn = "Add Case Study",
                    labelHi = "केस स्टडी जोड़ें",
                    icon = Lucide.FileText,
                    route = "add_case_study"
                ),
                SpeedDialItem(
                    id = "add_experience",
                    labelEn = "Add Experience",
                    labelHi = "अनुभव साझा करें",
                    icon = Lucide.MessageCircle,
                    route = "add_experience"
                )
            )
            "journey" -> listOf(
                SpeedDialItem(
                    id = "add_journey",
                    labelEn = "Add Journey",
                    labelHi = "जर्नी शुरू करें",
                    icon = Lucide.Plus,
                    route = "add_journey"
                )
            )
            "institute" -> {
                val normalizedRole = role.trim().uppercase()

                // Common items for everyone inside the Institute tab
                val commonItems = listOf(
                    SpeedDialItem(
                        id = "apply_leave",
                        labelEn = "Apply Leave",
                        labelHi = "छुट्टी के लिए आवेदन",
                        icon = Lucide.Calendar,
                        route = "leave"
                    ),
                    SpeedDialItem(
                        id = "add_remarks",
                        labelEn = "Add Remarks",
                        labelHi = "टिप्पणी जोड़ें",
                        icon = Lucide.MessageCircle,
                        route = "remarks_add"
                    )
                )

                when (normalizedRole) {
                    "GUARDIAN" -> commonItems + listOf(
                        SpeedDialItem(
                            id = "set_location",
                            labelEn = "Set Home Location",
                            labelHi = "घर का स्थान सेट करें",
                            icon = Lucide.MapPin,
                            route = "set_home_location"
                        )
                    )
                    "STUDENT" -> commonItems + listOf(
                        SpeedDialItem(
                            id = "set_location",
                            labelEn = "Set Home Location",
                            labelHi = "घर का स्थान सेट करें",
                            icon = Lucide.MapPin,
                            route = "set_home_location"
                        )
                    )
                    "TEACHER", "ASSISTANT TEACHER", "LECTURER", "COACH", "INSTRUCTOR" -> commonItems + listOf(
                        SpeedDialItem(
                            id = "add_attendance",
                            labelEn = "Take Attendance",
                            labelHi = "उपस्थिति दर्ज करें",
                            icon = Lucide.Check,
                            route = "take_attendance"
                        )
                    )
                    "ACCOUNTANT", "FINANCE", "CASHIER", "TREASURER" -> commonItems + listOf(
                        SpeedDialItem(
                            id = "add_finance",
                            labelEn = "Add Finance",
                            labelHi = "वित्त जोड़ें",
                            icon = Lucide.Coins,
                            route = "finance_toast",
                            requiresToastOnly = true
                        ),
                        SpeedDialItem(
                            id = "add_fee",
                            labelEn = "Collect Fee",
                            labelHi = "फीस जमा करें",
                            icon = Lucide.Coins,
                            route = "fees"
                        )
                    )
                    "ADMIN", "SYSTEM ADMINISTRATOR", "SCHOOL ADMINISTRATOR", "ORG ADMIN", "PRINCIPAL", "DIRECTOR", "OWNER" -> commonItems + listOf(
                        SpeedDialItem(
                            id = "add_attendance",
                            labelEn = "Take Attendance",
                            labelHi = "उपस्थिति दर्ज करें",
                            icon = Lucide.Check,
                            route = "take_attendance"
                        ),
                        SpeedDialItem(
                            id = "add_fee",
                            labelEn = "Collect Fee",
                            labelHi = "फीस जमा करें",
                            icon = Lucide.Coins,
                            route = "fees"
                        ),
                        SpeedDialItem(
                            id = "set_location",
                            labelEn = "Set Home Location",
                            labelHi = "घर का स्थान सेट करें",
                            icon = Lucide.MapPin,
                            route = "student_directory"
                        )
                    )
                    else -> commonItems
                }
            }
            else -> emptyList()
        }
    }
}
