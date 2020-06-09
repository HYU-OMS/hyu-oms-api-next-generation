package com.hyu_oms.restapi.v5.dtos.menu

import com.fasterxml.jackson.annotation.JsonProperty

data class MenuUpdateResponseDto(
    @JsonProperty("menu_id")
    var menuId: Long
) {
  constructor() : this(0)
}