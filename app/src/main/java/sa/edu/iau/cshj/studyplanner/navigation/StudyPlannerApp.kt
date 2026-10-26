package sa.edu.iau.cshj.studyplanner.navigation

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
import sa.edu.iau.cshj.studyplanner.rememberPlannerUiState

@Composable
fun StudyPlannerApp() {
    val navController = rememberNavController()
    // Keep progress above the NavHost so it survives a trip to task details.
    val plannerState = rememberPlannerUiState()

    Scaffold { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = PlannerHome,
            modifier = Modifier.fillMaxSize().padding(innerPadding)
        ) {
            composable<PlannerHome> {
                PlannerHomeRoute(
                    onTaskClick = { title ->
                        navController.navigate(TaskDetail(title))
                    },
                    plannerState = plannerState
                )
            }
            composable<TaskDetail> { backStackEntry ->
                val detail = backStackEntry.toRoute<TaskDetail>()
                TaskDetailRoute(
                    title = detail.title,
                    onBackClick = { navController.popBackStack() }
                )
            }
        }
    }
}
