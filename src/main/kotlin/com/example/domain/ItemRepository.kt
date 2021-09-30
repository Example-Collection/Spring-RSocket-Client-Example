package com.example.domain

import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import org.springframework.stereotype.Repository

@Repository
interface ItemRepository : ReactiveMongoRepository<Item, String>