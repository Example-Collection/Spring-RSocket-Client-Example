package com.example.dto

import com.example.domain.Item

data class ItemCreateRequestDto(
    val name: String = "",
    val description: String = "",
    val price: Double = 0.0
) {
    fun toEntity(): Item {
        return Item(name, description, price)
    }
}