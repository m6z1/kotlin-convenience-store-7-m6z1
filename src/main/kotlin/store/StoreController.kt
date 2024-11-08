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
                checkPromotion(product.keys.first(), product.values.first(), promotionName)
                return@forEach
            }
            // TODO: else 이용해 바로 멤버십 할인 확인하기
        }
    }

    private fun checkPromotion(product: String, productCountToPurchase: Int, promotionName: String) {
        val promotion = promotions.findPromotion(promotionName)
        val promotionApplyingBuyCount = promotion[2].toInt()
        val promotionGiveCount = promotion[3].toInt()
        val promotionStock: Int = productsManager.findPromotionStock(product)
        if (productCountToPurchase % promotionApplyingBuyCount == 0) {
            if ((productCountToPurchase / promotionApplyingBuyCount) * promotionGiveCount <= promotionStock) {
                // TODO: 프로모션 적용
            } else {
                // TODO: 일부 수량에 대해 정가로 결제하게 됨을 안내
            }
            return
        }
        //TODO: 필요한 수량을 추가로 가져오면 혜택을 받을 수 있음을 안내
    }
}