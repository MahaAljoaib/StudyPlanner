package sa.edu.iau.cshj.studyplanner

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.foundation.layout.Row
import androidx.compose.ui.Alignment
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.platform.testTag
import sa.edu.iau.cshj.studyplanner.model.StudyTask
import sa.edu.iau.cshj.studyplanner.model.sampleStudyTasks
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

import androidx.compose.foundation.background
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

data class PlannerUiState(
    val greeting: String = "Hello, Maha!",
    val tasks: List<StudyTask> = emptyList()
)

@Composable
fun PlannerHomeScreen(
    uiState: PlannerUiState,
    onTaskClick: (Int) -> Unit,
    onDoneChange: (Int, Boolean) -> Unit,
    onTodayClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val totalTasks = uiState.tasks.size
    val completedTasks = uiState.tasks.count { it.isDone }
    val progress = if (totalTasks > 0) {
        (completedTasks.toFloat() / totalTasks)
            .coerceIn(0f, 1f)
    } else {
        0f
    }

    val allDone = totalTasks > 0 && completedTasks == totalTasks

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("task-list"),
        contentPadding = PaddingValues(24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item(key = "header") {
            Column(verticalArrangement = Arrangement.spacedBy(24.dp)) {
                Spacer(modifier = Modifier.height(12.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "STUDY PLANNER",
                        modifier = Modifier.weight(1f),
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                    TextButton(onClick = onTodayClick) { Text("Today") }
                }

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
                            text = "$completedTasks of $totalTasks done",
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

                Text(
                    text = "Your Study Tasks",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

            }
        }
        items(uiState.tasks, key = { it.id }) { task ->
            TaskItem(task, onTaskClick, onDoneChange)
        }
    }
}

@Preview(showBackground = true, name = "Home — five tasks", widthDp = 390, heightDp = 844)
@Composable
internal fun PlannerHomePreviewFiveTasks() {
    StudyPlannerTheme {
        PlannerHomeScreen(
            uiState = PlannerUiState(tasks = sampleStudyTasks()),
            onTaskClick = {},
            onDoneChange = { _, _ -> }
        )
    }
}

// Store every task field, so state survives rotation as well as navigation.
internal val PlannerUiStateSaver = listSaver<PlannerUiState, Any>(
    save = { state ->
        listOf(state.greeting) + state.tasks.flatMap {
            listOf(it.id, it.title, it.course, it.isDone, it.description)
        }
    },
    restore = { saved ->
        PlannerUiState(
            greeting = saved[0] as String,
            tasks = saved.drop(1).chunked(5).map {
                StudyTask(it[0] as Int, it[1] as String, it[2] as String,
                    it[3] as Boolean, it[4] as String)
            }
        )
    }
)

@Composable
fun PlannerHomeRoute(
    onTaskClick: (Int) -> Unit,
    onTodayClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var uiState by rememberSaveable(stateSaver = PlannerUiStateSaver) {
        mutableStateOf(PlannerUiState(tasks = sampleStudyTasks()))
    }
    PlannerHomeScreen(
        uiState = uiState,
        onTaskClick = onTaskClick,
        onDoneChange = { id, checked ->
            uiState = uiState.copy(tasks = uiState.tasks.map { task ->
                if (task.id == id) task.copy(isDone = checked) else task
            })
        },
        onTodayClick = onTodayClick,
        modifier = modifier
    )
}
