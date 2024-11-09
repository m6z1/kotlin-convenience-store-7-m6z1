package store

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import store.membership.Membership
import store.membership.MembershipState

class MembershipTest {

    @Nested
    @DisplayName("멤버십 적용된 테스트")
    inner class ApplicationMembership {
        private val membership = Membership(MembershipState.APPLICATION)

        @Test
        fun `상품의 구매 금액이 10,000원일 경우 할인 금액은 3,000원이다`() {
            val price = 10_000

            val discountPrice = membership.calculateDiscount(price)

            assertEquals(3000, discountPrice)
        }

        @ParameterizedTest
        @CsvSource(
            "30_000, 8_000",
            "40_000, 8_000",
            "100_000, 8_000",
        )
        fun `상품의 구매 금액의 30%가 8,000원 이상일 경우에도 할인 금액은 8,000원이다`(
            price: Int,
            expectedDiscount: Int,
        ) {
            val discount = membership.calculateDiscount(price)

            assertEquals(expectedDiscount, discount)
        }
    }

    @ParameterizedTest
    @CsvSource(
        "5_000, 0",
        "30_000, 0",
        "40_000, 0",
        "100_000, 0",
    )
    fun `멤버십이 미적용일 경우 구매 금액이 어떤 값이어도 할인 금액은 0이다`(
        price: Int,
        expectedDiscount: Int,
    ) {
        val membership = Membership(MembershipState.NOT_APPLICATION)

        val discount = membership.calculateDiscount(price)

        assertEquals(expectedDiscount, discount)
    }
}