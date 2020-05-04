package com.hyu_oms.restapi.v5.entities

import com.hyu_oms.restapi.v5.enums.SocialAccountType
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.time.LocalDateTime
import java.time.ZoneId
import javax.persistence.*

@Entity
@Table(
    name = "`social_account`",
    uniqueConstraints = [
      UniqueConstraint(columnNames = arrayOf("account_type", "account_id"))
    ]
)
data class SocialAccount(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @ManyToOne
    @JoinColumn(name = "user_id")
    var user: User,

    @Column(name = "account_type")
    var accountType: SocialAccountType,

    @Column(length = 127, nullable = false, name = "account_id")
    var accountId: String,

    @CreationTimestamp
    var createdAt: LocalDateTime = LocalDateTime.now(ZoneId.of("UTC")),

    @UpdateTimestamp
    var updatedAt: LocalDateTime = LocalDateTime.now(ZoneId.of("UTC"))
)