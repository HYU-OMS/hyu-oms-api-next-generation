package com.hyu_oms.restapi.v5.dtos.order

import com.fasterxml.jackson.annotation.JsonProperty
import com.hyu_oms.restapi.v5.dtos.menu.MenuContentItemDto
import com.hyu_oms.restapi.v5.dtos.setmenu.SetMenuContentItemDto
import javax.validation.constraints.NotEmpty

data class OrderAddRequestDto(
    @field:NotEmpty
    @JsonProperty("group_id")
    var groupId: Long?,

    @field:NotEmpty
    @JsonProperty("destination")
    var destination: String?,

    @field:NotEmpty
    @JsonProperty("menu_contents")
    var menuContents: List<MenuContentItemDto>?,

    @field:NotEmpty
    @JsonProperty("set_menu_contents")
    var setMenuContents: List<SetMenuContentItemDto>?
)