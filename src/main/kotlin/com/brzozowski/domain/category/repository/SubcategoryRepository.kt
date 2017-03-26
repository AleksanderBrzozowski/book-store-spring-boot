package com.brzozowski.domain.category.repository

import com.brzozowski.domain.category.entity.Subcategory
import org.springframework.data.jpa.repository.JpaRepository

/**
 * @author Aleksander Brzozowski
 */
interface SubcategoryRepository : JpaRepository<Subcategory, Int> {
    fun findByCategoryId(categoryId: Int): List<Subcategory>
}
