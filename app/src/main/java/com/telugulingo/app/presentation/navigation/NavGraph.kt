package com.telugulingo.app.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.telugulingo.app.presentation.screen.home.HomeScreen
import com.telugulingo.app.presentation.screen.lesson.LessonScreen
import com.telugulingo.app.presentation.screen.practice.PracticeScreen
import com.telugulingo.app.presentation.screen.progress.ProgressScreen
import com.telugulingo.app.presentation.screen.quiz.QuizScreen
import com.telugulingo.app.presentation.screen.settings.SettingsScreen
import com.telugulingo.app.presentation.screen.vocabulary.VocabularyScreen

@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {
        composable(Screen.Home.route) {
            HomeScreen(
                onLessonClick = { lessonId ->
                    navController.navigate(Screen.Lesson.createRoute(lessonId))
                },
                onProgressClick = {
                    navController.navigate(Screen.Progress.route)
                },
                onSettingsClick = {
                    navController.navigate(Screen.Settings.route)
                }
            )
        }

        composable(
            route = Screen.Lesson.route,
            arguments = listOf(navArgument("lessonId") { type = NavType.LongType })
        ) { backStackEntry ->
            val lessonId = backStackEntry.arguments?.getLong("lessonId") ?: 1L
            LessonScreen(
                lessonId = lessonId,
                onBack = { navController.popBackStack() },
                onQuizClick = { navController.navigate(Screen.Quiz.createRoute(lessonId)) },
                onPracticeClick = { navController.navigate(Screen.Practice.createRoute(lessonId)) }
            )
        }

        composable(
            route = Screen.Quiz.route,
            arguments = listOf(navArgument("lessonId") { type = NavType.LongType })
        ) { backStackEntry ->
            val lessonId = backStackEntry.arguments?.getLong("lessonId") ?: 1L
            QuizScreen(
                lessonId = lessonId,
                onBack = { navController.popBackStack() },
                onComplete = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.Practice.route,
            arguments = listOf(navArgument("lessonId") { type = NavType.LongType })
        ) { backStackEntry ->
            val lessonId = backStackEntry.arguments?.getLong("lessonId") ?: 1L
            PracticeScreen(
                lessonId = lessonId,
                onBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.Vocabulary.route,
            arguments = listOf(navArgument("lessonId") { type = NavType.LongType })
        ) { backStackEntry ->
            val lessonId = backStackEntry.arguments?.getLong("lessonId") ?: 1L
            VocabularyScreen(
                lessonId = lessonId,
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Progress.route) {
            ProgressScreen(
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Settings.route) {
            SettingsScreen(
                onBack = { navController.popBackStack() }
            )
        }
    }
}
