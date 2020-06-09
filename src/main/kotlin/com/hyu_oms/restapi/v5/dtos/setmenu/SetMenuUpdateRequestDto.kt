package com.hyu_oms.restapi.v5.dtos.setmenu

import com.fasterxml.jackson.annotation.JsonProperty
import javax.validation.constraints.NotEmpty

data class SetMenuUpdateRequestDto(
    @field:NotEmpty
    @JsonProperty("set_menu_id")
    var setMenuId: Long?,

    @JsonProperty("price")
    var price: Int?,

    @JsonProperty("enabled")
    var enabled: Boolean?
)