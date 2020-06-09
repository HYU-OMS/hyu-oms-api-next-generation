package com.hyu_oms.restapi.v5.dtos.menu

import com.fasterxml.jackson.annotation.JsonProperty
import javax.validation.constraints.NotEmpty

data class MenuAddRequestDto(
    @field:NotEmpty
    @JsonProperty("group_id")
    var groupId: Long?,

    @field:NotEmpty
    @JsonProperty("name")
    var name: String?,

    @field:NotEmpty
    @JsonProperty("price")
    var price: Int?
)