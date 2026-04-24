package com.telugulingo.app.presentation.navigation

sealed class Screen(val route: String) {
    data object Home : Screen("home")
    data object Lesson : Screen("lesson/{lessonId}") {
        fun createRoute(lessonId: Long) = "lesson/$lessonId"
    }
    data object Vocabulary : Screen("vocabulary/{lessonId}") {
        fun createRoute(lessonId: Long) = "vocabulary/$lessonId"
    }
    data object Quiz : Screen("quiz/{lessonId}") {
        fun createRoute(lessonId: Long) = "quiz/$lessonId"
    }
    data object Practice : Screen("practice/{lessonId}") {
        fun createRoute(lessonId: Long) = "practice/$lessonId"
    }
    data object Progress : Screen("progress")
    data object Settings : Screen("settings")
}
