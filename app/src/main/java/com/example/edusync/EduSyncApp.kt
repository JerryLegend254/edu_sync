package com.example.edusync


import android.content.res.Resources
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.ScaffoldState
import androidx.compose.material.Snackbar
import androidx.compose.material.SnackbarHost
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LibraryBooks
import androidx.compose.material.icons.filled.People
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.edusync.common.snackbar.SnackbarManager
import com.example.edusync.ui.screens.add_study_group.AddGroupScreen
import com.example.edusync.ui.screens.add_task.AddTaskScreen
import com.example.edusync.ui.screens.auth.forgot_password.ForgotPasswordScreen
import com.example.edusync.ui.screens.auth.login.LoginScreen
import com.example.edusync.ui.screens.auth.sign_up.SignUpScreen
import com.example.edusync.ui.screens.home.HomeScreen
import com.example.edusync.ui.screens.profile.ProfileScreen
import com.example.edusync.ui.screens.repository.RepositoryScreen
import com.example.edusync.ui.screens.splash.SplashScreen
import com.example.edusync.ui.screens.study_groups.JoinGroupScreen
import com.example.edusync.ui.theme.EduSyncTheme
import kotlinx.coroutines.CoroutineScope

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun EduSyncApp() {
    EduSyncTheme {
        Surface(color = MaterialTheme.colors.background) {
            val appState = rememberAppState()

            val navBackStackEntry by appState.navController.currentBackStackEntryAsState()
            val currentRoute = navBackStackEntry?.destination?.route

            val noBottomBarScreens = listOf(
                SPLASH_SCREEN,
                LOGIN_SCREEN,
                SIGNUP_SCREEN,
                FORGOT_PASSWORD_SCREEN
            )

            // Check if bottom bar should be shown for current route
            val shouldShowBottomBar =
                currentRoute != null && !noBottomBarScreens.contains(currentRoute)

            Scaffold(
                snackbarHost = {
                    SnackbarHost(
                        hostState = it,
                        modifier = Modifier.padding(8.dp),
                        snackbar = { snackbarData ->
                            Snackbar(snackbarData, contentColor = MaterialTheme.colors.onPrimary)
                        }
                    )
                },
                scaffoldState = appState.scaffoldState,
                bottomBar = {
                    if (shouldShowBottomBar) {
                        Box(
                            modifier = Modifier
                                .background(MaterialTheme.colors.primary)
                                .padding(bottom = 8.dp)
                        ) {
                            BottomNavigation {
                                BottomNavItem.getBtmNavs().forEach { bottomNav ->
                                    BottomNavigationItem(
                                        icon = {
                                            Icon(
                                                bottomNav.icon,
                                                contentDescription = bottomNav.label
                                            )
                                        },
                                        onClick = { appState.navigate(bottomNav.route) },
                                        selected = currentRoute == bottomNav.route,
                                        label = { Text(bottomNav.label) }
                                    )
                                }
                            }
                        }
                    }
                }
            ) { innerPaddingModifier ->
                NavHost(
                    navController = appState.navController,
                    startDestination = SPLASH_SCREEN,
                    modifier = Modifier.padding(innerPaddingModifier)
                ) {
                    edusyncGraph(appState)
                }
            }
        }
    }
}


@Composable
fun rememberAppState(
    scaffoldState: ScaffoldState = rememberScaffoldState(),
    navController: NavHostController = rememberNavController(),
    snackbarManager: SnackbarManager = SnackbarManager,
    resources: Resources = resources(),
    coroutineScope: CoroutineScope = rememberCoroutineScope()
) =
    remember(scaffoldState, navController, snackbarManager, resources, coroutineScope) {
        EduSyncAppState(scaffoldState, navController, snackbarManager, resources, coroutineScope)
    }

@Composable
@ReadOnlyComposable
fun resources(): Resources {
    LocalConfiguration.current
    return LocalContext.current.resources
}

@RequiresApi(Build.VERSION_CODES.O)
@ExperimentalMaterialApi
fun NavGraphBuilder.edusyncGraph(appState: EduSyncAppState) {
    composable(SPLASH_SCREEN) {
        SplashScreen(openAndPopUp = { route, popUp -> appState.navigateAndPopUp(route, popUp) })
    }

    composable(LOGIN_SCREEN) {
        LoginScreen(openAndPopUp = { route, popUp ->
            appState.navigateAndPopUp(
                route,
                popUp
            )
        })
    }

    composable(SIGNUP_SCREEN) {
        SignUpScreen(openAndPopUp = { route, popUp ->
            appState.navigateAndPopUp(
                route,
                popUp
            )
        })
    }

    composable(FORGOT_PASSWORD_SCREEN) {
        ForgotPasswordScreen(openScreen = { route ->
            appState.navigate(
                route
            )
        })
    }

    composable(PROFILE_SCREEN) {
        ProfileScreen(
            restartApp = { route -> appState.clearAndNavigate(route) },
            openScreen = { route -> appState.navigate(route) }
        )
    }

    composable(HOME_SCREEN){
        HomeScreen(openScreen = {route -> appState.navigate((route))})
    }

    composable(ADD_TASK_SCREEN){
        AddTaskScreen(openScreen = {route -> appState.navigate(route)})
    }

    composable(JOIN_STUDY_GROUP_SCREEN) {
        JoinGroupScreen(onNavigateBack = {appState.popUp()}, openScreen = {route -> appState.navigate(route)})
    }

    composable(ADD_STUDY_GROUP_SCREEN){
        AddGroupScreen(onNavigateBack = {appState.popUp()}, onGroupCreated = {appState.popUp()})
    }

    composable(REPOSITORY_SCREEN) {
        RepositoryScreen()
    }



}

sealed class BottomNavItem(val route: String, val icon: ImageVector, val label: String) {
    data object Home : BottomNavItem(HOME_SCREEN, icon = Icons.Default.Home, "Home")
    data object Planner : BottomNavItem(ADD_TASK_SCREEN, icon = Icons.Default.AddCircle, "Planner")
    data object StudyGroups : BottomNavItem(JOIN_STUDY_GROUP_SCREEN, icon = Icons.Default.People, "Study Groups")
    data object Repository : BottomNavItem(REPOSITORY_SCREEN, icon = Icons.Default.LibraryBooks, "Repository")

    companion object {
        fun getBtmNavs(): List<BottomNavItem> {
            return mutableListOf<BottomNavItem>(
                BottomNavItem.Home,
                BottomNavItem.Planner,
                BottomNavItem.StudyGroups,
                BottomNavItem.Repository
            )
        }
    }
}