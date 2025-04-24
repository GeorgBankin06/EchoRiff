package com.echoriff.echoriff.admin.data

import com.echoriff.echoriff.admin.domain.ChangeUserRoleState
import com.echoriff.echoriff.radio.domain.model.Category
import com.echoriff.echoriff.radio.domain.model.Radio

interface AdminRepository {
    suspend fun getAllCategories(): List<Category>
    suspend fun addRadioToCategory(categoryTitle: String, radio: Radio)
    suspend fun changeUserRole(email: String, role: String): ChangeUserRoleState
}