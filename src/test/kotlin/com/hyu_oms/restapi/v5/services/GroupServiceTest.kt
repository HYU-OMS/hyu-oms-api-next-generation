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

  @BeforeAll
  fun setUp() {
    this.groupService = GroupService(
        groupRepository = this.groupRepository,
        memberRepository = this.memberRepository
    )

    for (id in 1L..3L) {
      val user = User(name = "TEST_USER_${id}")
      this.userRepository.save(user)

      val group = Group(name = "TEST_GROUP_${id}", creator = user)
      this.groupRepository.save(group)

      val member = Member(user = user, group = group, enabled = true, hasAdminPermission = true)
      this.memberRepository.save(member)
    }
  }

  @Test
  fun `Get enrolled groups`() {
    val targetUserId = 1L

    val user = this.userRepository.getOne(targetUserId)
    val response = this.groupService.getEnrolledList(user = user)

    assertThat(response.totalElements, `is`(1L))
    assertThat(response.totalPages, `is`(1))

    val groupIds = response.contents.map { it.id }
    assertThat(groupIds, hasItems(1L))
  }

  @Test
  fun `Get not enrolled and register allowed groups but there is no group with register allowed`() {
    val targetUserId = 1L

    val user = this.userRepository.getOne(targetUserId)
    val response = this.groupService.getNotEnrolledAndRegisterAllowedList(user = user)

    assertThat(response.totalElements, `is`(0L))
    assertThat(response.totalPages, `is`(0))

    val groupIds = response.contents.map { it.id }
    assertThat(groupIds.size, `is`(0))
  }

  @Test
  fun `Get not enrolled and register allowed groups but there are one group with register allowed`() {
    val targetUserId = 1L
    val targetGroupId = 2L

    val group = this.groupRepository.getOne(targetGroupId)
    group.allowRegister = true
    this.groupRepository.save(group)

    val user = this.userRepository.getOne(targetUserId)
    val response = this.groupService.getNotEnrolledAndRegisterAllowedList(user = user)

    assertThat(response.totalElements, `is`(1L))
    assertThat(response.totalPages, `is`(1))

    val groupIds = response.contents.map { it.id }
    assertThat(groupIds, hasItems(targetGroupId))
  }

  @Test
  fun `Add new group`() {
    val groupCount = this.groupRepository.count()

    val user = this.userRepository.getOne(1)
    val newGroupName = "TEST_GROUP_${groupCount + 1}"

    val response = this.groupService.addNewGroup(user = user, name = newGroupName)

    val newGroup = this.groupRepository.getOne(response.newGroupId)
    assertThat(newGroup.name, `is`(newGroupName))
    assertThat(newGroup.creator, `is`(user))
    assertThat(newGroup.enabled, `is`(true))
    assertThat(newGroup.allowRegister, `is`(false))

    val newMember = this.memberRepository.findByUserAndGroupAndEnabledIsTrue(user = user, group = newGroup)
    assertThat(newMember, notNullValue())
    assertThat(newMember!!.hasAdminPermission, `is`(true))
  }

  @Test
  fun `Update with creator`() {
    val targetGroupId = 1L

    val group = this.groupRepository.getOne(targetGroupId)
    val creator = group.creator

    val updatedGroupName = "UPDATED_GROUP_${group.id}"
    val response = this.groupService.updateGroup(
        user = creator,
        groupId = group.id,
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
    val targetGroupId = 1L
    val targetUserId = 2L

    val group = this.groupRepository.getOne(targetGroupId)
    val user = this.userRepository.getOne(targetUserId)

    assertThrows<PermissionDeniedException> {
      this.groupService.updateGroup(
          user = user,
          groupId = group.id
      )
    }
  }

  @Test
  fun `Attempt to update non-existing group`() {
    val targetUserId = 1L
    val targetGroupId = 999L

    val user = this.userRepository.getOne(targetUserId)

    assertThrows<GroupNotFoundException> {
      this.groupService.updateGroup(
          user = user,
          groupId = targetGroupId
      )
    }
  }

  @Test
  fun `Delete with creator`() {
    val targetGroupId = 1L

    val group = this.groupRepository.getOne(targetGroupId)
    val creator = group.creator

    val response = this.groupService.deleteGroup(user = creator, groupId = group.id)

    val deletedGroup = this.groupRepository.getOne(response.groupId)
    assertThat(deletedGroup.enabled, `is`(false))
    assertThat(deletedGroup.members.size, `is`(0))
  }

  @Test
  fun `Delete with non-creator`() {
    val targetGroupId = 1L
    val targetUserId = 2L

    val group = this.groupRepository.getOne(targetGroupId)
    val user = this.userRepository.getOne(targetUserId)

    assertThrows<PermissionDeniedException> {
      this.groupService.deleteGroup(
          user = user,
          groupId = group.id
      )
    }
  }

  @Test
  fun `Attempt to delete non-existing group`() {
    val targetUserId = 1L
    val targetGroupId = 999L

    val user = this.userRepository.getOne(targetUserId)

    assertThrows<GroupNotFoundException> {
      this.groupService.deleteGroup(
          user = user,
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