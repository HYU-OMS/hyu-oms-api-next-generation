package com.hyu_oms.restapi.v5.entities

import com.hyu_oms.restapi.v5.entities.Group
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.time.LocalDateTime
import java.time.ZoneId
import javax.persistence.*

@Entity
@Table(name = "`set_menu`")
data class SetMenu(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @Column(length = 127, nullable = false)
    var name: String,

    var price: Long,

    @ManyToOne
    @JoinColumn(name = "group_id")
    var group: Group,

    var enabled: Boolean = true,

    @CreationTimestamp
    var createdAt: LocalDateTime = LocalDateTime.now(ZoneId.of("UTC")),

    @UpdateTimestamp
    var updatedAt: LocalDateTime = LocalDateTime.now(ZoneId.of("UTC"))
)