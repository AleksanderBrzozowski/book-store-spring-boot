package com.brzozowski.domain.category.dto

import com.brzozowski.domain.category.entity.Subcategory

/**
 * @author Aleksander Brzozowski
 */
class SubcategoryDto(
        val name: String,
        val id: Int
){
    companion object{
        fun parse(subCategory: Subcategory): SubcategoryDto {
            return SubcategoryDto(name = subCategory.name, id = subCategory.id)
        }
    }
}