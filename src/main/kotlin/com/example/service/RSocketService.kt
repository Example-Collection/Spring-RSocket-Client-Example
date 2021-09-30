package com.example.service

import com.example.dto.ItemCreateRequestDto
import com.example.dto.ItemResponseDto
import io.rsocket.metadata.WellKnownMimeType
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.messaging.rsocket.RSocketRequester
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono
import java.net.URI
import java.time.Duration

@Service
class RSocketService(
    builder: RSocketRequester.Builder
) {

    companion object {
        private const val HOST = "localhost"
        private const val PORT = 7000
        private const val SAVE_REQUEST_RESPONSE = "items.saveRequestResponse"
        private const val GET_REQUEST_STREAM = "items.getRequestStream"
        private const val SAVE_WITHOUT_RESPONSE = "items.saveWithoutResponse"
        private const val GET_MONITOR = "items.monitor"
    }
    private val requester: Mono<RSocketRequester> = builder
        .dataMimeType(MediaType.APPLICATION_JSON)
        .metadataMimeType(MediaType.parseMediaType(WellKnownMimeType.MESSAGE_RSOCKET_ROUTING.toString()))
        .tcp(HOST, PORT)
        .toMono()
        .retry(5)
        .cache()

    fun saveItemInRequestResponse(item: ItemCreateRequestDto): Mono<ResponseEntity<*>> {
        return requester
            .flatMap {
                    rSocketRequester -> rSocketRequester
                .route(SAVE_REQUEST_RESPONSE)
                .data(item)
                .retrieveMono(ItemResponseDto::class.java)
            }
            .map {
                    itemResponse -> ResponseEntity.created(URI.create("/items/request-response")).body(itemResponse)
            }
    }

    fun getItemInRequestStream(): Flux<ItemResponseDto> {
        return requester
            .flatMapMany { rSocketRequester -> rSocketRequester
                .route(GET_REQUEST_STREAM)
                .retrieveFlux(ItemResponseDto::class.java)
                .delayElements(Duration.ofSeconds(1))}
    }

    fun saveItemWithoutResponse(item: ItemCreateRequestDto): Mono<ResponseEntity<*>> {
        return requester
            .flatMap { rSocketRequester -> rSocketRequester
                .route(SAVE_WITHOUT_RESPONSE)
                .data(item)
                .send()
            }
            .then(
                Mono.just(ResponseEntity.created(URI.create("/items/fire-and-forget")).build<Void>())
            )
    }

    fun monitorNewItems(): Flux<ItemResponseDto> {
        return requester
            .flatMapMany { rSocketRequester -> rSocketRequester
                .route(GET_MONITOR)
                .retrieveFlux(ItemResponseDto::class.java)
            }
    }
}