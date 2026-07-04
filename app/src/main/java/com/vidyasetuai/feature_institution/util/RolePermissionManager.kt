package com.vidyasetuai.feature_institution.util

/**
 * विद्यासेतु भूमिका-आधारित अनुमति प्रबंधक (Role Permission Manager)
 * 
 * यह वर्ग सभी कर्मचारियों, छात्रों और अभिभावकों की भूमिकाओं (Roles) के लिए
 * अनुमति नियमों (Permissions Rules) को केंद्रीय रूप से नियंत्रित करता है।
 */
object RolePermissionManager {

    // 1. सुविधा पहुँच नियंत्रण (Feature Access Control)
    fun hasAccessToFeature(roleCode: String, feature: InstitutionFeature): Boolean {
        val normalizedRole = roleCode.trim().uppercase()
        return when (normalizedRole) {
            // सिस्टम एडमिनिस्ट्रेटर और प्रिंसिपल सभी कुछ देख सकते हैं
            "ADMIN", "PRINCIPAL", "SYSTEM_ADMINISTRATOR" -> true

            // शिक्षकों (Teachers) की अनुमतियाँ
            "TEACHER" -> feature in listOf(
                InstitutionFeature.ATTENDANCE_TAKE,
                InstitutionFeature.ATTENDANCE_HISTORY,
                InstitutionFeature.EXAMS_MARKS_ENTRY,
                InstitutionFeature.LEAVE_APPLY,
                InstitutionFeature.LEAVE_STATUS,
                InstitutionFeature.SALARY_VIEW,
                InstitutionFeature.NOTICES_VIEW,
                InstitutionFeature.STUDENT_DIRECTORY
            )

            // लेखाकार (Accountants) की अनुमतियाँ
            "ACCOUNTANT" -> feature in listOf(
                InstitutionFeature.FINANCE_DASHBOARD,
                InstitutionFeature.FEES_COLLECT,
                InstitutionFeature.SALARY_MANAGE,
                InstitutionFeature.LEAVE_APPLY,
                InstitutionFeature.LEAVE_STATUS,
                InstitutionFeature.NOTICES_VIEW
            )

            // पुस्तकालयाध्यक्ष (Librarians) की अनुमतियाँ
            "LIBRARIAN" -> feature in listOf(
                InstitutionFeature.LIBRARY_BOOKS_MANAGE,
                InstitutionFeature.LIBRARY_FINE_COLLECT,
                InstitutionFeature.LEAVE_APPLY,
                InstitutionFeature.LEAVE_STATUS,
                InstitutionFeature.NOTICES_VIEW
            )

            // चालकों (Drivers) की अनुमतियाँ
            "DRIVER" -> feature in listOf(
                InstitutionFeature.TRANSPORT_TRIPS,
                InstitutionFeature.TRANSPORT_ATTENDANCE_TAKE,
                InstitutionFeature.LEAVE_APPLY,
                InstitutionFeature.LEAVE_STATUS,
                InstitutionFeature.SALARY_VIEW
            )

            // कार्यालय सहायकों/क्लर्क की अनुमतियाँ
            "OFFICE_ASSISTANT", "CLERK" -> feature in listOf(
                InstitutionFeature.STUDENT_REGISTRY_VIEW,
                InstitutionFeature.STUDENT_DIRECTORY,
                InstitutionFeature.LEAVE_APPLY,
                InstitutionFeature.LEAVE_STATUS,
                InstitutionFeature.NOTICES_MANAGE
            )

            // छात्र (Students) की अनुमतियाँ
            "STUDENT" -> feature in listOf(
                InstitutionFeature.STUDENT_PROFILE,
                InstitutionFeature.ATTENDANCE_HISTORY,
                InstitutionFeature.EXAMS_REPORT_CARD,
                InstitutionFeature.LEAVE_APPLY,
                InstitutionFeature.LEAVE_STATUS,
                InstitutionFeature.NOTICES_VIEW,
                InstitutionFeature.TRANSPORT_LIVE_TRACK
            )

            // अभिभावक (Guardians) की अनुमतियाँ
            "GUARDIAN" -> feature in listOf(
                InstitutionFeature.CHILD_PROFILES,
                InstitutionFeature.ATTENDANCE_HISTORY,
                InstitutionFeature.FEES_VIEW_PAY,
                InstitutionFeature.LEAVE_APPLY,
                InstitutionFeature.LEAVE_STATUS,
                InstitutionFeature.NOTICES_VIEW,
                InstitutionFeature.TRANSPORT_LIVE_TRACK
            )

            // अन्य सहायक स्टाफ़ (Peon, Security, Lab Assistant, Nurse)
            "PEON", "SECURITY", "LAB_ASSISTANT", "SCHOOL_NURSE" -> feature in listOf(
                InstitutionFeature.LEAVE_APPLY,
                InstitutionFeature.LEAVE_STATUS,
                InstitutionFeature.SALARY_VIEW,
                InstitutionFeature.NOTICES_VIEW
            )

            else -> false
        }
    }

    // 2. फ़ीस एक्सेस अनुमतियाँ (Fee Type Restrictions)
    // उदाहरण: लाइब्रेरियन केवल बुक्स या ड्रेस फीस देख सकता है, अकाउंटेंट सब कुछ
    fun getAllowedFeeCategories(roleCode: String): List<String> {
        val normalizedRole = roleCode.trim().uppercase()
        return when (normalizedRole) {
            "ADMIN", "PRINCIPAL", "ACCOUNTANT", "GUARDIAN", "STUDENT" -> listOf("ALL")
            "LIBRARIAN" -> listOf("BOOKS", "LIBRARY_LATE_FINE")
            "TRANSPORT_MANAGER" -> listOf("TRANSPORT")
            else -> emptyList()
        }
    }

    // 3. क्या कर्मचारी छुट्टी स्वीकृत (Approve) कर सकता है?
    fun canApproveLeaves(roleCode: String): Boolean {
        val normalizedRole = roleCode.trim().uppercase()
        return normalizedRole in listOf("ADMIN", "PRINCIPAL", "SYSTEM_ADMINISTRATOR")
    }
}

/**
 * ऐप के भीतर विभिन्न मॉड्यूल/सुविधाओं को दर्शाने के लिए Enum
 */
enum class InstitutionFeature {
    // Student Registry
    STUDENT_REGISTRY_VIEW,
    STUDENT_DIRECTORY,
    STUDENT_PROFILE,
    CHILD_PROFILES,

    // Attendance
    ATTENDANCE_TAKE,
    ATTENDANCE_HISTORY,
    TRANSPORT_ATTENDANCE_TAKE,

    // Fees & Finance
    FINANCE_DASHBOARD,
    FEES_COLLECT,
    FEES_VIEW_PAY,

    // Exams
    EXAMS_MARKS_ENTRY,
    EXAMS_REPORT_CARD,

    // Transport
    TRANSPORT_TRIPS,
    TRANSPORT_LIVE_TRACK,

    // Library
    LIBRARY_BOOKS_MANAGE,
    LIBRARY_FINE_COLLECT,

    // Leaves & Salary
    LEAVE_APPLY,
    LEAVE_STATUS,
    SALARY_VIEW,
    SALARY_MANAGE,

    // Notice Board & Communication
    NOTICES_VIEW,
    NOTICES_MANAGE
}
