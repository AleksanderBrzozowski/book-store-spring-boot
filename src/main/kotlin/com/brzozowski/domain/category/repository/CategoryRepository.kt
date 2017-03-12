package com.brzozowski.domain.category.repository

import com.brzozowski.domain.category.entity.Category
import org.springframework.data.jpa.repository.JpaRepository

/**
 * @author Aleksander Brzozowski
 */
interface CategoryRepository : JpaRepository<Category, Int> {
    fun findCategoryByName(name: String?): Category?
}