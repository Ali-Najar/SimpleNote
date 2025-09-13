// app/src/main/java/com/example/simplenote/ui/AppNav.kt
package com.example.simplenote.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.simplenote.ui.auth.ChangePasswordScreen
import com.example.simplenote.ui.auth.LoginScreen
import com.example.simplenote.ui.auth.RegisterScreen
import com.example.simplenote.ui.home.HomeScreen
import com.example.simplenote.ui.note.NoteEditorScreen
import com.example.simplenote.ui.settings.SettingsScreen

object Routes {
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val HOME = "home"
    const val EDIT = "edit/{id}"              // id=-1 for new
    fun edit(id: Int?) = "edit/${id ?: -1}"
    const val SETTINGS = "settings"
    const val CHANGE_PW = "change_pw"         // ✅ ensure this exists
    // inside Routes
    const val ONBOARD = "onboarding"

}

@Composable
fun AppNav() {
    val nav = rememberNavController()

    NavHost(navController = nav, startDestination = Routes.ONBOARD) {

        composable(Routes.ONBOARD) {
            com.example.simplenote.ui.onboarding.OnboardingScreen(
                onLogin = { nav.navigate(Routes.LOGIN) },
                onRegister = { nav.navigate(Routes.REGISTER) }
            )
        }

        composable(Routes.LOGIN) {
            LoginScreen(
                onRegister = { nav.navigate(Routes.REGISTER) },
                onLoggedIn  = {
                    nav.navigate(Routes.HOME) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                },
                onBack = {}
            )
        }

        composable(Routes.REGISTER) {
            RegisterScreen(
                onBackToLogin = { nav.popBackStack() },
                onRegistered = { nav.popBackStack() }
            )
        }

        composable(Routes.HOME) { backStackEntry ->
            // if you use the "needsRefresh" savedState trick, keep your collect here
            val needsRefreshFlow = backStackEntry.savedStateHandle.getStateFlow("needsRefresh", false)
            val needsRefresh by needsRefreshFlow.collectAsState(initial = false)

            HomeScreen(
                onCreateNote = { nav.navigate(Routes.edit(null)) },
                onOpenNote = { id -> nav.navigate(Routes.edit(id)) },
                onOpenSettings = { nav.navigate(Routes.SETTINGS) },
                onHome = {
                    nav.navigate(Routes.HOME) {
                        launchSingleTop = true
                        popUpTo(Routes.HOME) { inclusive = false }
                    }
                },
                refreshSignal = needsRefresh,
                onRefreshConsumed = { backStackEntry.savedStateHandle["needsRefresh"] = false }
            )
        }

        composable(
            Routes.EDIT,
            arguments = listOf(navArgument("id") { type = NavType.IntType })
        ) { entry ->
            val id = entry.arguments?.getInt("id")?.takeIf { it >= 0 }
            NoteEditorScreen(
                noteId = id,
                onBack = {
                    // ask Home to refresh
                    nav.previousBackStackEntry?.savedStateHandle?.set("needsRefresh", true)
                    nav.popBackStack()
                }
            )
        }

        composable(Routes.SETTINGS) {
            SettingsScreen(
                onBack = { nav.popBackStack() },
                onChangePassword = { nav.navigate(Routes.CHANGE_PW) },  // ✅ navigate to an existing destination
                onLoggedOut = {
                    nav.navigate(Routes.LOGIN) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.CHANGE_PW) {                     // ✅ this was likely missing
            ChangePasswordScreen(onBack = { nav.popBackStack() })
        }
    }
}
