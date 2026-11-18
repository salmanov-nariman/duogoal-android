package com.example.duogoal_android

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTextInput
import com.example.duogoal_android.ui.auth.AuthMode
import com.example.duogoal_android.ui.auth.AuthRequest
import com.example.duogoal_android.ui.auth.AuthUiState
import com.example.duogoal_android.ui.screens.AuthScreen
import com.example.duogoal_android.ui.theme.DuoGoalTheme
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

/** Проверяет UI без сети: поля, переключение формы, пароль и блокировку запроса. */
class AuthDesignTest {
    @get:Rule val compose = createComposeRule()

    @Test fun registrationPreservesEnteredEmailAndSubmitsCurrentForm() {
        var state by mutableStateOf(AuthUiState())
        var submitted: AuthUiState? = null
        compose.setContent {
            DuoGoalTheme(darkTheme = false) {
                AuthScreen(state, { state = state.copy(mode = it) }, { state = state.copy(username = it) }, { state = state.copy(email = it) }, { state = state.copy(password = it) }, { submitted = state }, {}, {})
            }
        }
        compose.onNodeWithTag("auth-username").assertDoesNotExist()
        compose.onNodeWithTag("auth-email").performScrollTo().performTextInput("test@example.com")
        compose.onNodeWithText("Регистрация").performScrollTo().performClick()
        compose.onNodeWithTag("auth-username").performScrollTo().performTextInput("testuser")
        compose.onNodeWithTag("auth-password").performScrollTo().performTextInput("test-password")
        compose.onNodeWithTag("auth-submit").performScrollTo().performClick()
        compose.runOnIdle {
            assertEquals(AuthMode.Register, submitted?.mode)
            assertEquals("test@example.com", submitted?.email)
            assertEquals("testuser", submitted?.username)
            assertEquals("test-password", submitted?.password)
        }
    }

    @Test fun loadingBlocksDuplicateRequests() {
        compose.setContent {
            DuoGoalTheme(darkTheme = true) { AuthScreen(AuthUiState(request = AuthRequest.Submit), {}, {}, {}, {}, {}, {}, {}) }
        }
        compose.onNodeWithTag("auth-submit").performScrollTo().assertIsNotEnabled()
        compose.onNodeWithTag("auth-email").performScrollTo().assertIsNotEnabled()
    }

    @Test fun passwordVisibilityCanBeToggledInDarkTheme() {
        compose.setContent {
            DuoGoalTheme(darkTheme = true) { AuthScreen(AuthUiState(password = "test-password"), {}, {}, {}, {}, {}, {}, {}) }
        }
        compose.onNodeWithContentDescription("Показать пароль", useUnmergedTree = true).performScrollTo().performClick()
        compose.onNodeWithContentDescription("Скрыть пароль", useUnmergedTree = true).assertIsDisplayed().performClick()
        compose.onNodeWithContentDescription("Показать пароль", useUnmergedTree = true).assertIsDisplayed()
    }

    @Test fun emailVerificationShowsCodeFieldAndResendAction() {
        var state by mutableStateOf(AuthUiState(mode = AuthMode.VerifyEmail, email = "test@example.com"))
        var submittedCode: String? = null
        compose.setContent {
            DuoGoalTheme(darkTheme = false) {
                AuthScreen(
                    state = state,
                    onMode = { state = state.copy(mode = it) },
                    onUsername = {},
                    onEmail = {},
                    onPassword = {},
                    onSubmit = { submittedCode = state.code },
                    onCode = { state = state.copy(code = it) },
                    onResend = { state = state.copy(request = AuthRequest.Resend) },
                )
            }
        }
        compose.onNodeWithText("test@example.com", substring = true).assertIsDisplayed()
        compose.onNodeWithTag("auth-password").assertDoesNotExist()
        compose.onNodeWithTag("auth-code").performScrollTo().performTextInput("012345")
        compose.onNodeWithTag("auth-submit").performScrollTo().performClick()
        compose.runOnIdle { assertEquals("012345", submittedCode) }
        compose.onNodeWithText("Отправить код ещё раз").performScrollTo().performClick()
        compose.onNodeWithText("Отправляем код…").assertIsNotEnabled()
        compose.onNodeWithTag("auth-submit").assertIsNotEnabled()
    }
}
