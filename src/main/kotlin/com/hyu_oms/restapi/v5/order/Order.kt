package com.hyu_oms.restapi.v5.order

import com.hyu_oms.restapi.v5.group.Group
import com.hyu_oms.restapi.v5.menu.Menu
import com.hyu_oms.restapi.v5.user.User
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.time.LocalDateTime
import java.time.ZoneId
import javax.persistence.*

@Entity
@Table(name = "`order`")
data class Order(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @ManyToOne
    var user: User,

    @ManyToOne
    var group: Group,

    @Column(length = 127, nullable = false)
    var destination: String,

    var totalPrice: Long,

    @OneToMany
    var orderItems: List<OrderItem>,

    var status: OrderStatus = OrderStatus.PD,

    @CreationTimestamp
    var createdAt: LocalDateTime = LocalDateTime.now(ZoneId.of("UTC")),

    @UpdateTimestamp
    var updatedAt: LocalDateTime = LocalDateTime.now(ZoneId.of("UTC"))
)