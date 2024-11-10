package store.promotion

import store.ResponseState
import store.products.ProductsManager
import java.io.File
import java.time.LocalDate

class Promotions {
    private val promotions: MutableList<Promotion> = mutableListOf()
    private val productsManager = ProductsManager()

    init {
        updatePromotions()
    }

    private fun updatePromotions() {
        val promotions = readPromotionsFile().drop(1)
        promotions.forEach { promotion ->
            val promotionData = promotion.split(",")
            val promotion = Promotion(
                name = promotionData[PROMOTION_NAME_INDEX],
                countOfBuy = promotionData[PROMOTION_BUY_COUNT_INDEX].toInt(),
                countOfGet = promotionData[PROMOTION_GET_COUNT_INDEX].toInt(),
                startDate = LocalDate.parse(promotionData[PROMOTION_START_DATE_INDEX]),
                endDate = LocalDate.parse(promotionData[PROMOTION_END_DATE_INDEX])
            )
            this.promotions.add(promotion)
        }
    }

    private fun readPromotionsFile(): List<String> {
        val path = "src/main/resources/promotions.md"
        val productsLine = emptyList<String>().toMutableList()
        File(path).forEachLine { productsLine.add(it) }

        return productsLine
    }

    fun isPossiblePromotionDiscount(promotionName: String, today: LocalDate): Boolean {
        val promotion = promotions.firstOrNull { it.name == promotionName } ?: return false

        return today in promotion.startDate..promotion.endDate
    }

    private fun findPromotion(promotionName: String): Promotion {
        return promotions.first { promotion -> promotion.name == promotionName }
    }

    fun checkPromotion(product: Map<String, Int>): PromotionState {
        val productName = product.keys.first()
        val productCountToPurchase = product.values.first()

        val promotionName = productsManager.findProductPromotion(productName = productName)?.takeIf { it != "null" }
            ?: return PromotionState.NONE
        val promotion = findPromotion(promotionName)
        val promotionStock = productsManager.findPromotionStock(productName)

        val totalProductCount = if (promotion.countOfBuy == 1) {
            productCountToPurchase + (productCountToPurchase / 2)
        } else {
            productCountToPurchase + (productCountToPurchase / 2)
        }

        if (totalProductCount > promotionStock) {
            return PromotionState.NOT_ENOUGH_STOCK
        }

        if (promotion.countOfBuy == 1 && productCountToPurchase % 2 != 0) {
            return PromotionState.ELIGIBLE_BENEFIT
        }

        if (promotion.countOfBuy == 2 && productCountToPurchase % 2 == 0) {
            return PromotionState.ELIGIBLE_BENEFIT
        }
        return PromotionState.AVAILABLE_BENEFIT
    }

    fun findInsufficientPromotionQuantity(product: Map<String, Int>): Int {
        val productName = product.keys.first()
        val productCountToPurchase = product.values.first()
        val promotionName = productsManager.findProductPromotion(productName = productName) ?: ""
        val promotionStock = productsManager.findPromotionStock(productName = productName)
        val promotion = findPromotion(promotionName)

        val promotionSetSize = promotion.countOfBuy + promotion.countOfGet
        val maxPromotionCount = promotionStock / promotionSetSize
        val maxPromotionQuantity = maxPromotionCount * promotionSetSize

        return productCountToPurchase - maxPromotionQuantity
    }

    fun isAddingFreebie(addingFreebieState: ResponseState): Boolean {
        return when (addingFreebieState) {
            ResponseState.POSITIVE -> true
            ResponseState.NEGATIVE -> false
        }
    }

    fun isRegularPriceToPay(regularPriceToPayState: ResponseState): Boolean {
        return when (regularPriceToPayState) {
            ResponseState.POSITIVE -> true
            ResponseState.NEGATIVE -> false
        }
    }

    fun findFreebieCount(productToPurchase: Map<String, Int>): Int {
        val productName = productToPurchase.keys.first()
        val productCountToPurchase = productToPurchase.values.first()

        val promotionName =
            productsManager.findProductPromotion(productName = productName)?.takeIf { it.isNotEmpty() } ?: ""
        val promotionStock = productsManager.findPromotionStock(productName = productName)
        val promotion = findPromotion(promotionName)

        val promotionSetSize = promotion.countOfBuy + promotion.countOfGet

        val maxSetsByPurchase = productCountToPurchase / promotion.countOfBuy
        val maxSetsByStock = promotionStock / promotionSetSize
        val applicableSets = minOf(maxSetsByPurchase, maxSetsByStock)

        return applicableSets * promotion.countOfGet
    }

    companion object {
        private const val PROMOTION_NAME_INDEX = 0
        private const val PROMOTION_BUY_COUNT_INDEX = 1
        private const val PROMOTION_GET_COUNT_INDEX = 2
        private const val PROMOTION_START_DATE_INDEX = 3
        private const val PROMOTION_END_DATE_INDEX = 4
    }
}