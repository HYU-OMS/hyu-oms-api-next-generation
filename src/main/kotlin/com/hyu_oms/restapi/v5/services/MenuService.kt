package com.hyu_oms.restapi.v5.services

import com.hyu_oms.restapi.v5.dtos.menu.MenuListItemDto
import com.hyu_oms.restapi.v5.entities.Menu
import com.hyu_oms.restapi.v5.exceptions.GroupNotFoundException
import com.hyu_oms.restapi.v5.exceptions.MenuNotFoundException
import com.hyu_oms.restapi.v5.exceptions.PermissionDeniedException
import com.hyu_oms.restapi.v5.exceptions.UserNotFoundException
import com.hyu_oms.restapi.v5.repositories.GroupRepository
import com.hyu_oms.restapi.v5.repositories.MemberRepository
import com.hyu_oms.restapi.v5.repositories.MenuRepository
import com.hyu_oms.restapi.v5.repositories.UserRepository
import org.modelmapper.ModelMapper
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.stream.Collectors

@Service
class MenuService(
    private val userRepository: UserRepository,
    private val groupRepository: GroupRepository,
    private val memberRepository: MemberRepository,
    private val menuRepository: MenuRepository
) {
  private val modelMapper: ModelMapper = ModelMapper()

  @Transactional(readOnly = true)
  fun getMenuList(userId: Long, groupId: Long): List<MenuListItemDto> {
    val user = this.userRepository.findByIdAndEnabledIsTrue(userId) ?: throw UserNotFoundException()
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

  @Transactional(readOnly = false)
  fun addMenu(userId: Long, groupId: Long, name: String, price: Int): Long {
    val user = this.userRepository.findByIdAndEnabledIsTrue(userId) ?: throw UserNotFoundException()
    val group = this.groupRepository.findByIdAndEnabledIsTrue(id = groupId) ?: throw GroupNotFoundException()
    val member = this.memberRepository.findByUserAndGroupAndEnabledIsTrue(user = user, group = group)
    if (member == null || !member.hasAdminPermission) {
      throw PermissionDeniedException()
    }

    val newMenu = Menu(
        name = name,
        price = price,
        group = group
    )
    this.menuRepository.save(newMenu)

    return newMenu.id
  }

  @Transactional(readOnly = false)
  fun updateMenu(userId: Long, menuId: Long, price: Int?, enabled: Boolean?) {
    val user = this.userRepository.findByIdAndEnabledIsTrue(userId) ?: throw UserNotFoundException()
    val menu = this.menuRepository.findByIdOrNull(id = menuId) ?: throw MenuNotFoundException()
    val member = this.memberRepository.findByUserAndGroupAndEnabledIsTrue(user = user, group = menu.group)
    if (member == null || !member.hasAdminPermission) {
      throw PermissionDeniedException()
    }

    if (price != null) {
      menu.price = price
    }

    if (enabled != null) {
      menu.enabled = enabled
    }

    this.menuRepository.save(menu)
  }
}