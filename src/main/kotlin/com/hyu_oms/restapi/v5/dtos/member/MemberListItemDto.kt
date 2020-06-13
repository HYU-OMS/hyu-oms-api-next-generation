package com.hyu_oms.restapi.v5.dtos.member

import com.hyu_oms.restapi.v5.dtos.user.UserListItemDto

data class MemberListItemDto(
    var id: Long,
    var user: UserListItemDto,
    var enabled: Boolean,
    var hasAdminPermission: Boolean
) {
  constructor() : this(
      0,
      UserListItemDto(0, ""),
      true,
      false
  )
}