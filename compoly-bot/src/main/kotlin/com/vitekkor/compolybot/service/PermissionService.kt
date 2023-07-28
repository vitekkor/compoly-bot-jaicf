package com.vitekkor.compolybot.service

import com.vitekkor.compolybot.model.Channel
import com.vitekkor.compolybot.model.Role
import com.vitekkor.compolybot.model.User

interface PermissionService {
    fun canUseCommand(commandName: String, user: User): Boolean

    fun updateRole(user: User, channel: Channel): User

    fun setRole(user: User, newRole: Role)
}
