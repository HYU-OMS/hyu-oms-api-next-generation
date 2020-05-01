package com.hyu_oms.restapi.v5.order

enum class OrderStatus(fullName: String) {
  PD("Pending"),
  AP("Approved"),
  RJ("Rejected")
}