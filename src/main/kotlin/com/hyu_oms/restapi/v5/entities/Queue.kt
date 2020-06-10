package com.hyu_oms.restapi.v5.entities

import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.time.LocalDateTime
import java.time.ZoneId
import javax.persistence.*

@Entity
@Table(name = "`queue`")
data class Queue(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,

    @ManyToOne
    @JoinColumn(name = "order_id")
    var order: Order,

    @ManyToOne
    @JoinColumn(name = "group_id")
    var group: Group,

    @ManyToOne
    @JoinColumn(name = "menu_id")
    var menu: Menu,

    @Column(length = 127, nullable = false)
    var destination: String,

    var amount: Int,

    var delivered: Boolean = false,

    @CreationTimestamp
    var createdAt: LocalDateTime = LocalDateTime.now(ZoneId.of("UTC")),

    @UpdateTimestamp
    var updatedAt: LocalDateTime = LocalDateTime.now(ZoneId.of("UTC"))
)