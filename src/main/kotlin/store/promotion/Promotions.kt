package store.promotion

import store.ResponseState
import store.fileReader.FileReader
import store.products.ProductsManager
import store.products.ProductsManager.Companion.NONE_PROMOTION
import java.time.LocalDate

class Promotions {
    private val promotions: MutableList<Promotion> = mutableListOf()
    private val productsManager = ProductsManager()
    private val fileReader = FileReader(PROMOTIONS_PATH)

    init {
        updatePromotions()
    }

    private fun updatePromotions() {
        fileReader.readFile().drop(FILE_TITLE).forEach { promotion ->
            val promotionData = promotion.split(FILE_DELIMITER)
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

    fun isPossiblePromotionDiscount(productName: String, today: LocalDate): Boolean {
        val promotion = promotions.firstOrNull { it.name == productsManager.findProductPromotion(productName) }

        if (promotion == null) {
            return false
        }

        return today in promotion.startDate..promotion.endDate
    }

    private fun findPromotion(promotionName: String): Promotion {
        return promotions.first { promotion -> promotion.name == promotionName }
    }

    fun checkPromotion(product: Map<String, Int>): PromotionState {
        val productName = product.keys.first()
        val productCountToPurchase = product.values.first()
        val promotionName =
            productsManager.findProductPromotion(productName = productName)?.takeIf { it != NONE_PROMOTION }
                ?: return PromotionState.NONE
        val promotion = findPromotion(promotionName)
        val promotionStock = productsManager.findPromotionStock(productName)
        val totalProductCount = calculateTotalProductCount(promotion, productCountToPurchase)

        if (isNotEnoughStock(totalProductCount, promotionStock)) return PromotionState.NOT_ENOUGH_STOCK
        if (isEligibleOneBuyOneGetPromotion(promotion, productCountToPurchase)) return PromotionState.ELIGIBLE_BENEFIT
        if (isEligibleTwoBuyOneGetPromotion(promotion, productCountToPurchase)) return PromotionState.ELIGIBLE_BENEFIT
        return PromotionState.AVAILABLE_BENEFIT
    }

    private fun calculateTotalProductCount(promotion: Promotion, productCountToPurchase: Int) =
        if (promotion.countOfBuy == 1) {
            productCountToPurchase + (productCountToPurchase / 2)
        } else {
            productCountToPurchase + (productCountToPurchase / 2)
        }

    private fun isNotEnoughStock(totalProductCount: Int, promotionStock: Int): Boolean {
        return totalProductCount > promotionStock
    }

    private fun isEligibleOneBuyOneGetPromotion(promotion: Promotion, productCountToPurchase: Int): Boolean {
        return promotion.countOfBuy == 1 && productCountToPurchase % 2 != 0
    }

    private fun isEligibleTwoBuyOneGetPromotion(promotion: Promotion, productCountToPurchase: Int): Boolean {
        return promotion.countOfBuy == 2 && productCountToPurchase % 3 != 0
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
        val promotion = findPromotion(promotionName)
        val promotionSetSize = promotion.countOfBuy + promotion.countOfGet

        return productCountToPurchase / promotionSetSize
    }

    companion object {
        private const val PROMOTIONS_PATH = "src/main/resources/promotions.md"
        private const val FILE_TITLE = 1
        private const val FILE_DELIMITER = ","
        private const val PROMOTION_NAME_INDEX = 0
        private const val PROMOTION_BUY_COUNT_INDEX = 1
        private const val PROMOTION_GET_COUNT_INDEX = 2
        private const val PROMOTION_START_DATE_INDEX = 3
        private const val PROMOTION_END_DATE_INDEX = 4
        private const val ONE_PLUS_ONE_PROMOTION_BUY_COUNT = 1
    }
}