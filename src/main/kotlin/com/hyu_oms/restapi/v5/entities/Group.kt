package com.hyu_oms.restapi.v5.entities

import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.time.LocalDateTime
import java.time.ZoneId
import javax.persistence.*

@Entity
@Table(name = "`group`")
data class Group(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long,

    @Column(length = 127, nullable = false)
    var name: String,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creator_id", nullable = false)
    var creator: User,

    @OneToMany(mappedBy = "group", targetEntity = Member::class, fetch = FetchType.LAZY)
    var members: List<Member> = arrayListOf(),

    @Column(name = "enabled", nullable = false)
    var enabled: Boolean = true,

    @Column(name = "allow_register", nullable = false)
    var allowRegister: Boolean = false,

    @CreationTimestamp
    var createdAt: LocalDateTime = LocalDateTime.now(ZoneId.of("UTC")),

    @UpdateTimestamp
    var updatedAt: LocalDateTime = LocalDateTime.now(ZoneId.of("UTC"))
)
