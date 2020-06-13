package com.hyu_oms.restapi.v5.responses

class ClientError4XX {
  companion object {
    val REST_CLIENT_ERROR = mutableMapOf<String, Any?>(
        "code" to "F000",
        "message" to "Error occurred from external API server.",
        "data" to null
    )
    val JWT_CREATION_ERROR = mutableMapOf<String, Any?>(
        "code" to "F001",
        "message" to "Unable to create new JWT.",
        "data" to null
    )
    val JWT_VERIFICATION_ERROR = mutableMapOf<String, Any?>(
        "code" to "F002",
        "message" to "Unable to verify given JWT.",
        "data" to null
    )
    val JWT_UNAUTHORIZED = mutableMapOf<String, Any?>(
        "code" to "F003",
        "message" to "JWT must be provided.",
        "data" to null
    )
    val JWT_FORBIDDEN = mutableMapOf<String, Any?>(
        "code" to "F004",
        "message" to "Access denied with provided JWT.",
        "data" to null
    )
    val UNSUPPORTED_SOCIAL_MEDIA_ERROR = mutableMapOf<String, Any?>(
        "code" to "F005",
        "message" to "This social media is not supported yet.",
        "data" to null
    )
    val USER_NOT_ENROLLED_TO_GROUP_ERROR = mutableMapOf<String, Any?>(
        "code" to "F006",
        "message" to "This user is not enrolled to requested group.",
        "data" to null
    )
    val GROUP_NOT_FOUND_ERROR = mutableMapOf<String, Any?>(
        "code" to "F007",
        "message" to "Requested group does not exists.",
        "data" to null
    )
    val PERMISSION_DENIED_ERROR = mutableMapOf<String, Any?>(
        "code" to "F008",
        "message" to "You don't have enough permission for this request.",
        "data" to null
    )
    val MENU_NOT_FOUND_ERROR = mutableMapOf<String, Any?>(
        "code" to "F009",
        "message" to "Requested menu does not exists.",
        "data" to null
    )
    val SET_MENU_NOT_FOUND_ERROR = mutableMapOf<String, Any?>(
        "code" to "F010",
        "message" to "Requested setmenu does not exists.",
        "data" to null
    )

    // TODO: 어떤 메뉴가 disabled 되었는지 담아서 보내줘야 함.
    val DISABLED_MENU_OR_SET_MENU_REQUESTED_ERROR = mutableMapOf<String, Any?>(
        "code" to "F011",
        "message" to "Requested menu or setmenu is disabled.",
        "data" to listOf<String>()
    )
    val ORDER_IS_NOT_PENDING_STATUS_ERROR = mutableMapOf<String, Any?>(
        "code" to "F012",
        "message" to "Requested order is not in pending status.",
        "data" to null
    )
    val QUEUE_ITEM_NOT_FOUND_ERROR = mutableMapOf<String, Any?>(
        "code" to "F013",
        "message" to "Requested queue item does not exists.",
        "data" to null
    )
    val QUEUE_ITEM_ALREADY_DELIVERED_ERROR = mutableMapOf<String, Any?>(
        "code" to "F014",
        "message" to "Requested queue item is already delivered.",
        "data" to null
    )
    val CREATOR_MODIFY_REQUESTED_ERROR = mutableMapOf<String, Any?>(
        "code" to "F015",
        "message" to "Requested member must not be modified since this member is a creator of this group.",
        "data" to null
    )
  }
}