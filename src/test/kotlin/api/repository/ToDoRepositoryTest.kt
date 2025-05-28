package api.repository

import api.model.ToDo
import io.micronaut.data.model.Page
import io.micronaut.data.model.Pageable
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import jakarta.inject.Inject 
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

@MicronautTest
class ToDoRepositoryTest {

    @Inject
    lateinit var toDoRepository: ToDoRepository

    @Test
    fun `findByStatus deve retornar ToDos com status especificado`() {
        val toDo1 = ToDo().apply {
            nome = "Nome 1"
            descricao = "Descrição 1"
            status = true
        }
        val toDo2 = ToDo().apply {
            nome = "Nome 2"
            descricao = "Descrição 2"
            status = false
        }
        toDoRepository.save(toDo1)
        toDoRepository.save(toDo2)

        val result = toDoRepository.findByStatus(true, Pageable.from(0, 10))

        assertNotNull(result)
        assertEquals(1, result.content.size) 
        assertEquals("Nome 1", result.content[0].nome)
    }

    @Test
    fun `findAll deve retornar todos os ToDos`() {
        val toDo1 = ToDo().apply {
            nome = "Nome 1"
            descricao = "Descrição 1"
            status = true
        }
        val toDo2 = ToDo().apply {
            nome = "Nome 2"
            descricao = "Descrição 2"
            status = false
        }
        toDoRepository.save(toDo1)
        toDoRepository.save(toDo2)

        val result = toDoRepository.findAll(Pageable.from(0, 10))

        assertNotNull(result)
        assertEquals(2, result.content.size) 
    }

    @Test
    fun `save deve persistir um ToDo`() {
        val toDo = ToDo().apply {
            nome = "Novo Nome"
            descricao = "Nova Descrição"
            status = true
        }

        val savedToDo = toDoRepository.save(toDo)

        assertNotNull(savedToDo.id)
        assertEquals("Novo Nome", savedToDo.nome)
        assertEquals("Nova Descrição", savedToDo.descricao)
        assertTrue(savedToDo.status)
    }

    @Test
    fun `delete deve remover um ToDo`() {
        val toDo = ToDo().apply {
            nome = "Nome"
            descricao = "Descrição"
            status = false
        }
        val savedToDo = toDoRepository.save(toDo)

        toDoRepository.delete(savedToDo)

        val result = toDoRepository.findById(savedToDo.id!!)
        assertTrue(result.isEmpty)
    }
}
