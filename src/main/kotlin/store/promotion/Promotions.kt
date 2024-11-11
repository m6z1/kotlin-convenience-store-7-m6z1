package store.promotion

import store.fileReader.FileReader
import store.products.Product
import store.products.ProductsManager
import store.utils.ResponseState
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

    private fun findPromotion(promotionName: String): Promotion? {
        return promotions.firstOrNull { promotion -> promotion.name == promotionName }
    }

    fun checkPromotion(product: Product): PromotionState {
        val promotion = findPromotion(product.promotion) ?: return PromotionState.NONE
        val promotionStock = productsManager.findPromotionStock(product.name)
        val totalProductCount = calculateTotalProductCount(promotion, product.quantity)

        if (isNotEnoughStock(totalProductCount, promotionStock)) return PromotionState.NOT_ENOUGH_STOCK
        if (promotionStock < (promotion.countOfBuy + promotion.countOfGet)) return PromotionState.NONE
        if (isEligibleOneBuyOneGetPromotion(promotion, product.quantity)) return PromotionState.ELIGIBLE_BENEFIT
        if (isEligibleTwoBuyOneGetPromotion(promotion, product.quantity)) return PromotionState.ELIGIBLE_BENEFIT
        return PromotionState.AVAILABLE_BENEFIT
    }

    private fun calculateTotalProductCount(promotion: Promotion, productCountToPurchase: Int): Int {
        if (promotion.countOfBuy == ONE_PLUS_ONE_PROMOTION_BUY_COUNT) {
            return productCountToPurchase + (productCountToPurchase / ONE_PLUS_ONE_PROMOTION_GET_COUNT + ONE_PLUS_ONE_PROMOTION_BUY_COUNT)
        }
        return productCountToPurchase + (productCountToPurchase / TWO_PLUS_ONE_PROMOTION_BUY_COUNT + TWO_PLUS_ONE_PROMOTION_GET_COUNT)
    }

    private fun isNotEnoughStock(totalProductCount: Int, promotionStock: Int): Boolean {
        return totalProductCount > promotionStock
    }

    private fun isEligibleOneBuyOneGetPromotion(promotion: Promotion, productCountToPurchase: Int): Boolean {
        val promotionSetSize = ONE_PLUS_ONE_PROMOTION_GET_COUNT + ONE_PLUS_ONE_PROMOTION_BUY_COUNT
        return promotion.countOfBuy == ONE_PLUS_ONE_PROMOTION_BUY_COUNT && productCountToPurchase % promotionSetSize != 0
    }

    private fun isEligibleTwoBuyOneGetPromotion(promotion: Promotion, productCountToPurchase: Int): Boolean {
        val promotionSetSize = TWO_PLUS_ONE_PROMOTION_BUY_COUNT + TWO_PLUS_ONE_PROMOTION_GET_COUNT
        return promotion.countOfBuy == TWO_PLUS_ONE_PROMOTION_BUY_COUNT && productCountToPurchase % promotionSetSize != 0
    }

    fun findInsufficientPromotionQuantity(product: Map<String, Int>): Int {
        val productName = product.keys.first()
        val productCountToPurchase = product.values.first()
        val promotionName = productsManager.findProductPromotion(productName = productName)
        val promotionStock = productsManager.findPromotionStock(productName = productName)
        val promotion = findPromotion(promotionName)
        val promotionSetSize = promotion!!.countOfBuy + promotion.countOfGet
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
            productsManager.findProductPromotion(productName = productName).takeIf { it.isNotEmpty() } ?: ""
        val promotion = findPromotion(promotionName)
        val promotionSetSize = promotion!!.countOfBuy + promotion.countOfGet

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
        private const val ONE_PLUS_ONE_PROMOTION_GET_COUNT = 1
        private const val TWO_PLUS_ONE_PROMOTION_BUY_COUNT = 2
        private const val TWO_PLUS_ONE_PROMOTION_GET_COUNT = 1
    }
}