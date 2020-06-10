package com.hyu_oms.restapi.v5.entities

import com.hyu_oms.restapi.v5.enums.OrderStatus
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
    var id: Long = 0,

    @ManyToOne
    @JoinColumn(name = "user_id")
    var user: User,

    @ManyToOne
    @JoinColumn(name = "group_id")
    var group: Group,

    @Column(length = 127, nullable = false)
    var destination: String,

    var totalPrice: Int,

    var status: OrderStatus = OrderStatus.PD,

    @OneToMany(mappedBy = "order", targetEntity = OrderItem::class, fetch = FetchType.LAZY)
    var orderItems: List<OrderItem> = arrayListOf(),

    @CreationTimestamp
    var createdAt: LocalDateTime = LocalDateTime.now(ZoneId.of("UTC")),

    @UpdateTimestamp
    var updatedAt: LocalDateTime = LocalDateTime.now(ZoneId.of("UTC"))
)