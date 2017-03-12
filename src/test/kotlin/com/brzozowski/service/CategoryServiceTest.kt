package com.brzozowski.service

import com.brzozowski.domain.category.dto.CategoryDto
import com.brzozowski.domain.category.dto.SubcategoryDto
import com.brzozowski.domain.category.repository.CategoryRepository
import com.brzozowski.domain.category.repository.SubcategoryRepository
import com.brzozowski.domain.category.service.*
import com.brzozowski.presentation.category.model.CreateCategoryModel
import com.brzozowski.presentation.category.model.CreateSubcategoryModel
import com.brzozowski.presentation.category.model.UpdateCategoryModel
import com.brzozowski.presentation.category.model.UpdateSubcategoryModel
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.hamcrest.Matchers.contains
import org.hamcrest.Matchers.samePropertyValuesAs
import org.junit.After
import org.junit.Assert.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.TestPropertySource
import org.springframework.test.context.junit4.SpringRunner


/**
 * @author Aleksander Brzozowski
 */

@Suppress("SpringKotlinAutowiredMembers")
@SpringBootTest
@RunWith(SpringRunner::class)
@TestPropertySource("classpath:/test.properties")
class CategoryServiceTest {

    @Autowired
    private lateinit var categoryService: CategoryService

    @Autowired
    private lateinit var categoryRepository: CategoryRepository
    @Autowired
    private lateinit var subcategoryRepository: SubcategoryRepository

    @After
    fun after() {
        subcategoryRepository.deleteAll()
        categoryRepository.deleteAll()
    }

    @Test
    fun `given existing category when add category expect exception`() {
        //given
        val createCategoryModel = CreateCategoryModel("category name")
        categoryService.addCategory(createCategoryModel)

        //when && then
        assertThatThrownBy { categoryService.addCategory(createCategoryModel) }
                .isInstanceOf(CategoryAlreadyExistException::class.java)
    }

    @Test
    fun `given not existing category when add category expect save category`() {
        //given
        val categoryName = "category name"
        val createCategoryModel = CreateCategoryModel(name = categoryName)

        //when
        val categoryDto = categoryService.addCategory(createCategoryModel)

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
            categoryService.addSubcategory(CreateSubcategoryModel(name = "subcategory name", categoryId = categoryId))
        }.isInstanceOf(CategoryNotFoundException::class.java)
    }

    @Test
    fun `given subcategory with existing category when add subcategory expect save subcategory`() {
        //given
        val categoryDto = categoryService.addCategory(CreateCategoryModel(name = "name"))
        val createSubcategoryModel = CreateSubcategoryModel(name = "subcategory name", categoryId = categoryDto.id)

        //when
        val subcategoryDto = categoryService.addSubcategory(createSubcategoryModel)

        //then
        val subcategories = categoryService.findAllSubcategories(categoryDto.id)
        assertThat(subcategories, contains(samePropertyValuesAs(subcategoryDto)))
    }

    @Test
    fun `given existing subcategory when add subcategory expect exception`() {
        //given
        val category = categoryService.addCategory(CreateCategoryModel(name = "category"))
        val createSubcategoryModel = CreateSubcategoryModel(name = "subcategory", categoryId = category.id)
        categoryService.addSubcategory(createSubcategoryModel)

        //when && then
        assertThatThrownBy { categoryService.addSubcategory(createSubcategoryModel) }
                .isInstanceOf(SubcategoryAlreadyExistException::class.java)
    }

    @Test
    fun `when update existing subcategory expect update subcategory`() {
        //given
        val categoryDto = categoryService.addCategory(CreateCategoryModel(name = "category"))
        val createSubcategoryModel = CreateSubcategoryModel(name = "subcategory", categoryId = categoryDto.id)
        val subcategoryDto = categoryService.addSubcategory(createSubcategoryModel)

        //when
        val updatedName = "subcategory1"
        val updatedSubcategoryDto = UpdateSubcategoryModel(name = updatedName)
                .let { categoryService.updateSubcategory(model = it, subcategoryId = subcategoryDto.id) }

        //then
        val expectedSubcategoryDto = SubcategoryDto(name = updatedName, id = subcategoryDto.id)

        assertThat(updatedSubcategoryDto, samePropertyValuesAs(expectedSubcategoryDto))

        val allSubcategories = categoryService.findAllSubcategories(categoryId = categoryDto.id)
        assertThat(allSubcategories, contains(samePropertyValuesAs(expectedSubcategoryDto)))
    }

    @Test
    fun `when update not existing subcategory expect exception`() {
        //given
        categoryService.addCategory(CreateCategoryModel(name = "category"))

        //when && then
        val model = UpdateSubcategoryModel(name = "subcategory")
        assertThatThrownBy { categoryService.updateSubcategory(model = model, subcategoryId = 1) }
                .isInstanceOf(SubcategoryNotFoundException::class.java)
    }

    @Test
    fun `when update existing category expect update category`() {
        //given
        val categoryDto = categoryService.addCategory(CreateCategoryModel(name = "category"))

        //when
        val updatedName = "category1"
        val updatedCategoryDto = UpdateCategoryModel(name = "category1")
                .let { categoryService.updateCategory(model = it, categoryId = categoryDto.id) }

        //then
        val expectedCategoryDto = CategoryDto(id = categoryDto.id, name = updatedName)

        assertThat(updatedCategoryDto, samePropertyValuesAs(expectedCategoryDto))

        val allCategories = categoryService.findAllCategories()
        assertThat(allCategories, contains(samePropertyValuesAs(expectedCategoryDto)))
    }

    @Test
    fun `when update not existing category expect exception`() {
        //when && then
        val model = UpdateCategoryModel(name = "category")
        assertThatThrownBy { categoryService.updateCategory(model = model, categoryId = 1) }
                .isInstanceOf(CategoryNotFoundException::class.java)
    }
}