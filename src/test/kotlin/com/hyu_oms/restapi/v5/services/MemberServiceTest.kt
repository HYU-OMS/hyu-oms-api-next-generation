package com.hyu_oms.restapi.v5.services

import com.hyu_oms.restapi.v5.entities.Group
import com.hyu_oms.restapi.v5.entities.Member
import com.hyu_oms.restapi.v5.entities.User
import com.hyu_oms.restapi.v5.exceptions.CreatorModifyRequestedException
import com.hyu_oms.restapi.v5.exceptions.PermissionDeniedException
import com.hyu_oms.restapi.v5.repositories.GroupRepository
import com.hyu_oms.restapi.v5.repositories.MemberRepository
import com.hyu_oms.restapi.v5.repositories.UserRepository
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.junit.jupiter.api.*
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit.jupiter.SpringExtension
import kotlin.random.Random

@ExtendWith(SpringExtension::class)
@DataJpaTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles(value = ["test"])
class MemberServiceTest {
  private lateinit var memberService: MemberService

  @Autowired
  private lateinit var userRepository: UserRepository

  @Autowired
  private lateinit var groupRepository: GroupRepository

  @Autowired
  private lateinit var memberRepository: MemberRepository

  @BeforeAll
  fun setUp() {
    this.memberService = MemberService(
        userRepository = this.userRepository,
        groupRepository = this.groupRepository,
        memberRepository = this.memberRepository
    )

    for (id1 in 1L..5L) {
      val user = User(name = "TEST_USER_${id1}")
      this.userRepository.save(user)

      val group = Group(name = "TEST_GROUP_${id1}", creator = user)
      this.groupRepository.save(group)
    }

    val users = this.userRepository.findAll()
    val groups = this.groupRepository.findAll()
    for (user in users) {
      for (group in groups) {
        val member = Member(
            user = user,
            group = group,
            enabled = true,
            hasAdminPermission = (group.creator.id == user.id)
        )
        this.memberRepository.save(member)
      }
    }

    for (id in 6L..9L) {
      val user = User(name = "TEST_USER_${id}")
      this.userRepository.save(user)
    }
  }

  @Test
  fun `Get members for each group`() {
    val groups = this.groupRepository.findAll()

    for (group in groups) {
      val members = group.members
      for (member in members) {
        assertThat(
            "Member is disabled",
            member.enabled,
            `is`(true)
        )
        assertThat(
            "Member has not correct permission",
            member.hasAdminPermission,
            `is`(member.user.id == group.creator.id)
        )
      }
    }
  }

  @Test
  fun `Add new member`() {
    // User ID 6 to 9 는 어느 그룹에도 속해 있지 않음.
    for (userId in 6L..9L) {
      for (groupId in 1L..5L) {
        val newMemberId = this.memberService.addMember(userId = 6, groupId = 1)

        val member = this.memberRepository.getOne(newMemberId)
        assertThat("Member's userId mismatch.", member.user.id, `is`(6L))
        assertThat("Member's groupId mismatch.", member.group.id, `is`(1L))
        assertThat("Member's enabled must be false.", member.enabled, `is`(false))
        assertThat("Member's admin permission must be false.", member.hasAdminPermission, `is`(false))
      }
    }
  }

  @Test
  fun `Update member's information`() {
    val members = this.memberRepository.findAll()
    for (member in members) {
      val group = member.group
      val creator = group.creator

      if (member.user == creator) {
        assertThrows<CreatorModifyRequestedException> {
          this.memberService.updateMember(
              userId = creator.id,
              memberId = member.id,
              enabled = false
          )
        }
      } else {
        val nextEnabled = Random.nextBoolean()
        val nextHasAdminPermission = Random.nextBoolean()

        this.memberService.updateMember(
            userId = creator.id,
            memberId = member.id,
            enabled = nextEnabled,
            hasAdminPermission = nextHasAdminPermission
        )

        val updatedMember = this.memberRepository.getOne(member.id)
        assertThat(
            "'enabled' status '${nextEnabled}' was requested but it does not.",
            updatedMember.enabled,
            `is`(nextEnabled)
        )
        assertThat(
            "'hasAdminPermission' status '${nextHasAdminPermission}' was requested but it does not.",
            updatedMember.hasAdminPermission,
            `is`(nextHasAdminPermission)
        )
      }
    }

    val userWithId6 = this.userRepository.getOne(6)
    val groupWithId1 = this.groupRepository.getOne(1)
    val existingMember = groupWithId1.members.last()

    val newMember = Member(user = userWithId6, group = groupWithId1, enabled = false)
    this.memberRepository.save(newMember)
    assertThrows<PermissionDeniedException> {
      this.memberService.updateMember(
          userId = userWithId6.id,
          memberId = existingMember.id,
          enabled = false
      )
    }

    newMember.enabled = true
    this.memberRepository.save(newMember)
    assertThrows<PermissionDeniedException> {
      this.memberService.updateMember(
          userId = userWithId6.id,
          memberId = existingMember.id,
          enabled = false
      )
    }

    newMember.hasAdminPermission = true
    this.memberRepository.save(newMember)
    this.memberService.updateMember(
        userId = userWithId6.id,
        memberId = existingMember.id,
        enabled = false
    )

    val updatedMember = this.memberRepository.getOne(existingMember.id)
    assertThat("'enabled' must be 'false'", updatedMember.enabled, `is`(false))
  }

//  @Test
//  fun `Delete member`() {
//
//  }

  @AfterAll
  fun tearDown() {
    this.memberRepository.deleteAll()
    this.groupRepository.deleteAll()
    this.userRepository.deleteAll()
  }
}