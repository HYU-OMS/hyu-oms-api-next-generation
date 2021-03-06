package com.hyu_oms.restapi.v5.dtos.setmenu

import com.fasterxml.jackson.annotation.JsonProperty
import javax.validation.constraints.NotEmpty

data class SetMenuContentItemDto(
    @field:NotEmpty
    @JsonProperty("set_menu_id")
    var setMenuId: Long?,

    @field:NotEmpty
    @JsonProperty("amount")
    var amount: Int?
)