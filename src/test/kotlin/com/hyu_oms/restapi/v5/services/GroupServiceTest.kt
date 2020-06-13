package com.hyu_oms.restapi.v5.services

import com.hyu_oms.restapi.v5.entities.Group
import com.hyu_oms.restapi.v5.entities.Member
import com.hyu_oms.restapi.v5.entities.User
import com.hyu_oms.restapi.v5.exceptions.GroupNotFoundException
import com.hyu_oms.restapi.v5.exceptions.PermissionDeniedException
import com.hyu_oms.restapi.v5.repositories.GroupRepository
import com.hyu_oms.restapi.v5.repositories.MemberRepository
import com.hyu_oms.restapi.v5.repositories.UserRepository
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.*
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.util.*

@ExtendWith(SpringExtension::class)
@DataJpaTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles(value = ["test"])
class GroupServiceTest {
  private lateinit var groupService: GroupService

  @Autowired
  private lateinit var userRepository: UserRepository

  @Autowired
  private lateinit var groupRepository: GroupRepository

  @Autowired
  private lateinit var memberRepository: MemberRepository

  private val initialUserIds = mutableListOf<Long>()
  private val initialGroupIds = mutableListOf<Long>()
  private val userIdToGroupIdMap = mutableMapOf<Long, MutableList<Long>>()

  @BeforeAll
  fun setUp() {
    this.groupService = GroupService(
        userRepository = this.userRepository,
        groupRepository = this.groupRepository,
        memberRepository = this.memberRepository
    )

    for (itr in 1..3) {
      val user = User(name = UUID.randomUUID().toString())
      this.userRepository.save(user)
      this.initialUserIds.add(user.id)

      val group = Group(name = UUID.randomUUID().toString(), creator = user)
      this.groupRepository.save(group)
      this.initialGroupIds.add(group.id)

      val member = Member(
          user = user,
          group = group,
          enabled = true,
          hasAdminPermission = true
      )
      this.memberRepository.save(member)

      this.userIdToGroupIdMap[user.id] = mutableListOf(group.id)
    }
  }

  @Test
  fun `Get enrolled groups`() {
    val targetUserId = this.initialUserIds.random()

    var currentPage = 0
    val groupIds = mutableListOf<Long>()
    while (true) {
      val response = this.groupService.getEnrolledList(
          userId = targetUserId,
          page = currentPage
      )
      val currentGroupIds = response.contents.map { it.id }
      if (currentGroupIds.isEmpty()) {
        break
      }

      groupIds.addAll(currentGroupIds)
      currentPage += 1
    }

    assertThat(
        "Failed to get enrolled groups",
        groupIds,
        hasItems(*this.userIdToGroupIdMap[targetUserId]!!.toTypedArray())
    )
  }

  @Test
  fun `Get not enrolled and register allowed groups`() {
    val targetUserId = this.initialUserIds.random()

    var currentPage = 0
    val groupIds = mutableListOf<Long>()
    while (true) {
      val response = this.groupService.getNotEnrolledAndRegisterAllowedList(
          userId = targetUserId,
          page = currentPage
      )
      val currentGroupIds = response.contents.map { it.id }
      if (currentGroupIds.isEmpty()) {
        break
      }

      groupIds.addAll(currentGroupIds)
      currentPage += 1
    }

    assertThat(
        "There is no other register allowed groups but size is not 0.",
        groupIds.size,
        `is`(0)
    )

    val groups = this.groupRepository.findAll()
    for (group in groups) {
      group.allowRegister = true
    }
    this.groupRepository.saveAll(groups)

    groupIds.clear()
    currentPage = 0

    while (true) {
      val response = this.groupService.getNotEnrolledAndRegisterAllowedList(
          userId = targetUserId,
          page = currentPage
      )
      val currentGroupIds = response.contents.map { it.id }
      if (currentGroupIds.isEmpty()) {
        break
      }

      groupIds.addAll(currentGroupIds)
      currentPage += 1
    }

    assertThat(
        "There is 2 register allowed groups but size is not 2.",
        groupIds.size,
        `is`(2)
    )
  }

  @Test
  fun `Add new group`() {
    val targetUserId = this.initialUserIds.random()

    val newGroupName = UUID.randomUUID().toString()

    val response = this.groupService.addNewGroup(userId = targetUserId, name = newGroupName)

    val targetUser = this.userRepository.getOne(targetUserId)
    val newGroup = this.groupRepository.getOne(response.newGroupId)
    assertThat(newGroup.name, `is`(newGroupName))
    assertThat(newGroup.creator, `is`(targetUser))
    assertThat(newGroup.enabled, `is`(true))
    assertThat(newGroup.allowRegister, `is`(false))

    val newMember = this.memberRepository.findByUserAndGroupAndEnabledIsTrue(user = targetUser, group = newGroup)
    assertThat(newMember, notNullValue())
    assertThat(newMember!!.hasAdminPermission, `is`(true))
  }

  @Test
  fun `Update with creator`() {
    val targetGroupId = this.initialGroupIds.random()

    val group = this.groupRepository.getOne(targetGroupId)
    val creator = group.creator

    val updatedGroupName = UUID.randomUUID().toString()
    val response = this.groupService.updateGroup(
        userId = creator.id,
        groupId = targetGroupId,
        name = updatedGroupName,
        allowRegister = true
    )

    val updatedGroup = this.groupRepository.getOne(targetGroupId)
    assertThat(response.groupId, `is`(updatedGroup.id))
    assertThat(updatedGroup.name, `is`(updatedGroupName))
    assertThat(updatedGroup.allowRegister, `is`(true))
  }

  @Test
  fun `Update with non-creator`() {
    val targetGroupId = this.initialGroupIds.random()
    val targetGroup = this.groupRepository.getOne(targetGroupId)

    var targetUserId: Long
    do {
      targetUserId = this.initialUserIds.random()
    } while (targetGroup.creator.id == targetUserId)

    assertThrows<PermissionDeniedException> {
      this.groupService.updateGroup(
          userId = targetUserId,
          groupId = targetGroupId
      )
    }
  }

  @Test
  fun `Attempt to update non-existing group`() {
    val targetUserId = this.initialUserIds.random()
    val targetGroupId = 0L

    assertThrows<GroupNotFoundException> {
      this.groupService.updateGroup(
          userId = targetUserId,
          groupId = targetGroupId
      )
    }
  }

  @Test
  fun `Delete with creator`() {
    val targetGroupId = this.initialGroupIds.random()

    val group = this.groupRepository.getOne(targetGroupId)
    val creator = group.creator

    val response = this.groupService.deleteGroup(userId = creator.id, groupId = group.id)

    val deletedGroup = this.groupRepository.getOne(response.groupId)
    assertThat(deletedGroup.enabled, `is`(false))
    assertThat(deletedGroup.members.size, `is`(0))
  }

  @Test
  fun `Delete with non-creator`() {
    val targetGroupId = this.initialGroupIds.random()
    val targetGroup = this.groupRepository.getOne(targetGroupId)

    var targetUserId: Long
    do {
      targetUserId = this.initialUserIds.random()
    } while (targetGroup.creator.id == targetUserId)

    assertThrows<PermissionDeniedException> {
      this.groupService.deleteGroup(
          userId = targetUserId,
          groupId = targetGroupId
      )
    }
  }

  @Test
  fun `Attempt to delete non-existing group`() {
    val targetUserId = this.initialUserIds.random()
    val targetGroupId = 0L

    assertThrows<GroupNotFoundException> {
      this.groupService.deleteGroup(
          userId = targetUserId,
          groupId = targetGroupId
      )
    }
  }

  @AfterAll
  fun tearDown() {
    this.memberRepository.deleteAll()
    this.groupRepository.deleteAll()
    this.userRepository.deleteAll()
  }
}