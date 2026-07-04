package com.vidyasetuai.feature_institution.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "local_students")
data class LocalStudentEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String, // from organization_students.id
    
    @ColumnInfo(name = "organization_id")
    val organizationId: String,
    
    @ColumnInfo(name = "active_session_id")
    val activeSessionId: String,
    
    @ColumnInfo(name = "name")
    val name: String,
    
    @ColumnInfo(name = "gender")
    val gender: String?,
    
    @ColumnInfo(name = "sr_number")
    val srNumber: String,
    
    @ColumnInfo(name = "admission_date")
    val admissionDate: String,
    
    @ColumnInfo(name = "date_of_birth")
    val dateOfBirth: String,
    
    @ColumnInfo(name = "enrollment_number")
    val enrollmentNumber: String?,
    
    @ColumnInfo(name = "image_url")
    val imageUrl: String?,
    
    @ColumnInfo(name = "image_local_path")
    val imageLocalPath: String?,
    
    @ColumnInfo(name = "guardian_image_url")
    val guardianImageUrl: String?,
    
    @ColumnInfo(name = "guardian_image_local_path")
    val guardianImageLocalPath: String?,
    
    @ColumnInfo(name = "is_active")
    val isActive: Boolean,
    
    @ColumnInfo(name = "is_deleted")
    val isDeleted: Boolean,

    // Guardian Core Details
    @ColumnInfo(name = "guardian_id")
    val guardianId: String,
    @ColumnInfo(name = "guardian_name")
    val guardianName: String?,
    @ColumnInfo(name = "guardian_mobile")
    val guardianMobile: String?,
    @ColumnInfo(name = "guardian_relationship_name")
    val guardianRelationshipName: String?,

    // Status, Category & Blood Group
    @ColumnInfo(name = "category_id")
    val categoryId: String?,
    @ColumnInfo(name = "category_name")
    val categoryName: String?,
    @ColumnInfo(name = "blood_group_id")
    val bloodGroupId: String?,
    @ColumnInfo(name = "blood_group_name")
    val bloodGroupName: String?,
    @ColumnInfo(name = "student_status_id")
    val studentStatusId: String?,
    @ColumnInfo(name = "student_status_name")
    val studentStatusName: String?,

    // Contact & Area Details
    @ColumnInfo(name = "address_area_id")
    val addressAreaId: String?,
    @ColumnInfo(name = "address_area_name")
    val addressAreaName: String?,
    @ColumnInfo(name = "address_details")
    val addressDetails: String?,

    // Enrollment & Class details
    @ColumnInfo(name = "class_id")
    val classId: String?,
    @ColumnInfo(name = "class_name")
    val className: String?,
    @ColumnInfo(name = "section_id")
    val sectionId: String?,
    @ColumnInfo(name = "section_name")
    val sectionName: String?,
    @ColumnInfo(name = "roll_number")
    val rollNumber: Int?,

    // QR Identity details
    @ColumnInfo(name = "qr_identity_id")
    val qrIdentityId: String?,
    @ColumnInfo(name = "qr_token_hash")
    val qrTokenHash: String?,
    @ColumnInfo(name = "qr_status")
    val qrStatus: String?,
    @ColumnInfo(name = "qr_expiry_date")
    val qrExpiryDate: String?,

    // ID Card details
    @ColumnInfo(name = "id_card_id")
    val idCardId: String?,
    @ColumnInfo(name = "card_number")
    val cardNumber: String?,
    @ColumnInfo(name = "id_card_status")
    val idCardStatus: String?,
    @ColumnInfo(name = "id_card_reissue_reason")
    val idCardReissueReason: String?,

    // Home Location coordinates
    @ColumnInfo(name = "home_latitude")
    val homeLatitude: Double?,
    @ColumnInfo(name = "home_longitude")
    val homeLongitude: Double?,

    // Additional Details
    @ColumnInfo(name = "mother_tongue_id")
    val motherTongueId: String?,
    @ColumnInfo(name = "mother_tongue_name")
    val motherTongueName: String?,
    @ColumnInfo(name = "religion")
    val religion: String?,
    @ColumnInfo(name = "nationality")
    val nationality: String,
    @ColumnInfo(name = "identification_mark")
    val identificationMark: String?,
    @ColumnInfo(name = "is_single_girl_child")
    val isSingleGirlChild: Boolean,
    @ColumnInfo(name = "caste_certificate_number")
    val casteCertificateNumber: String?,
    
    // Parents Additional Info
    @ColumnInfo(name = "father_name")
    val fatherName: String?,
    @ColumnInfo(name = "father_mobile")
    val fatherMobile: String?,
    @ColumnInfo(name = "father_email")
    val fatherEmail: String?,
    @ColumnInfo(name = "father_qualification")
    val fatherQualification: String?,
    @ColumnInfo(name = "father_occupation")
    val fatherOccupation: String?,
    @ColumnInfo(name = "parents_aadhaar_father")
    val parentsAadhaarFather: String?,
    @ColumnInfo(name = "mother_name")
    val motherName: String?,
    @ColumnInfo(name = "mother_mobile")
    val motherMobile: String?,
    @ColumnInfo(name = "mother_email")
    val motherEmail: String?,
    @ColumnInfo(name = "mother_qualification")
    val motherQualification: String?,
    @ColumnInfo(name = "mother_occupation")
    val motherOccupation: String?,
    @ColumnInfo(name = "parents_aadhaar_mother")
    val parentsAadhaarMother: String?,
    @ColumnInfo(name = "family_annual_income")
    val familyAnnualIncome: Double?,

    // Permanent Address
    @ColumnInfo(name = "permanent_address_details")
    val permanentAddressDetails: String?,
    @ColumnInfo(name = "permanent_address_area")
    val permanentAddressArea: String?,
    @ColumnInfo(name = "permanent_address_area_id")
    val permanentAddressAreaId: String?,
    @ColumnInfo(name = "permanent_area_name")
    val permanentAreaName: String?,

    // Previous Schooling & TC
    @ColumnInfo(name = "previous_school_name")
    val previousSchoolName: String?,
    @ColumnInfo(name = "previous_class")
    val previousClass: String?,
    @ColumnInfo(name = "previous_board")
    val previousBoard: String?,
    @ColumnInfo(name = "tc_number")
    val tcNumber: String?,
    @ColumnInfo(name = "tc_date")
    val tcDate: String?,
    @ColumnInfo(name = "previous_marks")
    val previousMarks: Double?,

    // Physical & Medical Info
    @ColumnInfo(name = "height")
    val height: Double?,
    @ColumnInfo(name = "weight")
    val weight: Double?,
    @ColumnInfo(name = "medical_conditions")
    val medicalConditions: String?,
    @ColumnInfo(name = "regular_medications")
    val regularMedications: String?,
    @ColumnInfo(name = "emergency_contact_name")
    val emergencyContactName: String?,
    @ColumnInfo(name = "emergency_contact_phone")
    val emergencyContactPhone: String?,

    // Bank Account Details
    @ColumnInfo(name = "bank_account_number")
    val bankAccountNumber: String?,
    @ColumnInfo(name = "bank_name")
    val bankName: String?,
    @ColumnInfo(name = "bank_branch")
    val bankBranch: String?,
    @ColumnInfo(name = "bank_ifsc")
    val bankIfsc: String?,
    @ColumnInfo(name = "bank_account_holder")
    val bankAccountHolder: String?,
    
    // Aadhaar number
    @ColumnInfo(name = "student_aadhar")
    val studentAadhar: String?,

    // Sync Fields
    @ColumnInfo(name = "last_synced_at")
    val lastSyncedAt: Long,
    @ColumnInfo(name = "sync_state")
    val syncState: String
)
