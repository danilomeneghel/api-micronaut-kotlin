package api.service

import api.model.ToDo
import api.dto.ToDoRequest
import api.dto.ToDoRequestUpdate
import api.repository.ToDoRepository
import io.micronaut.data.model.Page
import io.micronaut.data.model.Pageable
import jakarta.inject.Singleton
import jakarta.persistence.EntityNotFoundException

@Singleton
open class ToDoService(private val toDoRepository: ToDoRepository) {

    fun listar(status: Boolean?, pageable: Pageable): Page<ToDo>? {
        if(status != null){
            return toDoRepository.findByStatus(status, pageable)
        }
        return toDoRepository.findAll(pageable)
    }

    fun buscar(id: Long): ToDo {
        return toDoRepository.findById(id).orElseThrow{ EntityNotFoundException("ToDo Não encontrado!") }
    }

    fun salvar(toDoRequest: ToDoRequest): ToDo {
        val toDo = ToDo()
        toDo.nome = toDoRequest.nome
        toDo.descricao = toDoRequest.descricao
        toDo.status = toDoRequest.status
        return toDoRepository.save(toDo)
    }

    fun atualizar(id: Long, toDo: ToDoRequestUpdate): ToDo {
        val toDoDb = buscar(id)
        toDoDb.nome = toDo.nome
        toDoDb.descricao = toDo.descricao
        toDoDb.status = toDo.status
        return toDoRepository.update(toDoDb)
    }

    fun deletar(id: Long) {
        val toDoDb = buscar(id)
        toDoRepository.delete(toDoDb)
    }
}