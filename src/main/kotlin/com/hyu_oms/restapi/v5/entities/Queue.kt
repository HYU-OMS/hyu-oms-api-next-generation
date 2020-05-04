package com.hyu_oms.restapi.v5.entities

import com.hyu_oms.restapi.v5.entities.Menu
import com.hyu_oms.restapi.v5.entities.Order
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
    var id: Long? = null,

    @ManyToOne
    var order: Order,

    @ManyToOne
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