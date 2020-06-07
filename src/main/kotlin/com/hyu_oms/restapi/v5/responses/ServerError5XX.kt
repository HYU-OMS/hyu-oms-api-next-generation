package com.hyu_oms.restapi.v5.responses

class ServerError5XX {
  companion object {
    val INTERNAL_SERVER_ERROR = mutableMapOf<String, Any?>(
        "code" to "E000",
        "message" to "Internal server error.",
        "data" to null
    )
  }
}