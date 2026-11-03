package sa.edu.iau.cshj.studyplanner

import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalContext
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import android.content.ContentValues
import android.graphics.Bitmap
import android.os.Build
import android.provider.MediaStore
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.junit4.StateRestorationTester
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File
import sa.edu.iau.cshj.studyplanner.navigation.StudyPlannerApp
import sa.edu.iau.cshj.studyplanner.ui.theme.StudyPlannerTheme

@RunWith(AndroidJUnit4::class)
class StudyPlannerLab7Test {
    @get:Rule val compose = createComposeRule()

    private fun scrollTo(title: String) {
        compose.onNodeWithTag("task-list").performScrollToNode(hasText(title))
    }
    private fun progress(text: String) {
        compose.onNodeWithTag("task-list").performScrollToIndex(0)
        compose.onNodeWithText(text).assertIsDisplayed()
    }
    private fun toggle(title: String) {
        scrollTo(title)
        compose.onNodeWithContentDescription("Mark $title done").performClick()
    }

    @Test fun completionNavigationAndEvidence() {
        compose.setContent { StudyPlannerTheme { StudyPlannerApp() } }
        progress("0 of 5 done")
        toggle("Review Kotlin Basics")
        toggle("Prepare HCI Assignment")
        progress("2 of 5 done")
        // A checkbox changes completion without opening the detail destination.
        compose.onNodeWithText("Task details").assertDoesNotExist()
        compose.onNodeWithTag("task-list").performScrollToIndex(0)
        saveRoot("04-running-two-done.png")
        listOf("Review Kotlin Basics", "Prepare HCI Assignment", "Study Image Processing",
            "Practice Compose layouts", "Review HCI lecture notes").forEach { title ->
            scrollTo(title)
            compose.onNodeWithText(title).performClick()
            compose.onNodeWithText("Task details").assertIsDisplayed()
            compose.onNodeWithText(title).assertIsDisplayed()
            if (title == "Review Kotlin Basics") saveRoot("05-detail-from-row.png")
            compose.onNodeWithText("Back").performClick()
            progress("2 of 5 done")
        }
        toggle("Review Kotlin Basics")
        progress("1 of 5 done")
        listOf("Review Kotlin Basics", "Study Image Processing", "Practice Compose layouts",
            "Review HCI lecture notes").forEach { toggle(it) }
        progress("5 of 5 done")
        compose.onNodeWithText("Amazing! All tasks completed.").assertIsDisplayed()
    }

    @Test fun completionSurvivesSavedStateRestoration() {
        val restoration = StateRestorationTester(compose)
        restoration.setContent { StudyPlannerTheme { StudyPlannerApp() } }
        toggle("Review Kotlin Basics")
        toggle("Prepare HCI Assignment")
        restoration.emulateSavedInstanceStateRestore()
        progress("2 of 5 done")
        scrollTo("Review Kotlin Basics")
        compose.onNodeWithContentDescription("Mark Review Kotlin Basics done").assertIsOn()
        compose.onNodeWithText("Review Kotlin Basics").performClick()
        restoration.emulateSavedInstanceStateRestore()
        compose.onNodeWithText("Task details").assertIsDisplayed()
        compose.onNodeWithText("Back").performClick()
        progress("2 of 5 done")
    }

    @Test fun previewNotDoneRenders() {
        compose.setContent { TaskItemPreviewNotDone() }
        compose.onNodeWithText("Review Kotlin Basics").assertIsDisplayed()
        compose.onNodeWithContentDescription("Mark Review Kotlin Basics done").assertIsOff()
        saveRoot("01-preview-task-not-done.png")
    }

    @Test fun previewDoneRenders() {
        compose.setContent { TaskItemPreviewDone() }
        compose.onNodeWithText("Review Kotlin Basics").assertIsDisplayed()
        compose.onNodeWithContentDescription("Mark Review Kotlin Basics done").assertIsOn()
        saveRoot("02-preview-task-done.png")
    }

    @Test fun previewHomeRendersFiveTasks() {
        compose.setContent { PlannerHomePreviewFiveTasks() }
        progress("0 of 5 done")
        saveRoot("03-preview-home-five-tasks.png")
        scrollTo("Review HCI lecture notes")
        compose.onNodeWithText("Review HCI lecture notes").assertIsDisplayed()
    }

    @Test fun emptyListHasZeroProgress() {
        compose.setContent {
            StudyPlannerTheme {
                PlannerHomeScreen(PlannerUiState(), onTaskClick = {}, onDoneChange = { _, _ -> })
            }
        }
        progress("0 of 0 done")
    }

    private fun saveRoot(name: String) {
        compose.waitForIdle()
        saveBitmap(compose.onRoot().captureToImage().asAndroidBitmap(), name)
    }

    private fun saveBitmap(bitmap: Bitmap, name: String) {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val directory = File(context.getExternalFilesDir(null), "lab7-evidence")
        check(directory.mkdirs() || directory.isDirectory)
        File(directory, name).outputStream().use {
            check(bitmap.compress(Bitmap.CompressFormat.PNG, 100, it))
        }
        // Public evidence survives the test runner uninstalling the test app.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val values = ContentValues().apply {
                put(MediaStore.Images.Media.DISPLAY_NAME, name)
                put(MediaStore.Images.Media.MIME_TYPE, "image/png")
                put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/StudyPlanner-Lab7")
                put(MediaStore.Images.Media.IS_PENDING, 1)
            }
            val resolver = context.contentResolver
            val uri = requireNotNull(resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values))
            requireNotNull(resolver.openOutputStream(uri)).use {
                check(bitmap.compress(Bitmap.CompressFormat.PNG, 100, it))
            }
            values.clear()
            values.put(MediaStore.Images.Media.IS_PENDING, 0)
            resolver.update(uri, values, null, null)
        }
    }

    @Test
    fun shareBuildsTextChooserOnlyWhenTapped() {
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        val recordingContext = RecordingContext(instrumentation.targetContext)
        val title = "Lab 5 / notes? Kotlin & Compose #1"
        compose.setContent {
            CompositionLocalProvider(LocalContext provides recordingContext) {
                StudyPlannerTheme {
                    TaskDetailRoute(title = title, onBackClick = {})
                }
            }
        }
        assertNull(recordingContext.startedIntent)
        compose.onNodeWithText("Share").performClick()
        compose.runOnIdle {
            val chooser = requireNotNull(recordingContext.startedIntent)
            assertEquals(Intent.ACTION_CHOOSER, chooser.action)
            @Suppress("DEPRECATION")
            val send = requireNotNull(chooser.getParcelableExtra<Intent>(Intent.EXTRA_INTENT))
            assertEquals(Intent.ACTION_SEND, send.action)
            assertEquals("text/plain", send.type)
            assertEquals("Today I'm studying: $title", send.getStringExtra(Intent.EXTRA_TEXT))
            assertEquals(1, recordingContext.launchCount)
        }
    }

    private class RecordingContext(base: Context) : ContextWrapper(base) {
        var startedIntent: Intent? = null
        var launchCount: Int = 0

        override fun startActivity(intent: Intent) {
            startedIntent = intent
            launchCount++
        }
    }
}

@RunWith(AndroidJUnit4::class)
class StudyPlannerRotationTest {
    @get:Rule val compose = androidx.compose.ui.test.junit4.createAndroidComposeRule<MainActivity>()

    @Test fun rotationAndActivityRecreationPreserveTasksAndDestination() {
        fun top() { compose.onNodeWithTag("task-list").performScrollToIndex(0) }
        fun firstTask() {
            compose.onNodeWithTag("task-list").performScrollToNode(hasText("Review Kotlin Basics"))
        }
        firstTask()
        compose.onNodeWithContentDescription("Mark Review Kotlin Basics done").performClick()
        compose.runOnUiThread {
            compose.activity.requestedOrientation = android.content.pm.ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        }
        compose.waitUntil(10_000) {
            compose.activity.resources.configuration.orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE
        }
        firstTask()
        compose.onNodeWithContentDescription("Mark Review Kotlin Basics done").assertIsOn()
        compose.runOnUiThread {
            compose.activity.requestedOrientation = android.content.pm.ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        }
        compose.waitUntil(10_000) {
            compose.activity.resources.configuration.orientation == android.content.res.Configuration.ORIENTATION_PORTRAIT
        }
        top()
        compose.onNodeWithText("1 of 5 done").assertIsDisplayed()
        firstTask()
        compose.onNodeWithText("Review Kotlin Basics").performClick()
        compose.activityRule.scenario.recreate()
        compose.onNodeWithText("Task details").assertIsDisplayed()
        androidx.test.espresso.Espresso.pressBack()
        top()
        compose.onNodeWithText("1 of 5 done").assertIsDisplayed()
        firstTask()
        compose.onNodeWithContentDescription("Mark Review Kotlin Basics done").assertIsOn()
    }
}
