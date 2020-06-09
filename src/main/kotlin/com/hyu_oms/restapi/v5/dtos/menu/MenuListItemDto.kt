package com.hyu_oms.restapi.v5.dtos.menu

data class MenuListItemDto(
    var id: Long,
    var name: String,
    var price: Long,
    var enabled: Boolean = true
) {
  constructor() : this(0, "", 0, true)
}