package com.hyu_oms.restapi.v5.dtos.menu

import com.fasterxml.jackson.annotation.JsonProperty
import javax.validation.constraints.NotEmpty

data class MenuContentItemDto(
    @field:NotEmpty
    @JsonProperty("menu_id")
    var menuId: Long?,

    @field:NotEmpty
    @JsonProperty("amount")
    var amount: Int?
)