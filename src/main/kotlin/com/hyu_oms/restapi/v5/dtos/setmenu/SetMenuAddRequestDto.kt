package com.hyu_oms.restapi.v5.dtos.setmenu

import com.fasterxml.jackson.annotation.JsonProperty
import com.hyu_oms.restapi.v5.dtos.menu.MenuContentItemDto
import javax.validation.constraints.NotEmpty

data class SetMenuAddRequestDto(
    @field:NotEmpty
    @JsonProperty("group_id")
    var groupId: Long?,

    @field:NotEmpty
    @JsonProperty("name")
    var name: String?,

    @field:NotEmpty
    @JsonProperty("price")
    var price: Int?,

    @field:NotEmpty
    @JsonProperty("menu_contents")
    var menuContents: List<MenuContentItemDto>?
)