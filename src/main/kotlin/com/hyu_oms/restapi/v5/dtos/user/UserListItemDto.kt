package com.hyu_oms.restapi.v5.dtos.user

data class UserListItemDto(
    var id: Long,
    var name: String
) {
  constructor() : this(0, "")
}