package com.example.controller

import com.example.dto.ItemCreateRequestDto
import com.example.dto.ItemResponseDto
import com.example.service.RSocketService
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@RestController
class RSocketClientController(
    private val service: RSocketService
) {

    @PostMapping("/items/request-response")
    fun saveItemUsingRSocketRequestResponse(@RequestBody item: ItemCreateRequestDto): Mono<ResponseEntity<*>> {
        return service.saveItemInRequestResponse(item)
    }

    @GetMapping("/items/request-stream", produces = [MediaType.APPLICATION_NDJSON_VALUE])
    fun findItemsUsingRSocketRequestStream(): Flux<ItemResponseDto> {
        return service.getItemInRequestStream()
    }

    @PostMapping("/items/fire-and-forget")
    fun addNewItemsUsingRSocketFireAndForget(@RequestBody item: ItemCreateRequestDto): Mono<ResponseEntity<*>> {
        return service.saveItemWithoutResponse(item)
    }

    @GetMapping("/items", produces = [MediaType.TEXT_EVENT_STREAM_VALUE])
    fun liveUpdates(): Flux<ItemResponseDto> {
        return service.monitorNewItems()
    }

}