package store.membership

import kotlin.math.roundToInt

class Membership(private val membershipState: MembershipState) {

    fun calculateDiscount(price: Int): Int {
        return when (membershipState) {
            MembershipState.APPLICATION -> calculate(price)
            MembershipState.NOT_APPLICATION -> 0
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