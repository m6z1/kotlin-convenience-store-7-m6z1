package store

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import store.products.ProductsManager

class ProductsManagerTest {
    private val productsManager = ProductsManager()

    @Nested
    @DisplayName("구매할 상품에 대한 예외 테스트")
    inner class ProductExceptionTest {

        @Test
        fun `구매할 상품들 중 하나라도 재고가 부족하면 예외를 발생한다`() {
            val productsToPurchase = listOf(
                mapOf("물" to 100),
                mapOf("사이다" to 2),
            )

            assertThrows<IllegalArgumentException> { productsManager.validPossiblePurchase(productsToPurchase) }
        }

        @Test
        fun `구매할 상품들 중 존재하지 않는 상품일 경우 예외를 발생한다`() {
            val productsToPurchase = listOf(
                mapOf("쥐포" to 5),
                mapOf("사이다" to 2),
            )

            assertThrows<IllegalArgumentException> { productsManager.validPossiblePurchase(productsToPurchase) }
        }
    }

    @ParameterizedTest
    @CsvSource(
        "물, null",
        "사이다, 탄산2+1",
        "오렌지주스, MD추천상품",
        "감자칩, 반짝할인",
    )
    fun `구매할 상품의 이름을 통해 프로모션을 찾는다`(
        productName: String,
        expectedValue: String,
    ) {
        val result = productsManager.findProductPromotion(productName)

        assertEquals(expectedValue, result)
    }

    @ParameterizedTest
    @CsvSource(
        "사이다, 8",
        "오렌지주스, 9",
        "감자칩, 5",
    )
    fun `구매할 상품의 이름을 통해 프로모션 재고를 찾는다`(
        productName: String,
        expectedValue: Int,
    ) {
        val result = productsManager.findPromotionStock(productName)

        assertEquals(expectedValue, result)
    }

    @ParameterizedTest
    @CsvSource(
        "사이다, 1000",
        "오렌지주스, 1800",
        "감자칩, 1500",
    )
    fun `구매할 상품의 이름을 통해 가격을 찾는다`(
        productName: String,
        expectedValue: Int,
    ) {
        val result = productsManager.findProductPrice(productName)

        assertEquals(expectedValue, result)
    }
}