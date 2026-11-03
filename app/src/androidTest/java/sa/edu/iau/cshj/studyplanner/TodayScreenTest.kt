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
class TodayScreenTest {
    @get:Rule val compose = createComposeRule()

    @Test fun loadingPreviewRendersAndHelpRemainsResponsive() {
        compose.setContent { TodayLoadingPreview() }
        compose.onNodeWithTag("today-loading").assertIsDisplayed()
        saveRoot("01-loading.png")
        compose.onNodeWithText("Help").performClick()
        compose.waitUntil(5_000) { compose.onAllNodesWithText("Tick a task when you finish it").fetchSemanticsNodes().isNotEmpty() }
        compose.onNodeWithText("Tick a task when you finish it").assertIsDisplayed()
        compose.onNodeWithTag("today-loading").assertIsDisplayed()
        saveRoot("02-help-during-loading.png")
    }

    @Test fun loadedPreviewRendersAllThreeTasks() {
        compose.setContent { TodayLoadedPreview() }
        compose.onNodeWithText("Read Chapter 11").assertIsDisplayed()
        compose.onNodeWithText("Lab 8 report").assertIsDisplayed()
        compose.onNodeWithText("Revise SQL joins").assertIsDisplayed()
        compose.onNodeWithContentDescription("Mark Read Chapter 11 done").assertIsOn()
        saveRoot("03-loaded-preview.png")
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
