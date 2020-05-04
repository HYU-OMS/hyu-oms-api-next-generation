package com.hyu_oms.restapi.v5.set_menu

import com.hyu_oms.restapi.v5.menu.Menu
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
    var id: Long? = null,

    @OneToOne
    var menu: Menu,

    var amount: Int,

    @CreationTimestamp
    var createdAt: LocalDateTime = LocalDateTime.now(ZoneId.of("UTC")),

    @UpdateTimestamp
    var updatedAt: LocalDateTime = LocalDateTime.now(ZoneId.of("UTC"))
)