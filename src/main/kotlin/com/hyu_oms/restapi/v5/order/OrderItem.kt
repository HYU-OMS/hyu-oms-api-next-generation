package com.hyu_oms.restapi.v5.order

import com.hyu_oms.restapi.v5.menu.Menu
import com.hyu_oms.restapi.v5.setmenu.SetMenu
import javax.persistence.*

@Entity
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