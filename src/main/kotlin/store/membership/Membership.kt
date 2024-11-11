package store.membership

import store.ResponseState
import kotlin.math.roundToInt

class Membership(private val membershipState: ResponseState) {

    fun calculateDiscount(price: Int): Int {
        return when (membershipState) {
            ResponseState.POSITIVE -> calculate(price)
            ResponseState.NEGATIVE -> ZERO
        }
    }

    private fun calculate(price: Int): Int {
        val discount = (price * MEMBERSHIP_DISCOUNT_PERCENT).roundToInt()

        if (discount >= MAX_MEMBERSHIP_DISCOUNT) {
            return MAX_MEMBERSHIP_DISCOUNT
        }

        return discount
    }

    companion object {
        private const val ZERO = 0
        private const val MEMBERSHIP_DISCOUNT_PERCENT = 0.3
        private const val MAX_MEMBERSHIP_DISCOUNT = 8_000
    }
}