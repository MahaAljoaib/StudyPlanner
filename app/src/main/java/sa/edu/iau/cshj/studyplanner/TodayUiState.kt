package sa.edu.iau.cshj.studyplanner

import sa.edu.iau.cshj.studyplanner.model.StudyTask

data class TodayUiState(
    val isLoading: Boolean = true,
    val tasks: List<StudyTask> = emptyList(),
    val message: String? = null
)
