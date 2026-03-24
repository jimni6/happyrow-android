package com.happyrow.android.usecases.auth

import com.happyrow.android.TestData
import com.happyrow.android.fake.FakeAuthRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class AuthUseCasesTest {

    private lateinit var repo: FakeAuthRepository
    private lateinit var registerUser: RegisterUser
    private lateinit var signInUser: SignInUser
    private lateinit var signOutUser: SignOutUser
    private lateinit var getCurrentUser: GetCurrentUser
    private lateinit var resetPassword: ResetPassword
    private lateinit var signInWithProvider: SignInWithProvider

    @Before
    fun setup() {
        repo = FakeAuthRepository()
        registerUser = RegisterUser(repo)
        signInUser = SignInUser(repo)
        signOutUser = SignOutUser(repo)
        getCurrentUser = GetCurrentUser(repo)
        resetPassword = ResetPassword(repo)
        signInWithProvider = SignInWithProvider(repo)
    }

    @Test
    fun `register with valid data succeeds`() = runTest {
        val user = registerUser(TestData.registration())
        assertNotNull(user)
        assertEquals("new@example.com", user.email)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `register with empty email throws`() = runTest {
        registerUser(TestData.registration(email = ""))
    }

    @Test(expected = IllegalArgumentException::class)
    fun `register with invalid email format throws`() = runTest {
        registerUser(TestData.registration(email = "invalid"))
    }

    @Test(expected = IllegalArgumentException::class)
    fun `register with short password throws`() = runTest {
        registerUser(TestData.registration(password = "123", confirmPassword = "123"))
    }

    @Test(expected = IllegalArgumentException::class)
    fun `register with short firstname throws`() = runTest {
        registerUser(TestData.registration(firstname = "J"))
    }

    @Test(expected = IllegalArgumentException::class)
    fun `register with short lastname throws`() = runTest {
        registerUser(TestData.registration(lastname = "D"))
    }

    @Test(expected = IllegalArgumentException::class)
    fun `register with mismatched passwords throws`() = runTest {
        registerUser(TestData.registration(password = "password123", confirmPassword = "different"))
    }

    @Test
    fun `register propagates repository error`() = runTest {
        repo.shouldThrow = RuntimeException("Network error")
        try {
            registerUser(TestData.registration())
            fail("Should have thrown")
        } catch (e: RuntimeException) {
            assertEquals("Network error", e.message)
        }
    }

    @Test
    fun `signIn with valid credentials succeeds`() = runTest {
        val session = signInUser(TestData.credentials())
        assertNotNull(session)
        assertEquals("test@example.com", session.user.email)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `signIn with empty email throws`() = runTest {
        signInUser(TestData.credentials(email = ""))
    }

    @Test(expected = IllegalArgumentException::class)
    fun `signIn with empty password throws`() = runTest {
        signInUser(TestData.credentials(password = ""))
    }

    @Test
    fun `signOut clears session`() = runTest {
        repo.currentUser = TestData.user()
        signOutUser()
        assertNull(repo.currentUser)
    }

    @Test
    fun `getCurrentUser returns user when present`() = runTest {
        repo.currentUser = TestData.user()
        val user = getCurrentUser()
        assertNotNull(user)
    }

    @Test
    fun `getCurrentUser returns null when absent`() = runTest {
        val user = getCurrentUser()
        assertNull(user)
    }

    @Test
    fun `resetPassword with valid email succeeds`() = runTest {
        resetPassword("test@example.com")
    }

    @Test(expected = IllegalArgumentException::class)
    fun `resetPassword with empty email throws`() = runTest {
        resetPassword("")
    }

    @Test(expected = IllegalArgumentException::class)
    fun `resetPassword with invalid email throws`() = runTest {
        resetPassword("invalid")
    }

    @Test
    fun `signInWithProvider succeeds`() = runTest {
        signInWithProvider()
    }
}
