package com.example.seriepractica

import android.content.Context
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.seriepractica.data.TaskRepository
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class TaskRepositoryTest {
    private val context = InstrumentationRegistry.getInstrumentation().targetContext
    private val preferences = context.getSharedPreferences("repository_test", Context.MODE_PRIVATE)
    private lateinit var repository: TaskRepository

    @Before fun setup() {
        preferences.edit().clear().commit()
        repository = TaskRepository(preferences)
    }

    @After fun cleanup() { preferences.edit().clear().commit() }

    @Test fun createCompleteReopenAndDelete() {
        val task = repository.add("  Revisar inventario  ", "  Contar las cajas del almacén.  ")
        assertEquals("Revisar inventario", task.title)
        assertEquals("Contar las cajas del almacén.", task.description)
        assertFalse(task.completed)
        assertTrue(task.createdAt > 0)
        repository.setCompleted(task.id, true)

        val reopened = TaskRepository(preferences)
        assertTrue(reopened.find(task.id)!!.completed)
        assertEquals(task.createdAt, reopened.find(task.id)!!.createdAt)
        reopened.setCompleted(task.id, false)
        assertFalse(repository.find(task.id)!!.completed)
        reopened.delete(task.id)
        assertTrue(repository.all().isEmpty())
    }

    @Test fun identicalTitlesKeepIndependentIdsAndStates() {
        val first = repository.add("Enviar reporte", "Reporte de ventas.")
        val second = repository.add("Enviar reporte", "Reporte de compras.")
        assertNotEquals(first.id, second.id)
        repository.setCompleted(first.id, true)
        repository.delete(second.id)
        assertEquals(listOf(first.copy(completed = true)), repository.all())
    }

    @Test(expected = IllegalArgumentException::class)
    fun rejectWhitespaceOnlyTitle() { repository.add("   ", "Descripción") }

    @Test(expected = IllegalArgumentException::class)
    fun rejectDescriptionOverLimit() { repository.add("Título", "a".repeat(501)) }
}
