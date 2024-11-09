package store

import camp.nextstep.edu.missionutils.DateTimes.now
import store.membership.Membership
import store.membership.MembershipState
import store.products.ProductsManager
import store.promotion.PromotionState
import store.promotion.Promotions
import store.receipt.PurchasedProduct
import store.receipt.Receipt
import store.view.InputView
import store.view.OutputView
import java.time.LocalDate

class StoreController(
    private val inputView: InputView,
    private val outputView: OutputView,
    private val productsManager: ProductsManager,
    private val promotions: Promotions,
) {
    private val receipt = Receipt()

    fun start() {
        outputView.printWelcomeMessage()
        outputView.printProducts(productsManager.products)

        val productsToPurchase = inputView.readProductsToPurchase()
        productsManager.validPossiblePurchase(productsToPurchase)

        val today = now()
        val formattedToday = LocalDate.of(today.year, today.month, today.dayOfMonth)
        productsToPurchase.forEach { product ->
            promotions.isPossiblePromotionDiscount(
                promotionName = product.keys.first(),
                today = formattedToday,
            )
        }

        productsToPurchase.forEach { product ->
            val promotionState = promotions.checkPromotion(product)
            when (promotionState) {
                PromotionState.NONE -> {
                    receipt.addPurchasedProduct(
                        PurchasedProduct(
                            product.keys.first(),
                            product.values.first(),
                            productsManager.findProductPrice(product.keys.first()),
                        )
                    )
                    checkMembership(productsManager.findProductPrice(product.keys.first()) * product.values.first())
                }

                PromotionState.NOT_ENOUGH_STOCK -> Unit
                PromotionState.ELIGIBLE_BENEFIT -> Unit
                PromotionState.AVAILABLE_BENEFIT -> Unit
            }
        }
    }

    private fun checkMembership(price: Int) {
        while (true) {
            try {
                val membershipState = MembershipState.from(inputView.readMembershipState())
                val membershipDiscount = Membership(membershipState).calculateDiscount(price)
                receipt.addMembershipDiscount(membershipDiscount)
                break
            } catch (e: IllegalArgumentException) {
                println(e.message)
            }
        }
    }
}