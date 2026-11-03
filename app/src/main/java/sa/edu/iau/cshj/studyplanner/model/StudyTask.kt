package sa.edu.iau.cshj.studyplanner.model

data class StudyTask(
    val id: Int,
    val title: String,
    val course: String,
    val isDone: Boolean = false,
    val description: String = ""
)

internal fun sampleStudyTasks(): List<StudyTask> = listOf(
    StudyTask(1, "Review Kotlin Basics", "Mobile Application Programming"),
    StudyTask(2, "Prepare HCI Assignment", "Human Computer Interaction"),
    StudyTask(3, "Study Image Processing", "Image Processing"),
    StudyTask(4, "Practice Compose layouts", "Mobile Application Programming"),
    StudyTask(5, "Review HCI lecture notes", "Human Computer Interaction")
)
