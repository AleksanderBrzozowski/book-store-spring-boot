package com.brzozowski.domain.category.service

import com.brzozowski.domain.category.dto.CategoryDto
import com.brzozowski.domain.category.dto.SubcategoryDto
import com.brzozowski.domain.category.entity.Category
import com.brzozowski.domain.category.entity.Subcategory
import com.brzozowski.domain.category.repository.CategoryRepository
import com.brzozowski.domain.category.repository.SubcategoryRepository
import com.brzozowski.presentation.common.exception.BusinessException
import com.brzozowski.util.ifPresent
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * @author Aleksander Brzozowski
 */

@Service
class CategoryService(private val categoryRepository: CategoryRepository,
                      private val subcategoryRepository: SubcategoryRepository) {

    fun findAllCategories(): List<CategoryDto> {
        return categoryRepository.findAll()
                .map { CategoryDto.parse(it) }
    }

    fun findAllSubcategories(categoryId: Int): List<SubcategoryDto> {
        return subcategoryRepository.findByCategoryId(categoryId)
                .map { SubcategoryDto.parse(it) }
    }

    fun addCategory(categoryName: String): CategoryDto {
        categoryRepository.findCategoryByName(categoryName)
                .ifPresent { throw CategoryAlreadyExistException() }

        return Category(name = categoryName).let {
            categoryRepository.save(it)
                    .let { CategoryDto.parse(it) }
        }
    }

    fun addSubcategory(subcategoryName: String, categoryId: Int): SubcategoryDto {
        val category = categoryRepository.findOne(categoryId) ?: throw CategoryNotFoundException()

        subcategoryRepository.findByCategoryId(categoryId)
                .find { it.name.equals(subcategoryName, ignoreCase = true) }
                ?.apply { throw SubcategoryAlreadyExistException() }

        return subcategoryRepository.save(Subcategory(name = subcategoryName, category = category))
                .let { SubcategoryDto(name = it.name, id = it.id) }
    }

    @Transactional
    fun updateCategory(categoryId: Int, categoryName: String): CategoryDto {
        return categoryRepository.findOne(categoryId)
                ?.apply { this.name = categoryName }
                ?.let { CategoryDto.parse(it) }
                ?: throw CategoryNotFoundException()
    }

    @Transactional
    fun updateSubcategory(subcategoryName: String, subcategoryId: Int): SubcategoryDto {
        return subcategoryRepository.findOne(subcategoryId)
                ?.apply { this.name = subcategoryName }
                ?.let { SubcategoryDto.parse(it) }
                ?: throw SubcategoryNotFoundException()
    }
}

class CategoryNotFoundException : BusinessException("error.category.notFound")
class SubcategoryNotFoundException : BusinessException("error.subcategory.notFound")
class CategoryAlreadyExistException : BusinessException("error.category.alreadyExist")
class SubcategoryAlreadyExistException : BusinessException("error.subcategory.alreadyExist")
