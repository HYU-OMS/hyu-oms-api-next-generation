package com.hyu_oms.restapi.v5.services

import com.hyu_oms.restapi.v5.dtos.menu.MenuListItemDto
import com.hyu_oms.restapi.v5.entities.Menu
import com.hyu_oms.restapi.v5.entities.User
import com.hyu_oms.restapi.v5.exceptions.GroupNotFoundException
import com.hyu_oms.restapi.v5.exceptions.PermissionDeniedException
import com.hyu_oms.restapi.v5.exceptions.UserNotFoundException
import com.hyu_oms.restapi.v5.repositories.GroupRepository
import com.hyu_oms.restapi.v5.repositories.MemberRepository
import com.hyu_oms.restapi.v5.repositories.MenuRepository
import com.hyu_oms.restapi.v5.repositories.UserRepository
import org.modelmapper.ModelMapper
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service
import java.util.stream.Collectors

@Service
class MenuService(
    private val userRepository: UserRepository,
    private val groupRepository: GroupRepository,
    private val memberRepository: MemberRepository,
    private val menuRepository: MenuRepository
) {
  private val modelMapper: ModelMapper = ModelMapper()

  private fun getUserFromContext(): User {
    val userId = SecurityContextHolder.getContext().authentication.principal.toString().toLong()
    return userRepository.findByIdAndEnabledIsTrue(userId) ?: throw UserNotFoundException()
  }

  fun getMenuList(groupId: Long): List<MenuListItemDto> {
    val user = this.getUserFromContext()
    val group = this.groupRepository.findByIdAndEnabledIsTrue(id = groupId) ?: throw GroupNotFoundException()
    val member = this.memberRepository.findByUserAndGroupAndEnabledIsTrue(
        user = user,
        group = group
    )

    if (member == null || !member.enabled) {
      throw PermissionDeniedException()
    }

    val menus = this.menuRepository.findAllByGroup(group = group)
    return menus.stream()
        .map { menu: Menu ->
          this.modelMapper.map(
              menu,
              MenuListItemDto::class.javaObjectType
          )
        }
        .collect(Collectors.toList())
  }
}