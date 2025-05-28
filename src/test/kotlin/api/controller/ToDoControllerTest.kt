package api.controller

import api.exception.ErrorMessage
import api.model.ToDo
import api.dto.ToDoRequest
import api.dto.ToDoRequestUpdate
import api.repository.ToDoRepository
import io.micronaut.data.model.Page
import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpStatus
import io.micronaut.http.client.HttpClient
import io.micronaut.http.client.annotation.Client
import io.micronaut.http.client.exceptions.HttpClientResponseException
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import jakarta.inject.Inject
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@MicronautTest
internal class ToDoControllerTest {

    @field:Inject
    @field:Client("/api")
    lateinit var client: HttpClient

    @field:Inject
    lateinit var toDoRepository: ToDoRepository

    lateinit var toDo1: ToDo
    lateinit var toDo2: ToDo
    lateinit var toDo3: ToDo
    lateinit var toDo4: ToDo
    lateinit var toDo5: ToDo

    @BeforeEach
    fun setUp() {
        val t1 = ToDo().apply {
            nome = "nome1"
            descricao = "descricao1"
            status = true
        }
        val t2 = ToDo().apply {
            nome = "nome2"
            descricao = "descricao2"
            status = false
        }
        val t3 = ToDo().apply {
            nome = "nome3"
            descricao = "descricao3"
            status = true
        }
        val t4 = ToDo().apply {
            nome = "nome4"
            descricao = "descricao4"
            status = false
        }
        val t5 = ToDo().apply {
            nome = "nome5"
            descricao = "descricao5"
            status = true
        }

        toDo1 = toDoRepository.save(t1)
        toDo2 = toDoRepository.save(t2)
        toDo3 = toDoRepository.save(t3)
        toDo4 = toDoRepository.save(t4)
        toDo5 = toDoRepository.save(t5)
    }

    @AfterEach
    fun tearDown() {
        toDoRepository.deleteAll()
    }

    @Test
    fun deveSalvarUmToDo() {
        val toDo = ToDoRequest(
            nome = "Nome",
            descricao = "Descrição",
            status = false
        )
        val response = client.toBlocking().exchange(HttpRequest.POST("/", toDo), ToDo::class.java)

        assertEquals(HttpStatus.CREATED, response.status)
        assertNotNull(response.body())
        assertNotNull(response.body()!!.id)
        assertEquals(toDo.descricao, response.body()!!.descricao)
        assertEquals(false, response.body()!!.status)
    }

    @Test
    fun deveLancarExcecaoAoSalvarToDoSemDescricao() {
        val toDo = ToDoRequest(
            nome = "Nome inválido",
            descricao = "",
            status = false
        )
        var responseErro: ErrorMessage? = null
        val exception: HttpClientResponseException = assertThrows(HttpClientResponseException::class.java){
            // For validation errors (BAD_REQUEST), we expect to parse ErrorMessage
            responseErro = client.toBlocking().retrieve(HttpRequest.POST("/", toDo), ErrorMessage::class.java)
        }

        assertEquals(HttpStatus.BAD_REQUEST, exception.status)
        responseErro?.message?.let { assertTrue(it.contains("Campo Descrição deve ser preenchido")) }
    }

    @Test
    fun deveListarTodosOsToDos() {
        val response = client.toBlocking().exchange("/", Page::class.java)

        assertEquals(HttpStatus.OK, response.status)
        assertNotNull(response.body())
        assertEquals(5, (response.body() as Page<*>).content.size)
    }

    @Test
    fun deveListarTodosOsToDosComFiltro() {
        val response = client.toBlocking().exchange("/?status=true", Page::class.java)

        assertEquals(HttpStatus.OK, response.status)
        assertNotNull(response.body())
        assertEquals(3, (response.body() as Page<*>).content.size)
    }

    @Test
    fun deveBuscarToDoPorId() {
        val response = client.toBlocking().exchange("/${toDo1.id}", ToDo::class.java)

        assertEquals(HttpStatus.OK, response.status)
        assertNotNull(response.body())
        assertEquals(toDo1.id, response.body()!!.id)
        assertEquals("descricao1", response.body()!!.descricao)
    }

    @Test
    fun deveLancarExcecaoAoBuscarToDoComIdInexistente() {
        var responseErro: ErrorMessage? = null
        val exception: HttpClientResponseException = assertThrows(HttpClientResponseException::class.java) {
            // For NOT_FOUND errors, we expect to parse ErrorMessage
            responseErro = client.toBlocking().retrieve("/10", ErrorMessage::class.java)
        }
        responseErro?.message?.let { assertTrue(it.contains("ToDo Não encontrado!")) }
    }

    @Test
    fun deveAtualizarToDo() {
        val toDo = ToDoRequestUpdate(
            nome = "Nome Atualizado",
            descricao = "Descrição Atualizada",
            status = true
        )
        val response = client.toBlocking().exchange(HttpRequest.PUT("/${toDo2.id}", toDo), ToDo::class.java)

        assertEquals(HttpStatus.OK, response.status)
        assertNotNull(response.body())
        assertEquals(toDo2.id, response.body()!!.id)
        assertEquals(toDo.descricao, response.body()!!.descricao)
        assertEquals(true, response.body()!!.status)
    }

    @Test
    fun deveLancarExcecaoAoAtualizarToDoSemDescricao() {
        val toDo = ToDoRequestUpdate(
            nome = "Nome inválido",
            descricao = "",
            status = true
        )
        var responseErro: ErrorMessage? = null
        val exception: HttpClientResponseException = assertThrows(HttpClientResponseException::class.java){
            // For validation errors (BAD_REQUEST), we expect to parse ErrorMessage
            responseErro = client.toBlocking().retrieve(HttpRequest.PUT("/${toDo2.id}", toDo), ErrorMessage::class.java)
        }

        assertEquals(HttpStatus.BAD_REQUEST, exception.status)
        responseErro?.message?.let { assertTrue(it.contains("Campo Descrição deve ser preenchido")) }
    }

    @Test
    fun deveLancarExcecaoAoAtualizarToDoComIdInexistente() {
        val toDo = ToDoRequestUpdate(
            nome = "Nome atualizado",
            descricao = "Descrição Atualizada",
            status = true
        )
        var responseErro: ErrorMessage? = null
        val exception: HttpClientResponseException = assertThrows(HttpClientResponseException::class.java) {
            // For NOT_FOUND errors, we expect to parse ErrorMessage
            responseErro = client.toBlocking().retrieve(HttpRequest.PUT("/20", toDo), ErrorMessage::class.java)
        }

        responseErro?.message?.let { assertTrue(it.contains("ToDo Não encontrado!")) }
    }

    @Test
    fun deveDeletarToDo() {
        val response = client.toBlocking().exchange(HttpRequest.DELETE("/${toDo1.id}",null), HttpResponse::class.java)

        assertEquals(HttpStatus.NO_CONTENT, response.status)
        assertNull(response.body())

        // After deleting, trying to retrieve it should result in NOT_FOUND with a specific message
        var responseErro: ErrorMessage? = null
        val exception = assertThrows(HttpClientResponseException::class.java) {
            responseErro = client.toBlocking().retrieve("/${toDo1.id}", ErrorMessage::class.java)
        }
        
        responseErro?.message?.let { assertTrue(it.contains("ToDo Não encontrado!")) }
    }

    @Test
    fun deveLancarExecaooAoDeletarToDoComIdInexistente() {
        var responseErro: ErrorMessage? = null
        val exception = assertThrows(HttpClientResponseException::class.java) {
            // For NOT_FOUND errors, we expect to parse ErrorMessage
            responseErro = client.toBlocking().retrieve(HttpRequest.DELETE("/10",null), ErrorMessage::class.java)
        }

        responseErro?.message?.let { assertTrue(it.contains("ToDo Não encontrado!")) }
    }
}