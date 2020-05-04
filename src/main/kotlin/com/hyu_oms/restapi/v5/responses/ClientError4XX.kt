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
  }
}