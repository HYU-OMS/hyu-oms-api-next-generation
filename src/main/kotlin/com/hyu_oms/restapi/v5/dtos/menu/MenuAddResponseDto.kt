package com.hyu_oms.restapi.v5.dtos.menu

import com.fasterxml.jackson.annotation.JsonProperty

data class MenuAddResponseDto(
    @JsonProperty("new_menu_id")
    var newMenuId: Long
) {
  constructor() : this(0)
}