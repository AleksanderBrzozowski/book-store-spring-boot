package com.brzozowski.domain.book.service

import com.brzozowski.domain.book.dto.BookDto
import com.brzozowski.domain.book.entity.Book
import com.brzozowski.domain.book.entity.CategorySpecial
import com.brzozowski.domain.book.repository.BookRepository
import com.brzozowski.domain.category.dto.CategoryDto
import com.brzozowski.domain.category.dto.SubcategoryDto
import com.brzozowski.domain.category.repository.SubcategoryRepository
import com.brzozowski.domain.category.service.SubcategoryNotFoundException
import com.brzozowski.presentation.common.exception.BusinessException
import com.brzozowski.util.ifPresent
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * @author Aleksander Brzozowski
 */

@Service
class BookService(private val bookRepository: BookRepository,
                  private val subcategoryRepository: SubcategoryRepository) {

    @Transactional
    fun addBook(isbn: String, title: String, subcategoryId: Int, specialCategories: Set<CategorySpecial> = emptySet()): BookDto {
        validateUniqueBookParams(isbn = isbn, title = title)

        val subcategory = findOneSubcategory(subcategoryId)

        return Book(isbn = isbn, title = title, subcategory = subcategory, specialCategories = specialCategories.toMutableSet())
                .let { bookRepository.save(it) }
                .let { BookDto.parse(book = it, subcategory = SubcategoryDto.parse(subcategory)) }
    }

    @Transactional
    fun updateBook(idIsbn: String, isbn: String, title: String, specialCategories: Set<CategorySpecial> = emptySet(),
                   subcategoryId: Int): BookDto {
        validateUniqueBookParams(isbn = isbn, title = title)

        val book = findOneBook(idIsbn)

        return book.apply {
            this.title = title; this.isbn = isbn
            this.subcategory = findOneSubcategory(subcategoryId)
        }
                .let { BookDto.parse(book = it, subcategory = SubcategoryDto.parse(it.subcategory)) }
    }

    fun findBook(isbn: String): BookDto {
        val book = findOneBook(isbn)
        return BookDto.parse(
                book = book, subcategory = SubcategoryDto.parse(book.subcategory),
                category = CategoryDto.parse(book.subcategory.category)
        )
    }

    fun findBooks(subcategoryId: Int, pageable: Pageable): Page<BookDto> {
        return findOneSubcategory(subcategoryId)
                .let { bookRepository.findAllBySubcategory(subcategory = it, pageable = pageable) }
                .let { it.map { BookDto.parse(it) } }
    }

    private fun findOneBook(idIsbn: String) = bookRepository.findOne(idIsbn) ?: throw BookNotFoundException()

    private fun findOneSubcategory(subcategoryId: Int) = subcategoryRepository.findOne(subcategoryId) ?: throw SubcategoryNotFoundException()

    private fun validateUniqueBookParams(isbn: String, title: String) {
        bookRepository.findOneByTitleIgnoreCaseOrIsbn(title = title, isbn = isbn)
                ?.ifPresent {
                    if (this.title.equals(title, ignoreCase = true)) {
                        throw BookTitleAlreadyExistException()
                    }
                    if (this.isbn == isbn) {
                        throw BookIsbnAlreadyExistException()
                    }
                }
    }
}

class BookIsbnAlreadyExistException : BusinessException("error.book.isbnAlreadyExist")
class BookTitleAlreadyExistException : BusinessException("error.book.titleAlreadyExist")
class BookNotFoundException : BusinessException("error.book.notFound")