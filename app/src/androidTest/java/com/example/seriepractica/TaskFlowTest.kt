package com.example.seriepractica

import android.content.Context
import android.graphics.Bitmap
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.seriepractica.data.TaskRepository
import org.hamcrest.Matchers.not
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File

@RunWith(AndroidJUnit4::class)
class TaskFlowTest {
    private val instrumentation = InstrumentationRegistry.getInstrumentation()
    private val context = instrumentation.targetContext
    private val title = "Revisar inventario"
    private val description = "Contar las cajas del almacén y registrar las existencias."
    private val screenshots = InstrumentationRegistry.getArguments().getString("screenshots") == "true"

    @Before fun setup() {
        context.getSharedPreferences(TaskRepository.PREFERENCES_NAME, Context.MODE_PRIVATE)
            .edit().clear().commit()
    }

    @After fun cleanup() {
        context.getSharedPreferences(TaskRepository.PREFERENCES_NAME, Context.MODE_PRIVATE)
            .edit().clear().commit()
    }

    @Test fun completeOfflineWorkflow() {
        var scenario = ActivityScenario.launch(MainActivity::class.java)
        try {
            onView(withId(R.id.emptyTitle)).check(matches(withText(R.string.empty_title)))
            capture("01-inicio")

            onView(withId(R.id.addTask)).perform(click())
            onView(withId(R.id.saveTask)).perform(click())
            onView(withId(R.id.titleInput)).check(matches(hasErrorText(context.getString(R.string.title_required))))
            onView(withId(R.id.titleInput)).perform(replaceText(title))
            onView(withId(R.id.descriptionInput)).perform(replaceText(description), closeSoftKeyboard())
            capture("02-formulario")
            onView(withId(R.id.saveTask)).perform(click())
            onView(withId(R.id.taskTitle)).check(matches(withText(title)))
            onView(withId(R.id.taskStatus)).check(matches(withText(R.string.task_pending)))
            capture("03-lista")

            onView(withId(R.id.taskTitle)).perform(click())
            onView(withId(R.id.taskDescription)).check(matches(withText(description)))
            onView(withId(R.id.taskDate)).check(matches(not(withText(""))))
            capture("04-detalle")
            onView(withId(R.id.completeTask)).perform(click())
            onView(withId(R.id.taskStatus)).check(matches(withText(R.string.task_completed)))
            onView(withId(R.id.completeTask)).check(matches(withText(R.string.reopen_task)))
            capture("05-completada")
            onView(withId(R.id.returnToList)).perform(click())
            onView(withId(R.id.filterCompleted)).perform(click())
            onView(withId(R.id.taskCheck)).check(matches(isChecked()))
            capture("06-filtro")

            // A new Activity and repository must read the persisted status.
            scenario.close()
            scenario = ActivityScenario.launch(MainActivity::class.java)
            onView(withId(R.id.taskCheck)).check(matches(isChecked()))
            assertTrue(TaskRepository(context).all().single().completed)
            onView(withId(R.id.taskCheck)).perform(click())
            onView(withId(R.id.taskCheck)).check(matches(not(isChecked())))
            onView(withId(R.id.filterCompleted)).perform(click())
            onView(withId(R.id.emptyTitle)).check(matches(withText(R.string.empty_completed)))
            onView(withId(R.id.filterAll)).perform(click())

            onView(withId(R.id.deleteTask)).perform(click())
            onView(withText(R.string.cancel)).perform(click())
            assertEquals(1, TaskRepository(context).all().size)
            onView(withId(R.id.deleteTask)).perform(click())
            capture("07-eliminar")
            onView(withText(R.string.delete)).perform(click())
            onView(withId(R.id.emptyTitle)).check(matches(withText(R.string.empty_title)))
            assertTrue(TaskRepository(context).all().isEmpty())
            capture("08-final")
        } finally {
            scenario.close()
        }
    }

    @Test fun draftSurvivesRecreationAndExitCanBeCancelled() {
        ActivityScenario.launch(AddTaskActivity::class.java).use { scenario ->
            onView(withId(R.id.titleInput)).perform(replaceText("Preparar reunión"))
            onView(withId(R.id.descriptionInput)).perform(replaceText("Agenda del día.\nRevisar objetivos."), closeSoftKeyboard())
            scenario.recreate()
            onView(withId(R.id.titleInput)).check(matches(withText("Preparar reunión")))
            onView(withId(R.id.descriptionInput)).check(matches(withText("Agenda del día.\nRevisar objetivos.")))
            onView(withId(R.id.backButton)).perform(click())
            onView(withText(R.string.continue_editing)).perform(click())
            onView(withId(R.id.titleInput)).check(matches(withText("Preparar reunión")))
            onView(withId(R.id.backButton)).perform(click())
            onView(withText(R.string.discard)).perform(click())
            assertTrue(TaskRepository(context).all().isEmpty())
        }
    }

    @Test fun newTaskIsVisibleEvenWhenCompletedFilterWasSelected() {
        ActivityScenario.launch(MainActivity::class.java).use {
            onView(withId(R.id.filterCompleted)).perform(click())
            onView(withId(R.id.addTask)).perform(click())
            onView(withId(R.id.titleInput)).perform(replaceText(title))
            onView(withId(R.id.descriptionInput)).perform(replaceText(description), closeSoftKeyboard())
            onView(withId(R.id.saveTask)).perform(click())
            onView(withId(R.id.taskTitle)).check(matches(withText(title)))
            onView(withId(R.id.filterAll)).check(matches(isSelected()))
            onView(withId(R.id.taskTitle)).perform(click())
            onView(withId(R.id.deleteTask)).perform(click())
            onView(withText(R.string.delete)).perform(click())
            onView(withId(R.id.emptyTitle)).check(matches(withText(R.string.empty_title)))
        }
    }

    private fun capture(name: String) {
        if (!screenshots) return
        instrumentation.waitForIdleSync()
        val bitmap = instrumentation.uiAutomation.takeScreenshot()
        val directory = context.getExternalFilesDir("demo")!!
        File(directory, "$name.png").outputStream().use {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, it)
        }
        bitmap.recycle()
    }
}

