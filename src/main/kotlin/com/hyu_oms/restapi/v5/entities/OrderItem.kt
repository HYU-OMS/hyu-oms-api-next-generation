package com.hyu_oms.restapi.v5.entities

import javax.persistence.*

@Entity
@Table(name = "`order_item`")
data class OrderItem(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long,

    @ManyToOne
    @JoinColumn(name = "order_id")
    var order: Order,

    @ManyToOne
    @JoinColumn(name = "menu_id")
    var menuItem: Menu? = null,

    var menuAmount: Int? = null,

    @ManyToOne
    @JoinColumn(name = "set_menu_id")
    var setMenuItem: SetMenu? = null,

    var setMenuAmount: Int? = null
)