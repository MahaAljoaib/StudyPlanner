package sa.edu.iau.cshj.studyplanner

import kotlinx.coroutines.delay
import sa.edu.iau.cshj.studyplanner.model.StudyTask

class FakeTaskRepository {
    suspend fun loadTodayTasks(): List<StudyTask> {
        delay(1500L)
        return listOf(
            StudyTask(1, "Read Chapter 11", "Mobile Apps"),
            StudyTask(2, "Lab 8 report", "Mobile Apps"),
            StudyTask(3, "Revise SQL joins", "Databases")
        )
    }

    @Suppress("UNUSED_PARAMETER")
    suspend fun setDone(id: Int, done: Boolean) {
        // This lecture simulates a save; the ViewModel owns the updated list.
        delay(300L)
    }
}
