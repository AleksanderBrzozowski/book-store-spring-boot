package com.brzozowski.domain.book.dto

import com.brzozowski.domain.book.entity.Book
import com.brzozowski.domain.book.entity.CategorySpecial
import com.brzozowski.domain.category.dto.CategoryDto
import com.brzozowski.domain.category.dto.SubcategoryDto

/**
 * @author Aleksander Brzozowski
 */
data class BookDto(
        val isbn: String,
        val title: String,
        val specialCategories: List<CategorySpecial>,
        val subcategory: SubcategoryDto? = null,
        val category: CategoryDto? = null
) {
    companion object {
        fun parse(book: Book, subcategory: SubcategoryDto? = null, category: CategoryDto? = null): BookDto {
            return BookDto(isbn = book.isbn, title = book.title, specialCategories = book.specialCategories.toList(),
                    subcategory = subcategory, category = category)
        }
    }
}

