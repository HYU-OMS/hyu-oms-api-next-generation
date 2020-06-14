package com.hyu_oms.restapi.v5.services

import com.hyu_oms.restapi.v5.dtos.menu.MenuListItemDto
import com.hyu_oms.restapi.v5.entities.Group
import com.hyu_oms.restapi.v5.entities.Member
import com.hyu_oms.restapi.v5.entities.Menu
import com.hyu_oms.restapi.v5.entities.User
import com.hyu_oms.restapi.v5.exceptions.PermissionDeniedException
import com.hyu_oms.restapi.v5.repositories.GroupRepository
import com.hyu_oms.restapi.v5.repositories.MemberRepository
import com.hyu_oms.restapi.v5.repositories.MenuRepository
import com.hyu_oms.restapi.v5.repositories.UserRepository
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.hasItem
import org.hamcrest.Matchers.hasItems
import org.junit.jupiter.api.*
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.util.*
import kotlin.random.Random

@ExtendWith(SpringExtension::class)
@DataJpaTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles(value = ["test"])
class MenuServiceTest {
  private lateinit var menuService: MenuService

  @Autowired
  private lateinit var userRepository: UserRepository

  @Autowired
  private lateinit var groupRepository: GroupRepository

  @Autowired
  private lateinit var memberRepository: MemberRepository

  @Autowired
  private lateinit var menuRepository: MenuRepository

  private val groupIdToMenuIds = mutableMapOf<Long, MutableList<Long>>()
  private var notEnrolledUserId: Long = 0

  @BeforeAll
  fun setUp() {
    this.menuService = MenuService(
        userRepository = this.userRepository,
        groupRepository = this.groupRepository,
        memberRepository = this.memberRepository,
        menuRepository = this.menuRepository
    )

    for (itr1 in 1..2) {
      val user = User(name = UUID.randomUUID().toString())
      this.userRepository.save(user)

      val group = Group(name = UUID.randomUUID().toString(), creator = user)
      this.groupRepository.save(group)

      val member = Member(user = user, group = group, enabled = true, hasAdminPermission = true)
      this.memberRepository.save(member)

      this.groupIdToMenuIds[group.id] = mutableListOf()
      for (itr2 in 1..3) {
        val menu = Menu(
            name = UUID.randomUUID().toString(),
            price = Random.nextInt(1000, 10000),
            group = group
        )
        this.menuRepository.save(menu)
        this.groupIdToMenuIds[group.id]!!.add(menu.id)
      }
    }

    val notEnrolledUser = User(name = UUID.randomUUID().toString())
    this.userRepository.save(notEnrolledUser)
    this.notEnrolledUserId = notEnrolledUser.id
  }

  @Test
  fun `Get menus`() {
    val groups = this.groupRepository.findAll()
    for (group in groups) {
      val creatorId = group.creator.id
      val menuIds = this.menuService.getMenuList(
          userId = creatorId,
          groupId = group.id
      ).map { it.id }

      assertThat(
          "Group returns wrong menu items",
          menuIds,
          hasItems(*this.groupIdToMenuIds[group.id]!!.toTypedArray())
      )

      assertThrows<PermissionDeniedException>("User that not enrolled to this group gets menu items") {
        this.menuService.getMenuList(
            userId = this.notEnrolledUserId,
            groupId = group.id
        )
      }
    }
  }

  @Test
  fun `Add and update menu`() {
    val groups = this.groupRepository.findAll()
    for (group in groups) {
      val creatorId = group.creator.id
      val name = UUID.randomUUID().toString()
      val price = Random.nextInt(1000, 10000)

      val newMenuId = this.menuService.addMenu(
          userId = creatorId,
          groupId = group.id,
          name = name,
          price = price
      )

      val createdMenuItem = MenuListItemDto(
          id = newMenuId,
          name = name,
          price = price
      )

      val menuItems = this.menuService.getMenuList(
          userId = creatorId,
          groupId = group.id
      )

      assertThat(
          "Created menu does not exist",
          menuItems,
          hasItem(createdMenuItem)
      )

      val nextPrice = Random.nextInt(1000, 10000)
      val nextEnabled = Random.nextBoolean()
      this.menuService.updateMenu(
          userId = creatorId,
          menuId = newMenuId,
          price = nextPrice,
          enabled = nextEnabled
      )

      val updatedMenuItem = MenuListItemDto(
          id = newMenuId,
          name = name,
          price = nextPrice,
          enabled = nextEnabled
      )

      val menuItemsThatContainUpdatedOne = this.menuService.getMenuList(
          userId = creatorId,
          groupId = group.id
      )

      assertThat(
          "Updated menu does not exist",
          menuItemsThatContainUpdatedOne,
          hasItem(updatedMenuItem)
      )
    }

  }

  @AfterAll
  fun tearDown() {
    this.menuRepository.deleteAll()
    this.memberRepository.deleteAll()
    this.groupRepository.deleteAll()
    this.userRepository.deleteAll()
  }
}