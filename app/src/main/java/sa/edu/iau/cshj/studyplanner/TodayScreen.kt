package sa.edu.iau.cshj.studyplanner

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.launch
import sa.edu.iau.cshj.studyplanner.model.StudyTask
import sa.edu.iau.cshj.studyplanner.ui.theme.StudyPlannerTheme

@Composable
fun TodayRoute(
    viewModel: TodayViewModel = viewModel(),
    onBackClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    TodayScreen(
        uiState = uiState,
        onTaskCheckedChange = viewModel::onTaskCheckedChange,
        onMessageShown = viewModel::onMessageShown,
        onBackClick = onBackClick,
        modifier = modifier
    )
}

@Composable
fun TaskRow(
    task: StudyTask,
    onCheckedChange: (StudyTask, Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    ElevatedCard(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Checkbox(
                checked = task.isDone,
                onCheckedChange = { done -> onCheckedChange(task, done) },
                modifier = Modifier.semantics {
                    contentDescription = "Mark ${task.title} done"
                }
            )
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    task.title,
                    style = MaterialTheme.typography.titleMedium,
                    textDecoration = if (task.isDone) TextDecoration.LineThrough else null
                )
                Text(
                    task.course,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun TodayScreen(
    uiState: TodayUiState,
    onTaskCheckedChange: (StudyTask, Boolean) -> Unit,
    onMessageShown: () -> Unit,
    onBackClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    LaunchedEffect(uiState.message) {
        val message = uiState.message
        if (message != null) {
            snackbarHostState.showSnackbar(message)
            onMessageShown()
        }
    }

    Scaffold(
        modifier = modifier,
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(innerPadding).padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Text(
                "STUDY PLANNER",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    "Today",
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
                TextButton(onClick = {
                    scope.launch {
                        snackbarHostState.showSnackbar("Tick a task when you finish it")
                    }
                }) { Text("Help") }
                TextButton(onClick = onBackClick) { Text("Back") }
            }
            if (uiState.isLoading) {
                Box(modifier = Modifier.fillMaxWidth().weight(1f), contentAlignment = Alignment.Center) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        CircularProgressIndicator(modifier = Modifier.testTag("today-loading"))
                        Text("Loading today's tasks...", style = MaterialTheme.typography.bodyMedium)
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f).testTag("today-tasks"),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(uiState.tasks, key = { it.id }) { task ->
                        TaskRow(task, onTaskCheckedChange)
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, name = "Today - Loading", widthDp = 390, heightDp = 844)
@Composable
internal fun TodayLoadingPreview() {
    StudyPlannerTheme {
        TodayScreen(TodayUiState(), onTaskCheckedChange = { _, _ -> }, onMessageShown = {})
    }
}

@Preview(showBackground = true, name = "Today - Loaded", widthDp = 390, heightDp = 844)
@Composable
internal fun TodayLoadedPreview() {
    StudyPlannerTheme {
        TodayScreen(
            TodayUiState(isLoading = false, tasks = listOf(
                StudyTask(1, "Read Chapter 11", "Mobile Apps", isDone = true),
                StudyTask(2, "Lab 8 report", "Mobile Apps"),
                StudyTask(3, "Revise SQL joins", "Databases")
            )),
            onTaskCheckedChange = { _, _ -> },
            onMessageShown = {}
        )
    }
}
