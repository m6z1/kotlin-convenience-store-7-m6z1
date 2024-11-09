package store.membership

import store.ResponseState
import kotlin.math.roundToInt

class Membership(private val membershipState: ResponseState) {

    fun calculateDiscount(price: Int): Int {
        return when (membershipState) {
            ResponseState.POSITIVE -> calculate(price)
            ResponseState.NEGATIVE -> 0
        }
    }

    private fun calculate(price: Int): Int {
        val discount = (price * 0.3).roundToInt()

        if (discount >= 8000) {
            return 8000
        }

        return discount
    }
}