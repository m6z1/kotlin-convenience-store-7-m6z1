package store

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import store.products.Product
import store.promotion.PromotionState
import store.promotion.Promotions
import java.time.LocalDate

class PromotionsTest {
    private val promotions = Promotions()

    @ParameterizedTest
    @CsvSource(
        "물, 2024-01-01, false",
        "콜라, 2024-11-11, true",
        "사이다, 2025-01-01, false",
    )
    fun `프로모션 할인이 가능한지 확인한다`(
        productName: String,
        today: LocalDate,
        expectedValue: Boolean,
    ) {
        val result = promotions.isPossiblePromotionDiscount(productName, today)
        assertEquals(expectedValue, result)
    }

    @ParameterizedTest
    @CsvSource(
        "물, 500, 1, null, NONE",
        "비타민워터, 1500, 6, null, NONE",
        "콜라, 1000, 10, 탄산2+1, NOT_ENOUGH_STOCK",
        "오렌지주스, 1800, 1, MD추천상품, ELIGIBLE_BENEFIT",
        "사이다, 1000, 2, 탄산2+1, ELIGIBLE_BENEFIT",
        "오렌지주스, 1800, 2, MD추천상품, AVAILABLE_BENEFIT",
        "사이다, 1000, 3, 탄산2+1, AVAILABLE_BENEFIT",
    )
    fun `상품명과 구매 개수를 입력 시 프로모션 상태를 반환한다`(
        productName: String,
        productPrice: Int,
        countToPurchase: Int,
        productPromotion: String,
        expectedState: PromotionState,
    ) {
        val product = Product(productName, productPrice, countToPurchase, productPromotion)

        println(product)
        val result = promotions.checkPromotion(product)

        assertEquals(expectedState, result)
    }
}