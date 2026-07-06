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
                    route = "fab_add_case_study"
                ),
                SpeedDialItem(
                    id = "add_experience",
                    labelEn = "Add Experience",
                    labelHi = "अनुभव साझा करें",
                    icon = Lucide.MessageCircle,
                    route = "fab_add_experience"
                ),
                SpeedDialItem(
                    id = "quicks",
                    labelEn = "Quicks",
                    labelHi = "क्विक एक्शन",
                    icon = Lucide.Zap,
                    route = "fab_quicks"
                )
            )
            "journey" -> listOf(
                SpeedDialItem(
                    id = "add_journey",
                    labelEn = "Add Journey",
                    labelHi = "जर्नी शुरू करें",
                    icon = Lucide.Plus,
                    route = "fab_add_journey"
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
                        route = "fab_leave"
                    ),
                    SpeedDialItem(
                        id = "add_remarks",
                        labelEn = "Add Remarks",
                        labelHi = "टिप्पणी जोड़ें",
                        icon = Lucide.MessageCircle,
                        route = "fab_remarks_add"
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
                    "DRIVER" -> commonItems + listOf(
                        SpeedDialItem(
                            id = "start_trip",
                            labelEn = "Start Trip Attendance",
                            labelHi = "ट्रिप उपस्थिति शुरू करें",
                            icon = Lucide.Play,
                            route = "fab_start_trip"
                        )
                    )
                    "TEACHER", "ASSISTANT TEACHER", "LECTURER", "COACH", "INSTRUCTOR" -> commonItems + listOf(
                        SpeedDialItem(
                            id = "add_attendance",
                            labelEn = "Take Attendance",
                            labelHi = "उपस्थिति दर्ज करें",
                            icon = Lucide.Check,
                            route = "fab_take_attendance"
                        )
                    )
                    "ACCOUNTANT", "FINANCE", "CASHIER", "TREASURER" -> commonItems + listOf(
                        SpeedDialItem(
                            id = "add_finance",
                            labelEn = "Add Finance",
                            labelHi = "वित्त जोड़ें",
                            icon = Lucide.Coins,
                            route = "fab_add_finance",
                            requiresToastOnly = false
                        ),
                        SpeedDialItem(
                            id = "add_fee",
                            labelEn = "Collect Fee",
                            labelHi = "फीस जमा करें",
                            icon = Lucide.Coins,
                            route = "fab_fees"
                        )
                    )
                    "ADMIN", "SYSTEM ADMINISTRATOR", "SCHOOL ADMINISTRATOR", "ORG ADMIN", "PRINCIPAL", "DIRECTOR", "OWNER" -> commonItems + listOf(
                        SpeedDialItem(
                            id = "add_attendance",
                            labelEn = "Take Attendance",
                            labelHi = "उपस्थिति दर्ज करें",
                            icon = Lucide.Check,
                            route = "fab_take_attendance"
                        ),
                        SpeedDialItem(
                            id = "add_fee",
                            labelEn = "Collect Fee",
                            labelHi = "फीस जमा करें",
                            icon = Lucide.Coins,
                            route = "fab_fees"
                        )
                    )
                    else -> commonItems
                }
            }
            "profile" -> listOf(
                SpeedDialItem(
                    id = "search_user",
                    labelEn = "Search Users",
                    labelHi = "यूज़र्स खोजें",
                    icon = Lucide.Search,
                    route = "fab_search_user"
                )
            )
            else -> emptyList()
        }
    }
}
