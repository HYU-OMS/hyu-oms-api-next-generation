package com.hyu_oms.restapi.v5.dtos.menu

import com.fasterxml.jackson.annotation.JsonProperty
import javax.validation.constraints.NotEmpty

data class MenuUpdateRequestDto(
    @field:NotEmpty
    @JsonProperty("menu_id")
    var menuId: Long?,

    @JsonProperty("price")
    var price: Int?,

    @JsonProperty("enabled")
    var enabled: Boolean?
)