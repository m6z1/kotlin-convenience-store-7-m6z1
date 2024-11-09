package store

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

class PromotionsTest {
    private val promotions = Promotions()

    @ParameterizedTest
    @CsvSource(
        "물, 1, NONE",
        "비타민워터, 6, NONE",
        "콜라, 10, NOT_ENOUGH_STOCK",
        "오렌지주스, 1, ELIGIBLE_BENEFIT",
        "사이다, 2, ELIGIBLE_BENEFIT",
        "오렌지주스, 2, AVAILABLE_BENEFIT",
        "사이다, 3, AVAILABLE_BENEFIT",
    )
    fun `상품명과 구매 개수를 입력 시 프로모션 상태를 반환한다`(
        productName: String,
        count: Int,
        expectedState: PromotionState,
    ) {
        val product = mapOf(productName to count)

        val result = promotions.checkPromotion(product)

        assertEquals(expectedState, result)
    }
}