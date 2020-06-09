package com.hyu_oms.restapi.v5.services

import com.hyu_oms.restapi.v5.dtos.setmenu.SetMenuAddResponseDto
import com.hyu_oms.restapi.v5.dtos.setmenu.SetMenuContentItemDto
import com.hyu_oms.restapi.v5.dtos.setmenu.SetMenuListItemDto
import com.hyu_oms.restapi.v5.dtos.setmenu.SetMenuUpdateResponseDto
import com.hyu_oms.restapi.v5.entities.SetMenu
import com.hyu_oms.restapi.v5.entities.SetMenuContent
import com.hyu_oms.restapi.v5.entities.User
import com.hyu_oms.restapi.v5.exceptions.GroupNotFoundException
import com.hyu_oms.restapi.v5.exceptions.PermissionDeniedException
import com.hyu_oms.restapi.v5.exceptions.SetMenuNotFoundException
import com.hyu_oms.restapi.v5.exceptions.UserNotFoundException
import com.hyu_oms.restapi.v5.repositories.*
import org.modelmapper.ModelMapper
import org.springframework.data.repository.findByIdOrNull
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.stream.Collectors

@Service
class SetMenuService(
    private val userRepository: UserRepository,
    private val groupRepository: GroupRepository,
    private val memberRepository: MemberRepository,
    private val menuRepository: MenuRepository,
    private val setMenuRepository: SetMenuRepository,
    private val setMenuContentRepository: SetMenuContentRepository
) {
  private val modelMapper: ModelMapper = ModelMapper()

  private fun getUserFromContext(): User {
    val userId = SecurityContextHolder.getContext().authentication.principal.toString().toLong()
    return userRepository.findByIdAndEnabledIsTrue(userId) ?: throw UserNotFoundException()
  }

  @Transactional(readOnly = true)
  fun getSetMenuList(groupId: Long): List<SetMenuListItemDto> {
    val user = this.getUserFromContext()
    val group = this.groupRepository.findByIdAndEnabledIsTrue(id = groupId) ?: throw GroupNotFoundException()
    val member = this.memberRepository.findByUserAndGroupAndEnabledIsTrue(
        user = user,
        group = group
    )

    if (member == null || !member.enabled) {
      throw PermissionDeniedException()
    }

    val setMenus = this.setMenuRepository.findAllByGroup(group = group)
    return setMenus.stream()
        .map { setMenu: SetMenu ->
          this.modelMapper.map(
              setMenu,
              SetMenuListItemDto::class.javaObjectType
          )
        }
        .collect(Collectors.toList())
  }

  @Transactional(readOnly = false)
  fun addSetMenu(
      groupId: Long,
      name: String,
      price: Int,
      menuContents: List<SetMenuContentItemDto>
  ): SetMenuAddResponseDto {
    val user = this.getUserFromContext()
    val group = this.groupRepository.findByIdAndEnabledIsTrue(id = groupId) ?: throw GroupNotFoundException()
    val member = this.memberRepository.findByUserAndGroupAndEnabledIsTrue(user = user, group = group)
    if (member == null || !member.hasAdminPermission) {
      throw PermissionDeniedException()
    }

    val menuIds = mutableMapOf<Long, Int>()
    for (menuContent in menuContents) {
      menuIds[menuContent.menuId!!] = menuContent.amount!!
    }

    val menuCount = this.menuRepository.countDistinctByIdIn(ids = menuIds.keys.toList())
    if (menuCount != menuIds.size.toLong()) {
      // TODO: 메뉴 개수 불일치용 exception 따로 만들 것.
      throw PermissionDeniedException()
    }

    val newSetMenu = SetMenu(
        name = name,
        price = price,
        group = group
    )
    this.setMenuRepository.save(newSetMenu)

    val newSetMenuContents = menuIds.map { (menuId, amount) ->
      SetMenuContent(
          setMenu = newSetMenu,
          menu = this.menuRepository.getOne(menuId), // TODO: 이거 id 만 쓰는데 또 조회가 되나 확인해야 함.
          amount = amount
      )
    }
    this.setMenuContentRepository.saveAll(newSetMenuContents)

    return SetMenuAddResponseDto(newSetMenuId = newSetMenu.id)
  }

  @Transactional(readOnly = false)
  fun updateSetMenu(setMenuId: Long, price: Int?, enabled: Boolean?): SetMenuUpdateResponseDto {
    val user = getUserFromContext()
    val setMenu = this.setMenuRepository.findByIdOrNull(id = setMenuId) ?: throw SetMenuNotFoundException()
    val member = this.memberRepository.findByUserAndGroupAndEnabledIsTrue(user = user, group = setMenu.group)
    if (member == null || !member.hasAdminPermission) {
      throw PermissionDeniedException()
    }

    if (price != null) {
      setMenu.price = price
    }

    if (enabled != null) {
      setMenu.enabled = enabled
    }

    this.setMenuRepository.save(setMenu)

    return SetMenuUpdateResponseDto(setMenuId = setMenuId)
  }
}