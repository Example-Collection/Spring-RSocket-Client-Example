package com.example

import com.example.domain.Item
import com.example.domain.ItemRepository
import com.example.dto.ItemResponseDto
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient
import reactor.test.StepVerifier
import java.util.function.Predicate
import java.util.stream.Collectors
import java.util.stream.IntStream


@SpringBootTest
@AutoConfigureWebTestClient
class RSocketTest {

    @Autowired
    private lateinit var webTestClient: WebTestClient

    @Autowired
    private lateinit var repository: ItemRepository

    companion object {
        private const val ITEM_NAME = "item name"
        private const val ITEM_DESCRIPTION = "item description"
        private const val ITEM_PRICE = 12.34
    }

    @DisplayName("요청-응답 테스트")
    @Test
    @Throws(InterruptedException::class)
    fun verifyRemoteOperationsThroughRSocketRequestResponse() {
        repository.deleteAll()
            .`as`(StepVerifier::create)
            .verifyComplete()

        val item = webTestClient.post().uri("/items/request-response")
            .bodyValue(Item(ITEM_NAME, ITEM_DESCRIPTION, ITEM_PRICE))
            .exchange()
            .expectStatus().isCreated
            .expectBody(ItemResponseDto::class.java)
            .returnResult()
            .responseBody!!
        assertNotNull(item.id)
        assertEquals(ITEM_NAME, item.name)
        assertEquals(ITEM_DESCRIPTION, item.description)
        assertEquals(ITEM_PRICE, item.price)

        Thread.sleep(500)

        repository.findAll()
            .`as`(StepVerifier::create)
            .expectNextMatches { savedItem ->
                assertNotNull(savedItem.id)
                assertEquals(ITEM_NAME, savedItem.name)
                assertEquals(ITEM_DESCRIPTION, savedItem.description)
                assertEquals(ITEM_PRICE, savedItem.price)
                true
            }
            .verifyComplete()
    }

    @Test
    @DisplayName("요청-스트림 테스트")
    @Throws(InterruptedException::class)
    fun verifyRemoteOperationsThroughRSocketRequestStream() {

        repository.deleteAll().block()

        val items: List<Item> = IntStream.rangeClosed(1, 3)
            .mapToObj { i -> Item("name - $i", "description - $i", i + 0.0) }
            .collect(Collectors.toList())

        repository.saveAll(items).blockLast()

        webTestClient.get().uri("/items/request-stream")
            .accept(MediaType.APPLICATION_NDJSON)
            .exchange()
            .expectStatus().isOk
            .returnResult(ItemResponseDto::class.java)
            .responseBody
            .`as`(StepVerifier::create)
            .expectNextMatches(itemPredicate("1"))
            .expectNextMatches(itemPredicate("2"))
            .expectNextMatches(itemPredicate("3"))
            .verifyComplete()
    }

    private fun itemPredicate(num: String): Predicate<ItemResponseDto> {
        return Predicate { item: ItemResponseDto ->
            assertThat(item.name).startsWith("name")
            assertThat(item.name).endsWith(num)
            assertThat(item.description).startsWith("description")
            assertThat(item.description).endsWith(num)
            assertThat(item.price).isPositive
            true
        }
    }

    @Test
    @DisplayName("실행 후 망각 테스트")
    @Throws(InterruptedException::class)
    fun verifyRemoteOperationsThroughRSocketFireAndForget() {

        repository.deleteAll()
            .`as`(StepVerifier::create)
            .verifyComplete()

        webTestClient.post().uri("/items/fire-and-forget")
            .bodyValue(Item(ITEM_NAME, ITEM_DESCRIPTION, ITEM_PRICE))
            .exchange()
            .expectStatus().isCreated
            .expectBody().isEmpty

        Thread.sleep(500)

        repository.findAll()
            .`as`(StepVerifier::create)
            .expectNextMatches{ item ->
                assertNotNull(item.id)
                assertEquals(ITEM_NAME, item.name)
                assertEquals(ITEM_DESCRIPTION, item.description)
                assertEquals(ITEM_PRICE, item.price)
                true
            }
            .verifyComplete()
    }

}