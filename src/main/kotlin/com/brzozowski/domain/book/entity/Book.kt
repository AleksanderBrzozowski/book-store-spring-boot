package com.brzozowski.domain.book.entity

import com.brzozowski.domain.category.entity.Subcategory
import javax.persistence.*

/**
 * @author Aleksander Brzozowski
 */

@Entity
class Book(
        @Id
        var isbn: String,
        var title: String,

        @ManyToOne
        @JoinColumn(updatable = false, insertable = false)
        var subcategory: Subcategory,

        @Enumerated(EnumType.STRING)
        @ElementCollection(fetch = FetchType.EAGER)
        @CollectionTable(name = "book_category_special")
        @Column(name = "type")
        val specialCategories: MutableSet<CategorySpecial> = mutableSetOf(),

        @Enumerated(EnumType.STRING)
        val format: Format = Format.BOOK
)

enum class CategorySpecial {
    PROMO, BEST
}

enum class Format {
        E_BOOK, BOOK
}