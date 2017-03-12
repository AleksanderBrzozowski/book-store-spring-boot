package com.brzozowski.domain.category.entity

import org.hibernate.validator.constraints.NotBlank
import javax.persistence.*
import javax.persistence.GenerationType.IDENTITY

/**
 * @author Aleksander Brzozowski
 */

@Entity
class Category(
        @Id
        @GeneratedValue(strategy = IDENTITY)
        val id: Int = 0,
        @NotBlank
        @Column(unique = true)
        var name: String,
        @OneToMany(mappedBy = "category")
        val subcategories: MutableSet<Subcategory> = mutableSetOf()
)
