package com.hyu_oms.restapi.v5.entities

import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.time.LocalDateTime
import java.time.ZoneId
import javax.persistence.*

@Entity
@Table(
    name = "`group`",
    indexes = [
        Index(columnList = "enabled"),
        Index(columnList = "allow_register")
    ]
)
data class Group(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @Column(length = 127, nullable = false)
    var name: String,

    @ManyToOne(optional = true)
    var creator: User? = null,

    @OneToMany(mappedBy = "group")
    var members: List<Member> = listOf(),

    @Column(name = "enabled", nullable = false)
    var enabled: Boolean = true,

    @Column(name = "allow_register", nullable = false)
    var allowRegister: Boolean = false,

    @CreationTimestamp
    var createdAt: LocalDateTime = LocalDateTime.now(ZoneId.of("UTC")),

    @UpdateTimestamp
    var updatedAt: LocalDateTime = LocalDateTime.now(ZoneId.of("UTC"))
)
