package com.hyu_oms.restapi.v5.dtos.setmenu

data class SetMenuListItemDto(
    var id: Long,
    var name: String,
    var price: Long,
    var enabled: Boolean
) {
  constructor() : this(0, "", 0, true)
}