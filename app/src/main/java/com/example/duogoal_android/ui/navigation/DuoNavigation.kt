package com.example.duogoal_android.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.duogoal_android.ui.components.DuoBottomNav
import com.example.duogoal_android.ui.components.DuoTab
import com.example.duogoal_android.ui.screens.ChatScreen
import com.example.duogoal_android.ui.screens.ChatsScreen
import com.example.duogoal_android.ui.screens.CreateGoalScreen
import com.example.duogoal_android.ui.screens.FeedScreen
import com.example.duogoal_android.ui.screens.GoalActiveScreen
import com.example.duogoal_android.ui.screens.GoalWaitingScreen
import com.example.duogoal_android.ui.screens.InvitationScreen
import com.example.duogoal_android.ui.screens.NotificationsScreen
import com.example.duogoal_android.ui.screens.PartnerPickerScreen
import com.example.duogoal_android.ui.screens.ProfileScreen

/** Только приватные маршруты; auth находится за пределами этого back stack. */
enum class DuoDestination(val route: String) {
    Feed("feed"), Chats("chats"), Profile("profile"), Chat("chat"),
    Notifications("notifications"), CreateGoal("create-goal"), PartnerPicker("partner-picker"),
    GoalActive("goal-active"), GoalWaiting("goal-waiting"), Invitation("invitation"),
}

@Composable
fun DuoNavigation(onLogout: () -> Unit, navController: NavHostController = rememberNavController()) {
    val entry by navController.currentBackStackEntryAsState()
    val route = entry?.destination?.route
    val selectedTab = when (route) {
        DuoDestination.Feed.route -> DuoTab.Feed
        DuoDestination.Chats.route -> DuoTab.Chats
        DuoDestination.Profile.route -> DuoTab.Profile
        else -> null
    }
    val back: () -> Unit = { navController.popBackStack() }
    fun open(destination: DuoDestination) {
        navController.navigate(destination.route) { launchSingleTop = true }
    }
    Column(Modifier.fillMaxSize()) {
        Box(Modifier.weight(1f).fillMaxWidth()) {
            NavHost(navController, startDestination = DuoDestination.Feed.route) {
                composable(DuoDestination.Feed.route) {
                    FeedScreen { open(DuoDestination.Notifications) }
                }
                composable(DuoDestination.Chats.route) {
                    ChatsScreen({ open(DuoDestination.Chat) }, { open(DuoDestination.Notifications) })
                }
                composable(DuoDestination.Profile.route) {
                    ProfileScreen(
                        onOpenNotifications = { open(DuoDestination.Notifications) },
                        onOpenActiveGoal = { open(DuoDestination.GoalActive) },
                        onOpenWaitingGoal = { open(DuoDestination.GoalWaiting) },
                        onCreateGoal = { open(DuoDestination.CreateGoal) },
                        onLogout = onLogout,
                    )
                }
                composable(DuoDestination.Chat.route) { ChatScreen(onBack = back) }
                composable(DuoDestination.Notifications.route) {
                    NotificationsScreen(back) { open(DuoDestination.Invitation) }
                }
                composable(DuoDestination.CreateGoal.route) {
                    CreateGoalScreen(back) { open(DuoDestination.PartnerPicker) }
                }
                composable(DuoDestination.PartnerPicker.route) {
                    PartnerPickerScreen(back) {
                        navController.navigate(DuoDestination.GoalWaiting.route) {
                            popUpTo(DuoDestination.CreateGoal.route) { inclusive = true }
                            launchSingleTop = true
                        }
                    }
                }
                composable(DuoDestination.GoalActive.route) {
                    GoalActiveScreen(back) { open(DuoDestination.Chat) }
                }
                composable(DuoDestination.GoalWaiting.route) { GoalWaitingScreen(back) }
                composable(DuoDestination.Invitation.route) {
                    InvitationScreen(
                        onBack = back,
                        onAccept = {
                            navController.navigate(DuoDestination.GoalActive.route) {
                                popUpTo(DuoDestination.Invitation.route) { inclusive = true }
                                launchSingleTop = true
                            }
                        },
                        onDecline = back,
                    )
                }
            }
        }
        if (selectedTab != null) {
            DuoBottomNav(
                selected = selectedTab,
                onSelect = { tab ->
                    val destination = when (tab) {
                        DuoTab.Feed -> DuoDestination.Feed
                        DuoTab.Chats -> DuoDestination.Chats
                        DuoTab.Profile -> DuoDestination.Profile
                    }
                    navController.navigate(destination.route) {
                        popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                modifier = Modifier.padding(top = 6.dp, bottom = 10.dp),
            )
        }
    }
}
