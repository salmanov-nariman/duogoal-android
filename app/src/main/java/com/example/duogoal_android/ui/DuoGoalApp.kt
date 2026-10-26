package com.example.duogoal_android.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.duogoal_android.ui.components.DuoBottomNav
import com.example.duogoal_android.ui.components.DuoTab
import com.example.duogoal_android.ui.screens.ChatScreen
import com.example.duogoal_android.ui.screens.ChatsScreen
import com.example.duogoal_android.ui.screens.CreateGoalScreen
import com.example.duogoal_android.ui.screens.FeedScreen
import com.example.duogoal_android.ui.screens.GoalActiveScreen
import com.example.duogoal_android.ui.screens.GoalWaitingScreen
import com.example.duogoal_android.ui.screens.InvitationScreen
import com.example.duogoal_android.ui.screens.AuthScreen
import com.example.duogoal_android.ui.auth.AuthViewModel
import com.example.duogoal_android.ui.screens.NotificationsScreen
import com.example.duogoal_android.ui.screens.PartnerPickerScreen
import com.example.duogoal_android.ui.screens.ProfileScreen
import com.example.duogoal_android.ui.theme.DuoGoalTheme
import com.example.duogoal_android.ui.theme.DuoTheme

/** Маршруты: авторизация подключена к API, остальные данные пока демонстрационные. */
enum class DuoScreen {
    Login, Feed, Chats, Chat, Profile, Notifications,
    CreateGoal, PartnerPicker, GoalActive, GoalWaiting, Invitation,
}

/**
 * Простейшая навигация на back-стеке, чтобы можно было посмотреть все страницы.
 * Позже её можно заменить на Navigation Compose без изменения самих экранов.
 */
@Composable
fun DuoGoalApp() {
    DuoGoalTheme {
        val authViewModel: AuthViewModel = viewModel()
        val authState = authViewModel.state.collectAsState().value
        val backStack = remember { mutableStateListOf(DuoScreen.Login) }
        val current = backStack.last()

        fun push(screen: DuoScreen) {
            if (backStack.last() != screen) backStack.add(screen)
        }

        fun pop() {
            if (backStack.size > 1) backStack.removeAt(backStack.lastIndex)
        }

        fun switchTab(tab: DuoTab) {
            // Смена вкладки сбрасывает стек до корневой страницы вкладки
            backStack.clear()
            backStack.add(
                when (tab) {
                    DuoTab.Feed -> DuoScreen.Feed
                    DuoTab.Chats -> DuoScreen.Chats
                    DuoTab.Profile -> DuoScreen.Profile
                }
            )
        }

        BackHandler(enabled = backStack.size > 1) { pop() }

        LaunchedEffect(authState.authenticated) {
            if (authState.authenticated && current == DuoScreen.Login) switchTab(DuoTab.Feed)
        }
        val showBottomNav = current in listOf(DuoScreen.Feed, DuoScreen.Chats, DuoScreen.Profile)

        Column(
            Modifier
                .fillMaxSize()
                .background(DuoTheme.colors.bg)
                .statusBarsPadding()
                .navigationBarsPadding(),
        ) {
            Box(Modifier.weight(1f).fillMaxWidth()) {
                when (current) {
                    DuoScreen.Login -> AuthScreen(authState, authViewModel::setMode, authViewModel::updateUsername, authViewModel::updateEmail, authViewModel::updatePassword, authViewModel::submit)
                    DuoScreen.Feed -> FeedScreen(onOpenNotifications = { push(DuoScreen.Notifications) })
                    DuoScreen.Chats -> ChatsScreen(
                        onOpenChat = { push(DuoScreen.Chat) },
                        onOpenNotifications = { push(DuoScreen.Notifications) },
                    )
                    DuoScreen.Chat -> ChatScreen(onBack = ::pop)
                    DuoScreen.Profile -> ProfileScreen(
                        onOpenNotifications = { push(DuoScreen.Notifications) },
                        onOpenActiveGoal = { push(DuoScreen.GoalActive) },
                        onOpenWaitingGoal = { push(DuoScreen.GoalWaiting) },
                        onCreateGoal = { push(DuoScreen.CreateGoal) },
                    )
                    DuoScreen.Notifications -> NotificationsScreen(
                        onBack = ::pop,
                        onOpenInvitation = { push(DuoScreen.Invitation) },
                    )
                    DuoScreen.CreateGoal -> CreateGoalScreen(
                        onBack = ::pop,
                        onCreate = { push(DuoScreen.PartnerPicker) },
                    )
                    DuoScreen.PartnerPicker -> PartnerPickerScreen(
                        onBack = ::pop,
                        onSendRequest = { push(DuoScreen.GoalWaiting) },
                    )
                    DuoScreen.GoalActive -> GoalActiveScreen(onBack = ::pop, onOpenChat = { push(DuoScreen.Chat) })
                    DuoScreen.GoalWaiting -> GoalWaitingScreen(onBack = ::pop)
                    DuoScreen.Invitation -> InvitationScreen(
                        onBack = ::pop,
                        onAccept = ::pop,
                        onDecline = ::pop,
                    )
                }
            }
            if (showBottomNav) {
                DuoBottomNav(
                    selected = when (current) {
                        DuoScreen.Chats -> DuoTab.Chats
                        DuoScreen.Profile -> DuoTab.Profile
                        else -> DuoTab.Feed
                    },
                    onSelect = ::switchTab,
                    modifier = Modifier.padding(top = 6.dp, bottom = 10.dp),
                )
            }
        }
    }
}
