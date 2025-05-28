package api.dto

import jakarta.validation.constraints.NotBlank
import io.micronaut.core.annotation.Introspected

@Introspected
data class ToDoRequest(
    @field:NotBlank(message = "Campo Nome deve ser preenchido!")
    var nome: String,
    
    @field:NotBlank(message = "Campo Descrição deve ser preenchido!")
    var descricao: String,

    var status: Boolean
)