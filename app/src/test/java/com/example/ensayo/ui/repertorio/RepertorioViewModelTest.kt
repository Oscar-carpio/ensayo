package com.example.ensayo.ui.repertorio

import com.example.ensayo.domain.model.Song
import com.example.ensayo.domain.repository.SongRepository
import com.example.ensayo.testing.FakeSongRepository
import com.example.ensayo.testing.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class RepertorioViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val repository = FakeSongRepository()
    private lateinit var viewModel: RepertorioViewModel

    private val amazing = Song(
        title = "Amazing Grace",
        artist = "John Newton",
        musicalKey = "G",
        content = ""
    )
    private val holy = Song(title = "Holy Holy Holy", musicalKey = "D", content = "")

    @Before
    fun setUp() {
        viewModel = RepertorioViewModel(repository)
    }

    // stateIn(WhileSubscribed) solo trabaja si alguien escucha: simulamos la pantalla
    private fun TestScope.startCollecting() {
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect {}
        }
    }

    private fun successState() = viewModel.uiState.value as RepertorioUiState.Success

    @Test
    fun `el estado inicial es Loading`() {
        assertEquals(RepertorioUiState.Loading, viewModel.uiState.value)
    }

    @Test
    fun `sin canciones muestra Empty`() = runTest {
        startCollecting()
        assertEquals(RepertorioUiState.Empty, viewModel.uiState.value)
    }

    @Test
    fun `con canciones muestra Success y los tonos disponibles`() = runTest {
        repository.emit(listOf(amazing, holy))
        startCollecting()
        assertEquals(listOf(amazing, holy), successState().songs)
        assertEquals(listOf("D", "G"), successState().availableKeys)
    }

    @Test
    fun `al buscar se filtra la lista`() = runTest {
        repository.emit(listOf(amazing, holy))
        startCollecting()
        viewModel.onQueryChange("holy")
        assertEquals(listOf(holy), successState().songs)
        assertEquals("holy", successState().query)
    }

    @Test
    fun `seleccionar el mismo tono dos veces lo desactiva`() = runTest {
        repository.emit(listOf(amazing, holy))
        startCollecting()
        viewModel.onKeySelected("G")
        assertEquals("G", successState().selectedKey)
        viewModel.onKeySelected("G")
        assertNull(successState().selectedKey)
    }

    @Test
    fun `reacciona cuando el repositorio cambia`() = runTest {
        startCollecting()
        assertEquals(RepertorioUiState.Empty, viewModel.uiState.value)
        repository.save(amazing)
        assertEquals(listOf(amazing), successState().songs)
    }

    @Test
    fun `si el repositorio falla muestra Error`() = runTest {
        val failing = object : SongRepository by FakeSongRepository() {
            override fun observeSongs(): Flow<List<Song>> = flow {
                throw IllegalStateException("Disco lleno")
            }
        }
        viewModel = RepertorioViewModel(failing)
        startCollecting()
        assertEquals(RepertorioUiState.Error("Disco lleno"), viewModel.uiState.value)
    }
}
