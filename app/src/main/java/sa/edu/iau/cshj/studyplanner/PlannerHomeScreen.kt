package sa.edu.iau.cshj.studyplanner

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

import androidx.compose.foundation.background
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight


import androidx.compose.ui.tooling.preview.Preview
import sa.edu.iau.cshj.studyplanner.ui.theme.StudyPlannerTheme


import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember



data class PlannerUiState(
    val greeting: String = "Hello, Maha!",
    val totalTasks: Int = 7,
    val completedTasks: Int = 3,
    val tasks: List<String> = emptyList()
)



@Composable
fun PlannerHomeScreen(
    uiState: PlannerUiState,
    onCompleteTask: () -> Unit,
    onTaskClick: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val progress = if (uiState.totalTasks > 0) {
        (uiState.completedTasks.toFloat() / uiState.totalTasks)
            .coerceIn(0f, 1f)
    } else {
        0f
    }

    val allDone = uiState.totalTasks > 0 &&
            uiState.completedTasks >= uiState.totalTasks

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "STUDY PLANNER",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary
        )

        Text(
            text = uiState.greeting,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = "A little progress every day.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        ElevatedCard(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.elevatedCardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Your study progress",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )

                Text(
                    text = "${uiState.completedTasks} of ${uiState.totalTasks} done",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold
                )

                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(10.dp),
                    color = MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.colorScheme.surface,
                    strokeCap = StrokeCap.Round
                )

                Text(
                    text = if (allDone)
                        "Amazing! All tasks completed."
                    else
                        "Keep going! You've got this.",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        ElevatedCard(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "One step at a time",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold
                )

                Text(
                    text = "Make today a productive study day.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Button(
                    onClick = onCompleteTask,
                    enabled = uiState.completedTasks < uiState.totalTasks,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(
                        text = if (allDone)
                            "All Tasks Completed"
                        else
                            "Complete a Task"
                    )
                }
            }
        }

        Text(
            text = "Your Study Tasks",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        uiState.tasks.forEach { title ->
            Button(
                onClick = { onTaskClick(title) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(
                    text = title,
                    modifier = Modifier.padding(vertical = 8.dp),
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }
    }
}


@Preview(showBackground = true, name = "Nothing Done")
@Composable
private fun PlannerHomePreviewNothingDone() {
    StudyPlannerTheme {
        PlannerHomeScreen(
            uiState = PlannerUiState(
                greeting = "Hello, Maha!",
                totalTasks = 7,
                completedTasks = 0,
                tasks = listOf("Review Kotlin Basics", "Prepare HCI Assignment", "Study Image Processing")
            ),
            onCompleteTask = {}
        )
    }
}

@Preview(showBackground = true, name = "All Done")
@Composable
private fun PlannerHomePreviewAllDone() {
    StudyPlannerTheme {
        PlannerHomeScreen(
            uiState = PlannerUiState(
                greeting = "Hello, Maha!",
                totalTasks = 7,
                completedTasks = 7,
                tasks = listOf("Review Kotlin Basics", "Prepare HCI Assignment", "Study Image Processing")
            ),
            onCompleteTask = {}
        )
    }
}

@Composable
internal fun rememberPlannerUiState(): MutableState<PlannerUiState> = remember {
    mutableStateOf(
        PlannerUiState(
            tasks = listOf(
                "Review Kotlin Basics",
                "Prepare HCI Assignment",
                "Study Image Processing"
            )
        )
    )
}

@Composable
fun PlannerHomeRoute(
    onTaskClick: (String) -> Unit = {},
    plannerState: MutableState<PlannerUiState> = rememberPlannerUiState()
) {
    var uiState by plannerState

    PlannerHomeScreen(
        uiState = uiState,
        onCompleteTask = {
            if (uiState.completedTasks < uiState.totalTasks) {
                uiState = uiState.copy(
                    completedTasks = uiState.completedTasks + 1
                )
            }
        },
        onTaskClick = onTaskClick
    )
}
