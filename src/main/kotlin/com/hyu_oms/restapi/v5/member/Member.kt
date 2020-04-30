package com.hyu_oms.restapi.v5.member

import com.hyu_oms.restapi.v5.group.Group
import com.hyu_oms.restapi.v5.user.User
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.time.LocalDateTime
import java.time.ZoneId
import javax.persistence.*

@Entity
data class Member(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @ManyToOne
    var user: User,

    @ManyToOne
    var group: Group,

    var isEnabled: Boolean = false,
    var isAdmin: Boolean = false,

    @CreationTimestamp
    var createdAt: LocalDateTime = LocalDateTime.now(ZoneId.of("UTC")),

    @UpdateTimestamp
    var updatedAt: LocalDateTime = LocalDateTime.now(ZoneId.of("UTC"))
)