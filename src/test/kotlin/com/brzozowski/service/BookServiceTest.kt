package com.brzozowski.service

import com.brzozowski.TestExtensions.Companion.assertThrownBy
import com.brzozowski.domain.book.dto.BookDto
import com.brzozowski.domain.book.entity.CategorySpecial.BEST
import com.brzozowski.domain.book.service.BookService
import com.brzozowski.domain.category.dto.CategoryDto
import com.brzozowski.domain.category.dto.SubcategoryDto
import com.brzozowski.domain.category.service.CategoryService
import com.brzozowski.domain.category.service.SubcategoryNotFoundException
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.domain.PageRequest
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.transaction.annotation.Transactional

/**
 * @author Aleksander Brzozowski
 */

@Suppress("SpringKotlinAutowiredMembers")
@SpringBootTest
@RunWith(SpringRunner::class)
@Transactional
class BookServiceTest {

    @Autowired
    private lateinit var bookService: BookService

    @Autowired
    private lateinit var categoryService: CategoryService

    private val defaultIsbn = "1234567890123"
    private val defaultTitle = "title"

    private var defaultSubcategoryId = 0
    private var defaultCategoryId = 0
    private val defaultCategoryName = "default category"
    private val defaultSubcategoryName = "default subcategory"

    @Test
    fun `given not existing book when add book it should add book`() {
        //given
        defaultSetupSubcategoryAndCategory()

        //when
        val bookDto = bookService.addBook(isbn = defaultIsbn, title = defaultTitle, subcategoryId = defaultSubcategoryId, specialCategories = setOf(BEST))

        //then
        val expectedBook = BookDto(isbn = defaultIsbn, title = defaultTitle, specialCategories = listOf(BEST),
                subcategory = SubcategoryDto(name = defaultSubcategoryName, id = defaultSubcategoryId))
        assertThat(bookDto).isEqualTo(expectedBook)
    }

    @Test
    fun `given existing book when find book it should return that book`() {
        //given
        defaultSetupSubcategoryAndCategory()
        bookService.addBook(isbn = defaultIsbn, title = defaultTitle, subcategoryId = defaultSubcategoryId)

        //when
        val bookDto = bookService.findBook(defaultIsbn)

        //then
        val expectedBook = BookDto(isbn = defaultIsbn, title = defaultTitle, specialCategories = emptyList(),
                subcategory = SubcategoryDto(name = defaultSubcategoryName, id = defaultSubcategoryId),
                category = CategoryDto(name = defaultCategoryName, id = defaultCategoryId))
        assertThat(bookDto).isEqualTo(expectedBook)
    }

    @Test
    fun `given existing book when find books it should return that book`() {
        //given
        defaultSetupSubcategoryAndCategory()
        bookService.addBook(isbn = defaultIsbn, title = defaultTitle, subcategoryId = defaultSubcategoryId)

        //when
        val books = bookService.findBooks(subcategoryId = defaultSubcategoryId, pageable = PageRequest(0, 20))
                .content

        //then
        val expectedBook = BookDto(isbn = defaultIsbn, title = defaultTitle, specialCategories = emptyList(),
                subcategory = SubcategoryDto(name = defaultSubcategoryName, id = defaultSubcategoryId),
                category = CategoryDto(name = defaultCategoryName, id = defaultCategoryId))
        assertThat(books).contains(expectedBook)
    }

    @Test
    fun `given not existing subcategory when add book it should throw exception`() {
        //when && then
        assertThrownBy<SubcategoryNotFoundException> { bookService.addBook(isbn = defaultIsbn, title = defaultTitle, subcategoryId = defaultSubcategoryId) }
    }

    @Test
    fun `given existing title when add book it should throw exception`() {
        //given
        defaultSetupSubcategoryAndCategory()
        bookService.addBook(isbn = defaultIsbn, title = defaultTitle, subcategoryId = defaultSubcategoryId)

        //when && then
        val findBooks = bookService.findBooks(defaultSubcategoryId, PageRequest(0, 20))
//        assertThrownBy<BookTitleAlreadyExistException> { bookService.addBook(isbn = "", title = defaultTitle, subcategoryId = defaultSubcategoryId) }
    }

    @Test
    fun `given no books when find books it should return empty list`() {
        //when
        defaultSetupSubcategoryAndCategory()
        val books = bookService.findBooks(defaultSubcategoryId, PageRequest(0, 20))

        //then
        assertThat(books).isEmpty()
    }

    @Test
    fun `given not existing subcategory when find books it should throw exception`() {
        //when && then
        assertThrownBy<SubcategoryNotFoundException> { bookService.findBooks(defaultSubcategoryId, PageRequest(0, 20)) }
    }

    private fun defaultSetupSubcategoryAndCategory() {
        defaultSubcategoryId = categoryService.addCategory(defaultCategoryName)
                .apply { defaultCategoryId = this.id }
                .let { categoryService.addSubcategory(subcategoryName = defaultSubcategoryName, categoryId = it.id) }
                .id
    }
}