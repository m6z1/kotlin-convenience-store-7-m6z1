package store

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import store.receipt.PurchasedProduct
import store.receipt.Receipt

class ReceiptTest {
    private val receipt = Receipt()

    @Test
    fun `총 합계 금액 계산 메서드를 호출하면 구매한 상품들로 총 합계 금액을 반환한다`() {
        testValue.forEach { receipt.addPurchasedProduct(it) }

        val result = receipt.calculateTotalAmount()

        assertEquals(10_000, result)
    }

    @Test
    fun `증정품에 대한 상품들의 총 합계 금액을 반환한다`() {
        testValue.forEach { receipt.addPurchasedProduct(it) }
        promotionProducts.forEach { receipt.addPromotionProduct(it) }

        val result = receipt.calculateFreebiesPrice()

        assertEquals(1_000, result)
    }

    @Test
    fun `증정 상품을 제외한 상품들의 총 합계 금액을 반환한다`() {
        testValue.forEach { receipt.addPurchasedProduct(it) }
        promotionProducts.forEach { receipt.addPromotionProduct(it) }

        val result = receipt.calculateNotContainingFreebie()

        assertEquals(5_000, result)
    }

    @Test
    fun `구매자가 내야 할 금액을 반환한다`() {
        testValue.forEach { receipt.addPurchasedProduct(it) }
        promotionProducts.forEach { receipt.addPromotionProduct(it) }
        receipt.addMembershipDiscount(1_500)

        val result = receipt.calculateAmountDue()

        assertEquals(7_500, result)
    }

    companion object {
        private val testValue = listOf(
            PurchasedProduct(
                name = "물",
                count = 10,
                price = 500,
            ),
            PurchasedProduct(
                name = "사이다",
                count = 5,
                price = 1_000,
            ),
        )

        private val promotionProducts = listOf(
            mapOf("사이다" to 1),
        )
    }
}