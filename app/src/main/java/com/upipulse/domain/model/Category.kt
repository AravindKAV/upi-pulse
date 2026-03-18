package com.upipulse.domain.model

enum class CategoryType {
    DEBIT, CREDIT, BOTH
}

data class Category(
    val id: Long = 0,
    val name: String,
    val icon: String = "ic_custom",
    val type: CategoryType = CategoryType.BOTH
)
