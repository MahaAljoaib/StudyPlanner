package sa.edu.iau.cshj.studyplanner

import android.content.ContentValues
import android.graphics.Bitmap
import android.os.Build
import android.provider.MediaStore
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File

@RunWith(AndroidJUnit4::class)
class TodayIntegrationTest {
    @get:Rule val compose = createAndroidComposeRule<MainActivity>()

    private fun waitForText(text: String) {
        compose.waitUntil(10_000) { compose.onAllNodesWithText(text).fetchSemanticsNodes().isNotEmpty() }
    }
    private fun waitForNoText(text: String) {
        compose.waitUntil(12_000) { compose.onAllNodesWithText(text).fetchSemanticsNodes().isEmpty() }
    }

    @Test fun loadTickSnackbarHelpRotationAndBackPreserveExistingFeatures() {
        compose.onNodeWithTag("task-list").performScrollToNode(hasText("Review Kotlin Basics"))
        compose.onNodeWithContentDescription("Mark Review Kotlin Basics done").performClick()
        compose.onNodeWithTag("task-list").performScrollToIndex(0)
        compose.onNodeWithText("1 of 5 done").assertIsDisplayed()
        compose.onNodeWithText("Today").performClick()
        compose.onNodeWithTag("today-loading").assertIsDisplayed()
        waitForText("Read Chapter 11")
        compose.onNodeWithTag("today-loading").assertDoesNotExist()
        compose.onNodeWithText("Revise SQL joins").assertIsDisplayed()
        saveRoot("04-running-loaded.png")
        compose.onNodeWithContentDescription("Mark Read Chapter 11 done").performClick()
        waitForText("Marked as done")
        compose.onNodeWithContentDescription("Mark Read Chapter 11 done").assertIsOn()
        saveRoot("05-marked-as-done.png")
        waitForNoText("Marked as done")
        // The acknowledged message must not reappear after Activity recreation.
        compose.runOnUiThread {
            compose.activity.requestedOrientation = android.content.pm.ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        }
        compose.waitUntil(10_000) {
            compose.activity.resources.configuration.orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE
        }
        compose.onNodeWithTag("today-loading").assertDoesNotExist()
        compose.onNodeWithContentDescription("Mark Read Chapter 11 done").assertIsOn()
        compose.onNodeWithText("Marked as done").assertDoesNotExist()
        saveRoot("06-landscape-after-rotation.png")
        compose.runOnUiThread {
            compose.activity.requestedOrientation = android.content.pm.ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        }
        compose.waitUntil(10_000) {
            compose.activity.resources.configuration.orientation == android.content.res.Configuration.ORIENTATION_PORTRAIT
        }
        compose.onNodeWithTag("today-loading").assertDoesNotExist()
        compose.onNodeWithContentDescription("Mark Read Chapter 11 done").assertIsOn()
        compose.activityRule.scenario.recreate()
        compose.onNodeWithContentDescription("Mark Read Chapter 11 done").assertIsOn()
        compose.onNodeWithTag("today-loading").assertDoesNotExist()
        compose.onNodeWithText("Marked as done").assertDoesNotExist()
        compose.onNodeWithText("Help").performClick()
        waitForText("Tick a task when you finish it")
        saveRoot("07-help.png")
        compose.onNodeWithText("Back").performClick()
        compose.onNodeWithText("1 of 5 done").assertIsDisplayed()
        compose.onNodeWithTag("task-list").performScrollToNode(hasText("Review Kotlin Basics"))
        compose.onNodeWithContentDescription("Mark Review Kotlin Basics done").assertIsOn()
        compose.onNodeWithText("Review Kotlin Basics").performClick()
        compose.onNodeWithText("Task details").assertIsDisplayed()
        compose.onNodeWithText("Share").assertIsDisplayed()
        compose.onNodeWithText("Back").performClick()
        compose.onNodeWithTag("task-list").performScrollToIndex(0)
        compose.onNodeWithText("1 of 5 done").assertIsDisplayed()
        saveRoot("08-lab7-preserved.png")
    }
    private fun saveRoot(name: String) {
        compose.waitForIdle()
        saveBitmap(compose.onRoot().captureToImage().asAndroidBitmap(), name)
    }

    private fun saveBitmap(bitmap: Bitmap, name: String) {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val directory = File(context.getExternalFilesDir(null), "lecture8-evidence")
        check(directory.mkdirs() || directory.isDirectory)
        File(directory, name).outputStream().use {
            check(bitmap.compress(Bitmap.CompressFormat.PNG, 100, it))
        }
        // Public evidence survives the test runner uninstalling the test app.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val values = ContentValues().apply {
                put(MediaStore.Images.Media.DISPLAY_NAME, name)
                put(MediaStore.Images.Media.MIME_TYPE, "image/png")
                put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/StudyPlanner-Lecture8")
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

}
