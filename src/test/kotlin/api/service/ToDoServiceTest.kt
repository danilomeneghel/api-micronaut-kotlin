package api.service

import api.model.ToDo
import api.dto.ToDoRequest
import api.dto.ToDoRequestUpdate
import api.repository.ToDoRepository
import io.micronaut.data.model.Page
import io.micronaut.data.model.Pageable
import io.micronaut.test.annotation.MockBean
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import jakarta.inject.Inject
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.mockito.Mockito.*
import java.util.*
import jakarta.persistence.*
import jakarta.validation.*

@MicronautTest
internal class ToDoServiceTest {

    @Inject
    lateinit var toDoService: ToDoService

    @Inject
    lateinit var toDoRepository: ToDoRepository

    @Test
    fun deveSalvarToDo() {
        val toDo = ToDo().apply {
            nome = "ToDo 1"
            descricao = "Descrição 1"
            status = false
        }

        Mockito.`when`(toDoRepository.save(any())).thenReturn(toDo)
        val toDoMock = toDoService.salvar(ToDoRequest("ToDo 1", "Descrição 1", false))

        assertEquals(toDo.nome, toDoMock.nome)
        assertEquals(toDo.descricao, toDoMock.descricao)
    }

    @Test
    fun deveLancarExcecaoAoSalvarToDoSemDescricao() {
        Mockito.`when`(toDoRepository.save(any())).thenThrow(ConstraintViolationException::class.java)
        assertThrows(ConstraintViolationException::class.java){
            toDoService.salvar(ToDoRequest("Nome válido", "", false))
        }
    }

    @Test
    fun deveListarToDos() {
        val toDo1 = ToDo()
        toDo1.descricao = "ToDo 1"
        toDo1.status = false

        val toDo2 = ToDo()
        toDo2.descricao = "ToDo 2"
        toDo2.status = false

        val toDos = arrayListOf(toDo1,toDo2)
        val pageable = Pageable.from(0,10)

        Mockito.`when`(toDoRepository.findAll(any())).thenReturn(Page.of(toDos, pageable, 2))
        val toDosMock = toDoService.listar(null,pageable)

        assertNotNull(toDosMock)
        assertEquals(2, toDosMock!!.content.size)
    }

    @Test
    fun deveListarToDosComFiltro() {
        val toDo1 = ToDo().apply {
            nome = "ToDo 1"
            descricao = "Descrição 1"
            status = true
        }

        val toDos = arrayListOf(toDo1)
        val pageable = Pageable.from(0, 10)

        Mockito.`when`(toDoRepository.findByStatus(true, pageable)).thenReturn(Page.of(toDos, pageable, 1))
        val toDosMock = toDoService.listar(true, pageable)

        assertNotNull(toDosMock)
        assertEquals(1, toDosMock!!.content.size)
        assertTrue(toDosMock.content[0].status)
    }

    @Test
    fun deveBuscarToDo() {
        val toDo = ToDo()
        toDo.descricao = "ToDo 1"
        toDo.status = false

        Mockito.`when`(toDoRepository.findById(any())).thenReturn(Optional.of(toDo))
        val toDoMock = toDoService.buscar(1L)

        assertEquals(toDo.id, toDoMock.id)
        assertEquals(toDo.descricao, toDoMock.descricao)
        assertFalse(toDoMock.status)
    }

    @Test
    fun deveLancarExcecaoAoBuscarToDoComIdInexistente() {
        Mockito.`when`(toDoRepository.findById(any())).thenReturn(Optional.empty())
        assertThrows(EntityNotFoundException::class.java){
            toDoService.buscar(10L)
        }
    }

    @Test
    fun deveAtualizarToDo() {
        val toDo = ToDo().apply {
            nome = "ToDo 1"
            descricao = "Descrição 1"
            status = false
        }
        val toDoUpdate = ToDo().apply {
            nome = "ToDo atualizado"
            descricao = "Descrição atualizada"
            status = true
        }
        Mockito.`when`(toDoRepository.findById(any())).thenReturn(Optional.of(toDo))
        Mockito.`when`(toDoRepository.update(any())).thenReturn(toDoUpdate)
        val toDoMock = toDoService.atualizar(1L, ToDoRequestUpdate("ToDo atualizado", "Descrição atualizada", true))

        assertEquals(toDoUpdate.id, toDoMock.id)
        assertEquals(toDoUpdate.descricao, toDoMock.descricao)
        assertTrue(toDoMock.status)
    }

    @Test
    fun deveLancarExcecaoAoAtualizarToDoSemDescricao() {
        val toDo = ToDo().apply {
            nome = "ToDo 1"
            descricao = "Descrição 1"
            status = false
        }
        Mockito.`when`(toDoRepository.findById(any())).thenReturn(Optional.of(toDo))
        Mockito.`when`(toDoRepository.update(any())).thenThrow(ConstraintViolationException::class.java)
        assertThrows(ConstraintViolationException::class.java) {
            toDoService.atualizar(1L, ToDoRequestUpdate("ToDo atualizado", "", true))
        }
    }

    @Test
    fun deveLancarExcecaoAoAtualizarToDoComIdInexistente() {
        Mockito.`when`(toDoRepository.findById(any())).thenReturn(Optional.empty())
        assertThrows(EntityNotFoundException::class.java){
            toDoService.atualizar(10L, ToDoRequestUpdate("ToDo Atualizado", "Descrição atualizada", true))
        }
    }

    @Test
    fun deveDeletarToDo() {
        val toDo = ToDo()
        toDo.descricao = "ToDo 1"
        toDo.status = false

        Mockito.`when`(toDoRepository.findById(any())).thenReturn(Optional.of(toDo))

        toDoService.deletar(1L)
        verify(toDoRepository).delete(any())
    }

    @Test
    fun deveLancarExcecaoAoDeletarToDoComIdInexistente() {
        Mockito.`when`(toDoRepository.findById(any())).thenReturn(Optional.empty())
        assertThrows(EntityNotFoundException::class.java){
            toDoService.deletar(10L)
        }
    }

    @MockBean(ToDoRepository::class)
    fun toDoRepository(): ToDoRepository {
        return Mockito.mock(ToDoRepository::class.java)
    }
}