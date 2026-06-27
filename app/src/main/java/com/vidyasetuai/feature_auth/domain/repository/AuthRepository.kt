package com.vidyasetuai.feature_auth.domain.repository

interface AuthRepository {
    suspend fun signUp(email: String, password: String): Result<Unit>
    suspend fun signIn(email: String, password: String): Result<Unit>
    suspend fun signOut(): Result<Unit>
    suspend fun restoreSession(): Result<Boolean>
    suspend fun validateSessionOnline(): Boolean
}