package com.vidyasetuai.core.network

import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

object NetworkErrorMapper {
    fun parseErrorMessage(throwable: Throwable?): String {
        if (throwable == null) return "An unexpected error occurred. Please try again."

        val msg = throwable.message ?: ""
        val cause = throwable.cause

        // Check for offline / unknown host / connection failures
        if (throwable is UnknownHostException || cause is UnknownHostException ||
            msg.contains("UnknownHostException", ignoreCase = true) ||
            msg.contains("Unable to resolve host", ignoreCase = true) ||
            msg.contains("No address associated with hostname", ignoreCase = true) ||
            throwable is ConnectException || cause is ConnectException ||
            msg.contains("Failed to connect", ignoreCase = true)
        ) {
            return "🌐 No Internet Connection. Please check your network and try again."
        }

        // Check for timeout
        if (throwable is SocketTimeoutException || cause is SocketTimeoutException ||
            msg.contains("Timeout", ignoreCase = true) ||
            msg.contains("timed out", ignoreCase = true)
        ) {
            return "⏱️ Connection Timed Out. Please try again."
        }

        // Check for Supabase / Auth specific errors
        if (msg.contains("Invalid login credentials", ignoreCase = true) ||
            msg.contains("grant_type=password", ignoreCase = true) ||
            msg.contains("400 Bad Request", ignoreCase = true)
        ) {
            return "❌ Invalid email or password. Please check your credentials."
        }

        if (msg.contains("User already registered", ignoreCase = true) ||
            msg.contains("already exists", ignoreCase = true)
        ) {
            return "⚠️ An account with this email already exists."
        }

        if (msg.contains("Email not confirmed", ignoreCase = true)) {
            return "📧 Please confirm your email address before logging in."
        }

        // Clean user message if reasonable
        return if (msg.length in 1..80 && !msg.contains("Exception") && !msg.contains("at com.") && !msg.contains("http", ignoreCase = true)) {
            msg
        } else {
            "⚠️ Authentication failed. Please try again."
        }
    }
}
