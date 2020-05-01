package com.hyu_oms.restapi.v5.user

import com.hyu_oms.restapi.v5.social_account.SocialAccount
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.time.LocalDateTime
import java.time.ZoneId
import javax.persistence.*

@Entity
@Table(name = "`user`")
data class User(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @Column(length = 127, nullable = false)
    var name: String,

    @OneToMany
    var linkedSocialAccounts: Set<SocialAccount> = setOf(),

    @Column(nullable = false)
    var isEnabled: Boolean = false,

    @CreationTimestamp
    var createdAt: LocalDateTime = LocalDateTime.now(ZoneId.of("UTC")),

    @UpdateTimestamp
    var updatedAt: LocalDateTime = LocalDateTime.now(ZoneId.of("UTC"))
)