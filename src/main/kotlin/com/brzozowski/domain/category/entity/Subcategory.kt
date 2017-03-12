package com.brzozowski.domain.category.entity

import org.hibernate.validator.constraints.NotBlank
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType.IDENTITY
import javax.persistence.Id
import javax.persistence.ManyToOne

/**
 * @author Aleksander Brzozowski
 */

@Entity
class Subcategory(
        @Id
        @GeneratedValue(strategy = IDENTITY)
        val id: Int = 0,
        @NotBlank
        var name: String,
        @ManyToOne
        val category: Category
)