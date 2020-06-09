package com.hyu_oms.restapi.v5.entities

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
    var id: Long = 0,

    @Column(length = 127, nullable = false)
    var name: String,

    var price: Int,

    @OneToMany(mappedBy = "setMenu", targetEntity = SetMenuContent::class, fetch = FetchType.LAZY)
    var setMenuContents: List<SetMenuContent> = arrayListOf(),

    @ManyToOne
    @JoinColumn(name = "group_id")
    var group: Group,

    var enabled: Boolean = true,

    @CreationTimestamp
    var createdAt: LocalDateTime = LocalDateTime.now(ZoneId.of("UTC")),

    @UpdateTimestamp
    var updatedAt: LocalDateTime = LocalDateTime.now(ZoneId.of("UTC"))
)