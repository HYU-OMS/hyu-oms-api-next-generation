package com.hyu_oms.restapi.v5.group

import com.hyu_oms.restapi.v5.member.Member
import com.hyu_oms.restapi.v5.user.User
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.time.LocalDateTime
import java.time.ZoneId
import javax.persistence.*

@Entity
data class Group(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @Column(length = 127, nullable = false)
    var name: String,

    @ManyToOne(optional = true)
    var creator: User? = null,

    @OneToMany
    var members: List<Member> = listOf(),

    @Column(nullable = false)
    var isEnabled: Boolean = true,

    @Column(nullable = false)
    var allowRegister: Boolean = false,

    @CreationTimestamp
    var createdAt: LocalDateTime = LocalDateTime.now(ZoneId.of("UTC")),

    @UpdateTimestamp
    var updatedAt: LocalDateTime = LocalDateTime.now(ZoneId.of("UTC"))
)
