package com.restaurant.be.category.domain.service

import com.restaurant.be.category.presentation.controller.dto.GetCategoriesResponse
import com.restaurant.be.category.presentation.controller.dto.common.CategoryDto
import com.restaurant.be.category.repository.CategoryRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GetCategoryService(
    private val categoryRepository: CategoryRepository
) {
    @Transactional(readOnly = true)
    fun getCategories(): GetCategoriesResponse {
        val categories = categoryRepository.findAll()

        return GetCategoriesResponse(
            categories = categories.map { CategoryDto(it.id ?: 0, it.name) }
        )
    }

    @Transactional(readOnly = true)
    fun getMainCategories(categoriesStr: String): String {
        val categoryNames = categoriesStr.split(",").map { it.trim() }.filter { it.isNotEmpty() }

        val allCategories = categoryRepository.findAll()

        val categoryMap = allCategories.associateBy { it.name }

        val mainCategories = categoryNames.mapNotNull { subCategoryName ->
            val category = categoryMap[subCategoryName]

            when {
                category?.parent != null -> category.parent?.name
                category != null -> category.name
                else -> null
            }
        }.distinct()

        return mainCategories.joinToString(", ")
    }
}
