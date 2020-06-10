package com.hyu_oms.restapi.v5.dtos.queue

import com.hyu_oms.restapi.v5.dtos.menu.MenuListItemDto

data class QueueItemDto(
    var id: Long,
    var menu: MenuListItemDto,
    var destination: String,
    var amount: Int
) {
  constructor() : this(
      0,
      MenuListItemDto(),
      "",
      0
  )
}