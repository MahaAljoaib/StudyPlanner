package sa.edu.iau.cshj.studyplanner.navigation

import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import sa.edu.iau.cshj.studyplanner.PlannerHomeRoute
import sa.edu.iau.cshj.studyplanner.TaskDetailRoute
import sa.edu.iau.cshj.studyplanner.TodayRoute
import sa.edu.iau.cshj.studyplanner.model.sampleStudyTasks

@Composable
fun StudyPlannerApp() {
    val navController = rememberNavController()

    Scaffold { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = PlannerHome,
            modifier = Modifier.fillMaxSize().padding(innerPadding).consumeWindowInsets(innerPadding)
        ) {
            composable<PlannerHome> {
                PlannerHomeRoute(
                    onTaskClick = { id ->
                        navController.navigate(TaskDetail(id))
                    },
                    onTodayClick = { navController.navigate(Today) { launchSingleTop = true } }
                )
            }
            composable<Today> {
                TodayRoute(onBackClick = { navController.popBackStack() })
            }
            composable<TaskDetail> { backStackEntry ->
                val detail = backStackEntry.toRoute<TaskDetail>()
                TaskDetailRoute(
                    title = sampleStudyTasks().firstOrNull { it.id == detail.id }?.title
                        ?: "Task not found",
                    onBackClick = { navController.popBackStack() }
                )
            }
        }
    }
}
