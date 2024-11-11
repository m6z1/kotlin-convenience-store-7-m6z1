package store

import camp.nextstep.edu.missionutils.DateTimes.now
import store.membership.Membership
import store.products.ProductsManager
import store.promotion.PromotionState
import store.promotion.Promotions
import store.receipt.PurchasedProduct
import store.receipt.Receipt
import store.view.InputView
import store.view.OutputView

class StoreController(
    private val inputView: InputView,
    private val outputView: OutputView,
    private val productsManager: ProductsManager,
    private val promotions: Promotions,
) {
    private val receipt = Receipt()
    private lateinit var productsToPurchase: List<Map<String, Int>>

    fun start() {
        outputView.printWelcomeMessage()
        outputView.printProducts(productsManager.products)
        productsToPurchase = getValidatedProductsToPurchase()
        productsToPurchase.forEach { product ->
            if (isApplyingPromotion(product)) {
                addProductsToReceipt(product)
                return@forEach
            }
            val purchasedProduct = PurchasedProduct(
                name = product.keys.first(),
                count = product.values.first(),
                price = productsManager.findProductPrice(product.keys.first()),
            )
            receipt.addPurchasedProduct(
                purchasedProduct
            )
            productsManager.updateLatestProduct(purchasedProduct = purchasedProduct, isPromotionPeriod = false)
        }
        checkMembership()
        showReceipt()
        checkMorePurchase()
    }


    private fun getValidatedProductsToPurchase(): List<Map<String, Int>> {
        while (true) {
            try {
                outputView.printProductsToPurchaseMessage()
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
        val promotionState = promotions.checkPromotion(productToPurchase)

        when (promotionState) {
            PromotionState.NONE -> addRegularProductToReceipt(productToPurchase)
            PromotionState.NOT_ENOUGH_STOCK -> addProductWithInsufficientStock(productToPurchase)
            PromotionState.ELIGIBLE_BENEFIT -> addEligibleBenefitProductToReceipt(productToPurchase)
            PromotionState.AVAILABLE_BENEFIT -> addAvailableBenefitProductToReceipt(productToPurchase)
        }
    }

    private fun addRegularProductToReceipt(product: Map<String, Int>) {
        val purchasedProduct = PurchasedProduct(
            product.keys.first(), product.values.first(), productsManager.findProductPrice(product.keys.first())
        )
        receipt.addPurchasedProduct(purchasedProduct)
        productsManager.updateLatestProduct(purchasedProduct, true)
    }

    private fun addProductWithInsufficientStock(product: Map<String, Int>) {
        val productName = product.keys.first()
        val productCountToPurchase = product.values.first()

        if (checkRegularPriceToPay(productName, promotions.findInsufficientPromotionQuantity(product))) {
            val purchasedProduct = PurchasedProduct(
                productName, productCountToPurchase, productsManager.findProductPrice(productName)
            )
            receipt.addPurchasedProduct(purchasedProduct)
            receipt.addPromotionProduct(mapOf(productName to promotions.findFreebieCount(product)))
            productsManager.updateLatestProduct(purchasedProduct, true)
            return
        }
        val purchasedProduct = PurchasedProduct(
            productName,
            productCountToPurchase - promotions.findInsufficientPromotionQuantity(product),
            productsManager.findProductPrice(productName)
        )
        receipt.addPurchasedProduct(purchasedProduct)
        receipt.addPromotionProduct(mapOf(productName to promotions.findFreebieCount(product)))
        productsManager.updateLatestProduct(purchasedProduct, true)
    }

    private fun addEligibleBenefitProductToReceipt(product: Map<String, Int>) {
        val productName = product.keys.first()
        val productCountToPurchase = product.values.first()

        if (checkFreebie(productName)) {
            val purchasedProduct = PurchasedProduct(
                productName,
                productCountToPurchase + 1,
                productsManager.findProductPrice(productName),
            )
            receipt.addPurchasedProduct(purchasedProduct)
            productsManager.updateLatestProduct(purchasedProduct, true)
            return
        }
        val purchasedProduct = PurchasedProduct(
            productName, productCountToPurchase, productsManager.findProductPrice(productName)
        )

        receipt.addPurchasedProduct(purchasedProduct)
        receipt.addPromotionProduct(mapOf(productName to promotions.findFreebieCount(product)))
        productsManager.updateLatestProduct(purchasedProduct, true)
    }

    private fun checkFreebie(productName: String): Boolean {
        while (true) {
            try {
                outputView.printAddingFreebieMessage(productName)
                val addingFreebieState = ResponseState.from(inputView.readAddingFreebie())
                return promotions.isAddingFreebie(addingFreebieState)
            } catch (e: IllegalArgumentException) {
                outputView.printErrorMessage(e.message)
            }
        }
    }

    private fun checkRegularPriceToPay(productName: String, regularPriceToPayCount: Int): Boolean {
        while (true) {
            try {
                outputView.printRegularPriceToPayMessage(productName, regularPriceToPayCount)
                val regularPriceToPayState = ResponseState.from(inputView.readRegularPriceToPay())
                return promotions.isRegularPriceToPay(regularPriceToPayState)
            } catch (e: IllegalArgumentException) {
                outputView.printErrorMessage(e.message)
            }
        }
    }

    private fun addAvailableBenefitProductToReceipt(product: Map<String, Int>) {
        receipt.addPurchasedProduct(
            PurchasedProduct(
                product.keys.first(), product.values.first(), productsManager.findProductPrice(product.keys.first())
            )
        )
        receipt.addPromotionProduct(
            mapOf(product.keys.first() to promotions.findFreebieCount(product))
        )
    }

    private fun checkMembership() {
        while (true) {
            try {
                outputView.printMembershipMessage()
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
                outputView.printMorePurchaseMessage()
                val morePurchaseState = ResponseState.from(inputView.readMorePurchase())
                when (morePurchaseState) {
                    ResponseState.POSITIVE -> restart()
                    ResponseState.NEGATIVE -> break
                }
            } catch (e: IllegalArgumentException) {
                println(e.message)
            }
        }
    }

    private fun restart() {
        receipt.reset()
        start()
    }
}