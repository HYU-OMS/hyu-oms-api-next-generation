package com.hyu_oms.restapi.v5.dtos.setmenu

import com.fasterxml.jackson.annotation.JsonProperty

data class SetMenuUpdateResponseDto(
    @JsonProperty("setmenu_id")
    var setMenuId: Long
) {
  constructor() : this(0)
}