package store

import camp.nextstep.edu.missionutils.DateTimes.now
import store.membership.Membership
import store.products.Product
import store.products.ProductsManager
import store.promotion.PromotionState
import store.promotion.Promotions
import store.receipt.PurchasedProduct
import store.receipt.Receipt
import store.utils.ResponseState
import store.view.InputView
import store.view.OutputView

class StoreController(
    private val inputView: InputView,
    private val outputView: OutputView,
    private val productsManager: ProductsManager,
    private val promotions: Promotions,
    private val receipt: Receipt,
) {
    private lateinit var productsToPurchase: List<Map<String, Int>>

    fun start() {
        outputView.printWelcomeMessage()
        outputView.printProducts(productsManager.products)
        productsToPurchase = getValidatedProductsToPurchase()
        productsToPurchase.forEach { product ->
            checkApplyingPromotionToProduct(product)
        }
        checkMembership()
        showReceipt()
        checkMorePurchase()
    }

    private fun checkApplyingPromotionToProduct(product: Map<String, Int>) {
        if (isApplyingPromotion(product)) {
            addProductsToReceipt(product)
            return
        }
        addRegularProduct(product)
    }

    private fun addRegularProduct(product: Map<String, Int>) {
        val purchasedProduct = PurchasedProduct(
            name = product.keys.first(),
            count = product.values.first(),
            price = productsManager.findProductPrice(product.keys.first()),
        )
        receipt.addPurchasedProduct(
            product = purchasedProduct,
        )
        productsManager.updateLatestProduct(purchasedProduct = purchasedProduct, isPromotionPeriod = false)
    }

    private fun getValidatedProductsToPurchase(): List<Map<String, Int>> {
        while (true) {
            try {
                val products = inputView.readProductsToPurchase()
                productsManager.validPossiblePurchase(products)
                return products
            } catch (e: IllegalArgumentException) {
                outputView.printErrorMessage(e.message)
            }
        }
    }

    private fun isApplyingPromotion(productToPurchase: Map<String, Int>): Boolean {
        val today = now().toLocalDate()

        return promotions.isPossiblePromotionDiscount(
            productName = productToPurchase.keys.first().toString(),
            today = today,
        )
    }

    private fun addProductsToReceipt(productToPurchase: Map<String, Int>) {
        val productName = productToPurchase.keys.first()
        val product = Product(
            name = productToPurchase.keys.first(),
            price = productsManager.findProductPrice(productName),
            quantity = productToPurchase.values.first(),
            promotion = productsManager.findProductPromotion(productName)
        )
        val promotionState = promotions.checkPromotion(product)
        divideFromPromotionState(promotionState, productToPurchase)
    }

    private fun divideFromPromotionState(
        promotionState: PromotionState,
        productToPurchase: Map<String, Int>
    ) {
        when (promotionState) {
            PromotionState.NONE -> addRegularProductToReceipt(productToPurchase)
            PromotionState.NOT_ENOUGH_STOCK -> addProductWithInsufficientStock(productToPurchase)
            PromotionState.ELIGIBLE_BENEFIT -> addEligibleBenefitProductToReceipt(productToPurchase)
            PromotionState.AVAILABLE_BENEFIT -> addAvailableBenefitProductToReceipt(productToPurchase)
        }
    }

    private fun addRegularProductToReceipt(product: Map<String, Int>) {
        val purchasedProduct = PurchasedProduct(
            name = product.keys.first(),
            count = product.values.first(),
            price = productsManager.findProductPrice(product.keys.first()),
        )
        receipt.addPurchasedProduct(purchasedProduct)
        productsManager.updateLatestProduct(purchasedProduct, true)
    }

    private fun addPromotionProductToReceipt(
        productName: String,
        productCountToPurchase: Int,
    ) {
        val purchasedProduct = PurchasedProduct(
            name = productName,
            count = productCountToPurchase,
            price = productsManager.findProductPrice(productName),
        )
        receipt.addPurchasedProduct(purchasedProduct)
        productsManager.updateLatestProduct(purchasedProduct, true)
        val freebieCount = promotions.findFreebieCount(mapOf(purchasedProduct.name to purchasedProduct.count))
        if (freebieCount == 0) return
        receipt.addPromotionProduct(mapOf(productName to freebieCount))
    }

    private fun addProductWithInsufficientStock(product: Map<String, Int>) {
        val productName = product.keys.first()
        val productCountToPurchase = product.values.first()
        if (checkRegularPriceToPay(productName, promotions.findInsufficientPromotionQuantity(product))) {
            addProductWithInsufficientToReceipt(productName, productCountToPurchase, product)
            return
        }
        addPromotionProductToReceipt(
            productName,
            productCountToPurchase - promotions.findInsufficientPromotionQuantity(product),
        )
    }

    private fun addProductWithInsufficientToReceipt(
        productName: String,
        productCountToPurchase: Int,
        product: Map<String, Int>
    ) {
        val purchasedProduct = createPurchasedProduct(productName, productCountToPurchase)
        val promotionProductCount = purchasedProduct.count - promotions.findInsufficientPromotionQuantity(product)
        val freebieCount = promotions.findFreebieCount(
            mapOf(purchasedProduct.name to promotionProductCount)
        )
        receipt.addPurchasedProduct(purchasedProduct)
        productsManager.updateLatestProduct(purchasedProduct, true)
        receipt.addPromotionProduct(mapOf(productName to freebieCount))
    }

    private fun createPurchasedProduct(
        productName: String,
        productCountToPurchase: Int
    ): PurchasedProduct {
        val purchasedProduct = PurchasedProduct(
            name = productName,
            count = productCountToPurchase,
            price = productsManager.findProductPrice(productName),
        )
        return purchasedProduct
    }

    private fun checkRegularPriceToPay(productName: String, regularPriceToPayCount: Int): Boolean {
        while (true) {
            try {
                val regularPriceToPayState =
                    ResponseState.from(inputView.readRegularPriceToPay(productName, regularPriceToPayCount))
                return promotions.isRegularPriceToPay(regularPriceToPayState)
            } catch (e: IllegalArgumentException) {
                outputView.printErrorMessage(e.message)
            }
        }
    }

    private fun addEligibleBenefitProductToReceipt(product: Map<String, Int>) {
        val productName = product.keys.first()
        val productCountToPurchase = product.values.first()

        if (checkFreebie(productName)) {
            addPromotionProductToReceipt(productName, productCountToPurchase + 1)
            return
        }
        addPromotionProductToReceipt(productName, productCountToPurchase)
    }

    private fun checkFreebie(productName: String): Boolean {
        while (true) {
            try {
                val addingFreebieState = ResponseState.from(inputView.readAddingFreebie(productName))
                return promotions.isAddingFreebie(addingFreebieState)
            } catch (e: IllegalArgumentException) {
                outputView.printErrorMessage(e.message)
            }
        }
    }

    private fun addAvailableBenefitProductToReceipt(product: Map<String, Int>) {
        receipt.addPurchasedProduct(
            PurchasedProduct(
                name = product.keys.first(),
                count = product.values.first(),
                price = productsManager.findProductPrice(product.keys.first()),
            )
        )
        receipt.addPromotionProduct(
            mapOf(product.keys.first() to promotions.findFreebieCount(product))
        )
    }

    private fun checkMembership() {
        while (true) {
            try {
                val membershipState = ResponseState.from(inputView.readMembershipState())
                val membershipDiscount =
                    Membership(membershipState).calculateDiscount(receipt.calculateNotContainingFreebie())
                receipt.addMembershipDiscount(membershipDiscount)
                break
            } catch (e: IllegalArgumentException) {
                outputView.printErrorMessage(e.message)
            }
        }
    }

    private fun showReceipt() {
        outputView.printReceipt(
            purchasedProducts = receipt.purchasedProducts,
            freebies = receipt.promotions,
            totalAmount = receipt.calculateTotalAmount(),
            promotionDiscount = receipt.calculateFreebiesPrice(),
            membershipDiscount = receipt.membershipDiscount,
            amountDue = receipt.calculateAmountDue(),
        )
    }

    private fun checkMorePurchase() {
        while (true) {
            try {
                val morePurchaseState = ResponseState.from(inputView.readMorePurchase())
                executeEachLogic(morePurchaseState)
                break
            } catch (e: IllegalArgumentException) {
                println(e.message)
            }
        }
    }

    private fun executeEachLogic(morePurchaseState: ResponseState) {
        when (morePurchaseState) {
            ResponseState.POSITIVE -> restart()
            ResponseState.NEGATIVE -> Unit
        }
    }

    private fun restart() {
        receipt.reset()
        start()
    }
}