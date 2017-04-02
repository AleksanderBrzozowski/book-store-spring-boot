package com.brzozowski.domain.book.repository

import com.brzozowski.domain.book.entity.Book
import com.brzozowski.domain.category.entity.Subcategory
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.PagingAndSortingRepository

/**
 * @author Aleksander Brzozowski
 */
interface BookRepository : PagingAndSortingRepository<Book, String> {
    fun findOneByTitleIgnoreCaseOrIsbn(title: String, isbn: String): Book?
    fun findAllBySubcategory(subcategory: Subcategory, pageable: Pageable): Page<Book>
}