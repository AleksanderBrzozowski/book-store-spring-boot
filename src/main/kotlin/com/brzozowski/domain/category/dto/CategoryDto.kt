package com.brzozowski.domain.category.dto

import com.brzozowski.domain.category.entity.Category

/**
 * @author Aleksander Brzozowski
 */
class CategoryDto(
        val id: Int,
        val name: String
){
    companion object{
        fun parse(category: Category): CategoryDto {
            return CategoryDto(id = category.id, name = category.name)
        }
    }
}