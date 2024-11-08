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
        welcome()
        val productsToPurchase = inputView.readProductsToPurchase()
        productsManager.validPossiblePurchase(productsToPurchase)

        checkPromotions(productsToPurchase)
    }

    private fun welcome() {
        outputView.printWelcomeMessage()
        outputView.printProducts(productsManager.products)
    }

    private fun checkPromotions(productsToPurchase: List<Map<String, Int>>) {
        val today = LocalDate.of(now().year, now().month, now().dayOfMonth)
        productsToPurchase.forEach { product ->
            val promotionName = productsManager.findPromotion(product.keys.first()) ?: return@forEach
            if (promotions.isPossiblePromotionDiscount(promotionName, today)) {
                checkPromotion(product.keys.first(), product.values.first())
                return@forEach
            }
            // TODO: else 이용해 바로 멤버십 할인 확인하기
        }
    }

    private fun checkPromotion(product: String, productCountToPurchase: Int) {

    }
}