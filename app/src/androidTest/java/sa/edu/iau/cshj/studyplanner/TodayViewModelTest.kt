package sa.edu.iau.cshj.studyplanner

import android.os.SystemClock
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class TodayViewModelTest {
    @Test fun loadSaveAndMessageFollowTheGuideWithoutMutatingOldState() = runBlocking {
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        val store = ViewModelStore()
        lateinit var model: TodayViewModel
        val start = SystemClock.elapsedRealtime()
        instrumentation.runOnMainSync {
            model = ViewModelProvider(store, ViewModelProvider.NewInstanceFactory())[TodayViewModel::class.java]
        }
        try {
            assertTrue(model.uiState.value.isLoading)
            assertTrue(model.uiState.value.tasks.isEmpty())
            val loaded = withTimeout(10_000) { model.uiState.first { !it.isLoading } }
            assertTrue(SystemClock.elapsedRealtime() - start >= 1400)
            assertEquals(listOf("Read Chapter 11", "Lab 8 report", "Revise SQL joins"), loaded.tasks.map { it.title })
            assertTrue(loaded.tasks.none { it.isDone })
            val task = loaded.tasks.first()
            val saveStart = SystemClock.elapsedRealtime()
            instrumentation.runOnMainSync { model.onTaskCheckedChange(task, true) }
            val done = withTimeout(5_000) { model.uiState.first { it.tasks.first().isDone } }
            assertTrue(SystemClock.elapsedRealtime() - saveStart >= 250)
            assertNotSame(loaded.tasks, done.tasks)
            assertFalse(loaded.tasks.first().isDone)
            assertEquals(1, done.tasks.count { it.isDone })
            assertEquals("Marked as done", done.message)
            instrumentation.runOnMainSync { model.onMessageShown() }
            assertNull(model.uiState.value.message)
            instrumentation.runOnMainSync { model.onTaskCheckedChange(done.tasks.first(), false) }
            val undone = withTimeout(5_000) { model.uiState.first { !it.tasks.first().isDone } }
            assertNull(undone.message)
            assertTrue(done.tasks.first().isDone)
        } finally {
            instrumentation.runOnMainSync { store.clear() }
        }
    }
}
