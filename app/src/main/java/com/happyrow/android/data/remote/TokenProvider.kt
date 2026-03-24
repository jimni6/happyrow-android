package com.happyrow.android.data.remote

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.gotrue.auth
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TokenProvider @Inject constructor(
    private val supabaseClient: SupabaseClient
) {
    fun getToken(): String? {
        return supabaseClient.auth.currentSessionOrNull()?.accessToken
    }
}
