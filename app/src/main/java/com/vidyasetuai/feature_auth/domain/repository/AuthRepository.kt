package com.vidyasetuai.feature_auth.domain.repository

interface AuthRepository {
    suspend fun signUp(email: String, password: String): Result<Unit>
    suspend fun signIn(email: String, password: String): Result<Unit>
    suspend fun signOut(): Result<Unit>
    suspend fun restoreSession(): Result<Boolean>
    suspend fun validateSessionOnline(): Boolean
    suspend fun sendPasswordResetOtp(email: String): Result<Unit>
    suspend fun verifyRecoveryOtp(email: String, otp: String): Result<Unit>
    suspend fun updatePassword(newPassword: String): Result<Unit>
    suspend fun sendLoginOtp(email: String): Result<Unit>
    suspend fun verifyLoginOtp(email: String, otp: String): Result<Unit>
}