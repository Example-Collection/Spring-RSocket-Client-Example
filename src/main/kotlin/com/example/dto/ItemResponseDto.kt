package com.example.dto

import com.example.domain.Item

data class ItemResponseDto(
    val id: String,
    val name: String,
    val description: String,
    val price: Double
) {
    constructor(item: Item) : this(item.id!!, item.name, item.description, item.price)
}