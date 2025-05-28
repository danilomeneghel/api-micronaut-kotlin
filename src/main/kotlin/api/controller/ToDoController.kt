package api.controller

import api.model.ToDo
import api.dto.ToDoRequest
import api.dto.ToDoRequestUpdate
import api.service.ToDoService
import io.micronaut.data.model.Page
import io.micronaut.data.model.Pageable
import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.*
import io.micronaut.validation.Validated
import jakarta.validation.Valid

@Validated
@Controller("/api")
class ToDoController(private val toDoService: ToDoService) {

    @Get
    fun listar(@QueryValue status: Boolean?, pageable: Pageable): HttpResponse<Page<ToDo>>{
        return HttpResponse.ok(toDoService.listar(status, pageable))
    }

    @Get("/{id}")
    fun buscar(@PathVariable id: Long): HttpResponse<ToDo> {
        return HttpResponse.ok(toDoService.buscar(id))
    }

    @Post
    fun salvar(@Body @Valid toDoRequest: ToDoRequest): HttpResponse<ToDo> {
        return HttpResponse.created(toDoService.salvar(toDoRequest))
    }

    @Put("/{id}")
    fun atualizar(@PathVariable id: Long, @Body @Valid toDo: ToDoRequestUpdate): HttpResponse<ToDo> {
        return HttpResponse.ok(toDoService.atualizar(id, toDo))
    }

    @Delete("/{id}")
    fun deletar(@PathVariable id: Long): HttpResponse<Unit> {
        toDoService.deletar(id)
        return HttpResponse.noContent()
    }
}