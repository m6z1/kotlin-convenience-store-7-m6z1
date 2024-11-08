package store

import camp.nextstep.edu.missionutils.DateTimes.now
import java.time.LocalDate

class StoreController(
    private val inputView: InputView,
    private val outputView: OutputView,
    private val productsManager: ProductsManager,
    private val promotions: Promotions,
) {

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
                PromotionState.NONE -> Unit
                PromotionState.NOT_ENOUGH_STOCK -> Unit
                PromotionState.ELIGIBLE_BENEFIT -> Unit
                PromotionState.AVAILABLE_BENEFIT -> Unit
            }
        }
    }
}