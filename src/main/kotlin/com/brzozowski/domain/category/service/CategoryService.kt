package com.brzozowski.domain.category.service

import com.brzozowski.domain.category.dto.CategoryDto
import com.brzozowski.domain.category.dto.SubcategoryDto
import com.brzozowski.domain.category.entity.Category
import com.brzozowski.domain.category.entity.Subcategory
import com.brzozowski.domain.category.repository.CategoryRepository
import com.brzozowski.domain.category.repository.SubcategoryRepository
import com.brzozowski.presentation.category.model.CreateCategoryModel
import com.brzozowski.presentation.category.model.CreateSubcategoryModel
import com.brzozowski.presentation.category.model.UpdateCategoryModel
import com.brzozowski.presentation.category.model.UpdateSubcategoryModel
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
                ?.map { SubcategoryDto.parse(it) }
                ?: throw SubcategoryNotFoundException()
    }

    fun addCategory(model: CreateCategoryModel): CategoryDto {
        categoryRepository.findCategoryByName(model.name)
                .ifPresent { throw CategoryAlreadyExistException() }

        return Category(name = model.name!!).let {
            categoryRepository.save(it).let {
                CategoryDto.parse(it)
            }
        }
    }

    @Transactional
    fun addSubcategory(model: CreateSubcategoryModel): SubcategoryDto {
        return categoryRepository.findOne(model.categoryId)
                ?.let {
                    it.subcategories
                            .find { it.name.equals(other = model.name, ignoreCase = true) }
                            .ifPresent { throw SubcategoryAlreadyExistException() }

                    subcategoryRepository.save(Subcategory(name = model.name!!, category = it)).let {
                        SubcategoryDto.parse(it)
                    }
                }
                ?: throw CategoryNotFoundException()
    }

    @Transactional
    fun updateCategory(model: UpdateCategoryModel, categoryId: Int): CategoryDto {
        return categoryRepository.findOne(categoryId)
                ?.apply { this.name = model.name!! }
                ?.let { CategoryDto.parse(it) }
                ?: throw CategoryNotFoundException()
    }

    @Transactional
    fun updateSubcategory(model: UpdateSubcategoryModel, subcategoryId: Int): SubcategoryDto {
        return subcategoryRepository.findOne(subcategoryId)
                ?.apply { this.name = model.name!! }
                ?.let { SubcategoryDto.parse(it) }
                ?: throw SubcategoryNotFoundException()
    }
}

class CategoryNotFoundException : BusinessException("error.category.notFound")
class SubcategoryNotFoundException : BusinessException("error.subcategory.notFound")
class CategoryAlreadyExistException : BusinessException("error.category.alreadyExist")
class SubcategoryAlreadyExistException : BusinessException("error.subcategory.alreadyExist")
