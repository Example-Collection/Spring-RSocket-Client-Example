package com.example.domain

import org.springframework.data.annotation.Id
import java.util.*

class Item {
    @Id
    var id: String? = null
    var name: String = ""
    var description: String = ""
    var price = 0.0


    constructor(name: String, description: String, price: Double) {
        this.name = name
        this.description = description
        this.price = price
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) {
            return true
        }
        if (other == null || javaClass != other.javaClass) {
            return false
        }
        val item = other as Item
        return item.price.compareTo(price) == 0 &&
                Objects.equals(id, item.id) &&
                Objects.equals(name, item.name) &&
                Objects.equals(description, item.description)
    }

    override fun hashCode(): Int {
        var result = id?.hashCode() ?: 0
        result = 31 * result + name.hashCode()
        result = 31 * result + description.hashCode()
        result = 31 * result + price.hashCode()
        return result
    }
}
