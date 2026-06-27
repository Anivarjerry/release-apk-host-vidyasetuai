package com.vidyasetuai.feature_profile.presentation.state

import com.vidyasetuai.feature_profile.domain.model.ContributorVerification
import com.vidyasetuai.feature_profile.domain.model.UserProfile

data class ProfileUiState(
    val isLoading: Boolean = false,
    val profile: UserProfile? = null,
    val verification: ContributorVerification? = null,
    val usernameChecking: Boolean = false,
    val usernameUnique: Boolean? = null, // null = not checked, true = unique, false = taken
    val updateSuccess: Boolean = false,
    val applySuccess: Boolean = false,
    val error: String? = null
)