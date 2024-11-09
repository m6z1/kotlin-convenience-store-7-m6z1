package store

import java.io.File
import java.time.LocalDate

class Promotions {
    private val promotions: MutableList<List<String>> = emptyList<List<String>>().toMutableList()
    private val productsManager = ProductsManager()

    init {
        updatePromotions()
    }

    private fun updatePromotions() {
        val productsLine = readProductsFile()
        val productsLineExceptTitle = productsLine.filter { it != productsLine[0] }
        productsLineExceptTitle.forEach { product ->
            promotions.add(product.split(","))
        }
    }

    private fun readProductsFile(): List<String> {
        val path = "src/main/resources/promotions.md"
        val productsLine = emptyList<String>().toMutableList()
        File(path).forEachLine { productsLine.add(it) }

        return productsLine
    }

    fun isPossiblePromotionDiscount(promotionName: String, today: LocalDate): Boolean {
        val promotion = promotions.firstOrNull { it[PROMOTION_NAME_INDEX] == promotionName } ?: return false

        val startDate = LocalDate.parse(promotion[PROMOTION_START_DATE_INDEX])
        val endDate = LocalDate.parse(promotion[PROMOTION_END_DATE_INDEX])
        return today in startDate..endDate
    }

    private fun findPromotion(promotionName: String): List<String> {
        return promotions.find { promotion -> promotion[PROMOTION_NAME_INDEX] == promotionName }
            ?: throw IllegalArgumentException("[ERROR] 해당 프로모션 이름을 가진 프로모션은 존재하지 않습니다.")
    }

    fun checkPromotion(product: Map<String, Int>): PromotionState {
        val productName = product.keys.first()
        val productCountToPurchase = product.values.first()
        val promotionName = productsManager.findProductPromotion(productName = productName)
        val promotionStock = productsManager.findPromotionStock(productName = product.keys.first())
        val promotion = findPromotion(promotionName ?: return PromotionState.NONE)

        if (promotionStock < (productCountToPurchase / promotion[PROMOTION_BUY_COUNT_INDEX].toInt()) + productCountToPurchase) {
            return PromotionState.NOT_ENOUGH_STOCK
        }

        if (productCountToPurchase % (promotion[PROMOTION_BUY_COUNT_INDEX].toInt() + promotion[PROMOTION_GET_COUNT_INDEX].toInt()) == 1) {
            return PromotionState.ELIGIBLE_BENEFIT
        }

        return PromotionState.AVAILABLE_BENEFIT
    }

    companion object {
        const val PROMOTION_NAME_INDEX = 0
        const val PROMOTION_BUY_COUNT_INDEX = 1
        const val PROMOTION_GET_COUNT_INDEX = 2
        const val PROMOTION_START_DATE_INDEX = 3
        const val PROMOTION_END_DATE_INDEX = 4
    }
}