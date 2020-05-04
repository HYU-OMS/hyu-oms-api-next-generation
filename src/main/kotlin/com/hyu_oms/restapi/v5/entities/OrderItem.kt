package com.hyu_oms.restapi.v5.entities

import javax.persistence.*

@Entity
@Table(name = "`order_item`")
data class OrderItem(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @ManyToOne
    var order: Order,

    @ManyToOne
    var menuItem: Menu? = null,

    var menuAmount: Int? = null,

    @ManyToOne
    var setMenuItem: SetMenu? = null,

    var setMenuAmount: Int? = null
)