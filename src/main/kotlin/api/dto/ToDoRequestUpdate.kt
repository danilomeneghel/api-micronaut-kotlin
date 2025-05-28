package api.dto

import io.micronaut.core.annotation.Introspected
import jakarta.validation.constraints.NotBlank

@Introspected
data class ToDoRequestUpdate(
    @field:NotBlank(message = "Campo Nome deve ser preenchido!")
    var nome: String,

    @field:NotBlank(message = "Campo Descrição deve ser preenchido!")
    var descricao: String,

    var status: Boolean
)