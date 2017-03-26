package com.brzozowski.service

import com.brzozowski.domain.category.dto.CategoryDto
import com.brzozowski.domain.category.dto.SubcategoryDto
import com.brzozowski.domain.category.service.*
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.hamcrest.Matchers.*
import org.junit.Assert.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.transaction.annotation.Transactional


/**
 * @author Aleksander Brzozowski
 */

@Suppress("SpringKotlinAutowiredMembers")
@SpringBootTest
@RunWith(SpringRunner::class)
@Transactional
class CategoryServiceTest {

    @Autowired
    private lateinit var categoryService: CategoryService

    @Test
    fun `given existing category when add category expect exception`() {
        //given
        val categoryName = "category name"
        categoryService.addCategory(categoryName = categoryName)

        //when && then
        assertThatThrownBy { categoryService.addCategory(categoryName = categoryName) }
                .isInstanceOf(CategoryAlreadyExistException::class.java)
    }

    @Test
    fun `given not existing category when add category expect save category`() {
        //given
        val categoryName = "category name"

        //when
        val categoryDto = categoryService.addCategory(categoryName = categoryName)

        //then
        val categories = categoryService.findAllCategories()
        assertThat(categories, contains(samePropertyValuesAs(categoryDto)))
    }

    @Test
    fun `given subcategory with not existing category when add subcategory expect exception`() {
        //given
        val categoryId = 1

        //when && then
        assertThatThrownBy {
            categoryService.addSubcategory(subcategoryName = "subcategory name", categoryId = categoryId)
        }.isInstanceOf(CategoryNotFoundException::class.java)
    }

    @Test
    fun `given subcategory with existing category when add subcategory expect save subcategory`() {
        //given
        val categoryDto = categoryService.addCategory(categoryName = "name")

        //when
        val subcategoryDto = categoryService.addSubcategory(subcategoryName = "subcategory name", categoryId = categoryDto.id)

        //then
        val subcategories = categoryService.findAllSubcategories(categoryDto.id)
        assertThat(subcategories, contains(samePropertyValuesAs(subcategoryDto)))
    }

    @Test
    fun `given existing subcategory when add subcategory expect exception`() {
        //given
        val categoryId = categoryService.addCategory(categoryName = "category").id
        val subcategoryName = "subcategory name"
        categoryService.addSubcategory(subcategoryName = subcategoryName, categoryId = categoryId)

        //when && then
        assertThatThrownBy { categoryService.addSubcategory(subcategoryName = subcategoryName, categoryId = categoryId) }
                .isInstanceOf(SubcategoryAlreadyExistException::class.java)
    }

    @Test
    fun `when update existing subcategory expect update subcategory`() {
        //given
        val categoryId = categoryService.addCategory(categoryName = "category name").id
        val subcategoryDto = categoryService.addSubcategory(subcategoryName = "subcategory name", categoryId = categoryId)

        //when
        val updatedName = "subcategory1"
        val updatedSubcategoryDto = categoryService.updateSubcategory(subcategoryName = updatedName, subcategoryId = subcategoryDto.id)

        //then
        val expectedSubcategoryDto = SubcategoryDto(name = updatedName, id = subcategoryDto.id)

        assertThat(updatedSubcategoryDto, samePropertyValuesAs(expectedSubcategoryDto))

        val allSubcategories = categoryService.findAllSubcategories(categoryId = categoryId)
        assertThat(allSubcategories, contains(samePropertyValuesAs(expectedSubcategoryDto)))
    }

    @Test
    fun `when update not existing subcategory expect exception`() {
        //given
        categoryService.addCategory(categoryName = "category name")

        //when && then
        assertThatThrownBy { categoryService.updateSubcategory(subcategoryId = 1, subcategoryName = "subcategory name") }
                .isInstanceOf(SubcategoryNotFoundException::class.java)
    }

    @Test
    fun `when update existing category expect update category`() {
        //given
        val categoryId = categoryService.addCategory(categoryName = "category name").id

        //when
        val updatedName = "category1"
        val updatedCategoryDto = categoryService.updateCategory(categoryId = categoryId, categoryName = "category1")

        //then
        val expectedCategoryDto = CategoryDto(id = categoryId, name = updatedName)

        assertThat(updatedCategoryDto, samePropertyValuesAs(expectedCategoryDto))

        val allCategories = categoryService.findAllCategories()
        assertThat(allCategories, contains(samePropertyValuesAs(expectedCategoryDto)))
    }

    @Test
    fun `when update not existing category expect exception`() {
        //when && then
        assertThatThrownBy { categoryService.updateCategory(categoryId = 1, categoryName = "category name") }
                .isInstanceOf(CategoryNotFoundException::class.java)
    }

    @Test
    fun `when no categories it should return emptyl ist`() {
        //when
        val allCategories = categoryService.findAllCategories()

        //then
        assertThat(allCategories, hasSize(0))
    }

    @Test
    fun `when no subcategories for given category id it should return empty list`() {
        //when
        val allSubcategories = categoryService.findAllSubcategories(1)

        //then
        assertThat(allSubcategories, hasSize(0))
    }
}