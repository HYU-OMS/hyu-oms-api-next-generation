package com.hyu_oms.restapi.v5.dtos.setmenu

import com.fasterxml.jackson.annotation.JsonProperty

data class SetMenuAddResponseDto(
    @JsonProperty("new_setmenu_id")
    var newSetMenuId: Long
) {
  constructor() : this(0)
}