package com.example.duogoal_android

import androidx.activity.OnBackPressedDispatcher
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.StateRestorationTester
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.navigation.compose.ComposeNavigator
import androidx.navigation.testing.TestNavHostController
import com.example.duogoal_android.data.auth.SessionState
import com.example.duogoal_android.ui.SessionGate
import com.example.duogoal_android.ui.components.DuoText
import com.example.duogoal_android.ui.navigation.DuoDestination
import com.example.duogoal_android.ui.navigation.DuoNavigation
import com.example.duogoal_android.ui.theme.DuoGoalTheme
import com.example.duogoal_android.ui.theme.DuoTheme
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

/** Выполняется без backend на устройстве; проверяет реальный Navigation Compose. */
class NavigationSessionTest {
    @get:Rule val compose = createComposeRule()

    @Test fun repeatedTabSelectionDoesNotDuplicateBackStackAndSystemBackReturnsToFeed() {
        lateinit var controller: TestNavHostController
        lateinit var back: OnBackPressedDispatcher
        compose.setContent {
            val context = LocalContext.current
            controller = remember { TestNavHostController(context).apply { navigatorProvider.addNavigator(ComposeNavigator()) } }
            back = LocalOnBackPressedDispatcherOwner.current!!.onBackPressedDispatcher
            DuoGoalTheme { DuoNavigation({}, controller) }
        }
        compose.onNodeWithText("Профиль").performClick()
        compose.onNodeWithText("Профиль").performClick()
        compose.runOnIdle {
            assertEquals(DuoDestination.Profile.route, controller.currentDestination?.route)
            back.onBackPressed()
        }
        compose.runOnIdle { assertEquals(DuoDestination.Feed.route, controller.currentDestination?.route) }
    }

    @Test fun nestedGoalSurvivesSavedStateRestorationAndToolbarBackReturnsToProfile() {
        val restoration = StateRestorationTester(compose)
        restoration.setContent { DuoGoalTheme { DuoNavigation({}) } }
        compose.onNodeWithText("Профиль").performClick()
        compose.onNodeWithText("Английский каждый день").performScrollTo().performClick()
        compose.onNodeWithText("Ваша цель").assertIsDisplayed()
        restoration.emulateSavedInstanceStateRestore()
        compose.onNodeWithText("Ваша цель").assertIsDisplayed()
        compose.onNodeWithContentDescription("Назад").performClick()
        compose.onNodeWithText("Твой путь").assertIsDisplayed()
    }

    @Test fun signingOutRemovesPrivateUiAndNewAccountStartsWithFreshNavigation() {
        var state by mutableStateOf<SessionState>(SessionState.SignedIn("first-session"))
        compose.setContent {
            SessionGate(state, {}, { state = SessionState.SigningOut }) {
                DuoText("SIGN_IN_ONLY", 16, DuoTheme.colors.fg)
            }
        }
        compose.onNodeWithText("Профиль").performClick()
        compose.onNodeWithTag("profile-logout").performScrollTo().performClick()
        compose.onNodeWithTag("confirm-logout").performClick()
        compose.onNodeWithTag("session-status").assertIsDisplayed()
        compose.onNodeWithText("Твой путь").assertDoesNotExist()
        compose.runOnIdle { state = SessionState.SignedOut() }
        compose.onNodeWithText("SIGN_IN_ONLY").assertIsDisplayed()
        compose.runOnIdle { state = SessionState.SignedIn("second-session") }
        compose.onNodeWithText("Вместе\nполучается больше.").assertIsDisplayed()
        compose.onNodeWithText("Твой путь").assertDoesNotExist()
    }

    @Test fun restoringAndNetworkFailureNeverExposeSavedPrivateScreen() {
        var state by mutableStateOf<SessionState>(SessionState.SignedIn("same-session"))
        val restoration = StateRestorationTester(compose)
        restoration.setContent {
            SessionGate(state, {}, {}) { DuoText("SIGN_IN_ONLY", 16, DuoTheme.colors.fg) }
        }
        compose.onNodeWithText("Профиль").performClick()
        compose.onNodeWithText("Английский каждый день").performScrollTo().performClick()
        compose.runOnIdle { state = SessionState.Restoring }
        restoration.emulateSavedInstanceStateRestore()
        compose.onNodeWithText("Ваша цель").assertDoesNotExist()
        compose.runOnIdle { state = SessionState.RestoreFailed("Нет сети") }
        compose.onNodeWithText("Повторить попытку").assertIsDisplayed()
        compose.onNodeWithText("SIGN_IN_ONLY").assertDoesNotExist()
        compose.runOnIdle { state = SessionState.SignedIn("same-session") }
        compose.onNodeWithText("Ваша цель").assertIsDisplayed()
    }
}
