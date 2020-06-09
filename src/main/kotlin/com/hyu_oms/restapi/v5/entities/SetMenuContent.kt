package com.hyu_oms.restapi.v5.entities

import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.time.LocalDateTime
import java.time.ZoneId
import javax.persistence.*

@Entity
@Table(name = "`set_menu_content`")
data class SetMenuContent(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,

    @ManyToOne
    @JoinColumn(name = "set_menu_id")
    var setMenu: SetMenu,

    @OneToOne
    var menu: Menu,

    var amount: Int,

    @CreationTimestamp
    var createdAt: LocalDateTime = LocalDateTime.now(ZoneId.of("UTC")),

    @UpdateTimestamp
    var updatedAt: LocalDateTime = LocalDateTime.now(ZoneId.of("UTC"))
)