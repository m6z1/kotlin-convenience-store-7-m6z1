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

        productsToPurchase = retryInput(
            inputValue = { inputView.readProductsToPurchase() },
            validationInputValue = { productsManager.validPossiblePurchase(it) }
        )

        val today = now().toLocalDate()
        productsToPurchase.forEach { product ->
            promotions.isPossiblePromotionDiscount(
                promotionName = product.keys.first(),
                today = today,
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
                }

                PromotionState.NOT_ENOUGH_STOCK -> {
                    if (checkRegularPriceToPay(
                            product.keys.first(),
                            promotions.findInsufficientPromotionQuantity(product),
                        )
                    ) {

                        receipt.addPurchasedProduct(
                            PurchasedProduct(
                                product.keys.first(),
                                product.values.first(),
                                productsManager.findProductPrice(product.keys.first()),
                            )
                        )
                        receipt.addPromotionProduct(
                            mapOf(
                                product.keys.first() to promotions.findFreebieCount(product)
                            )
                        )
                        return@forEach
                    }
                    receipt.addPurchasedProduct(
                        PurchasedProduct(
                            product.keys.first(),
                            product.values.first() - promotions.findInsufficientPromotionQuantity(product),
                            productsManager.findProductPrice(product.keys.first()),
                        )
                    )
                    receipt.addPromotionProduct(
                        mapOf(
                            product.keys.first() to promotions.findFreebieCount(product)
                        )
                    )
                }

                PromotionState.ELIGIBLE_BENEFIT -> {
                    if (checkFreebie(product.keys.first())) {
                        receipt.addPurchasedProduct(
                            PurchasedProduct(
                                product.keys.first(),
                                product.values.first() + 1,
                                productsManager.findProductPrice(product.keys.first()),
                            )
                        )
                        receipt.addPromotionProduct(
                            mapOf(
                                product.keys.first() to promotions.findFreebieCount(product)
                            )
                        )
                        return@forEach
                    }
                    receipt.addPurchasedProduct(
                        PurchasedProduct(
                            product.keys.first(),
                            product.values.first(),
                            productsManager.findProductPrice(product.keys.first()),
                        )
                    )
                    receipt.addPromotionProduct(
                        mapOf(
                            product.keys.first() to promotions.findFreebieCount(product)
                        )
                    )
                }

                PromotionState.AVAILABLE_BENEFIT -> {
                    receipt.addPurchasedProduct(
                        PurchasedProduct(
                            product.keys.first(),
                            product.values.first(),
                            productsManager.findProductPrice(product.keys.first()),
                        )
                    )
                    receipt.addPromotionProduct(
                        mapOf(
                            product.keys.first() to promotions.findFreebieCount(product)
                        )
                    )
                }
            }
        }

        checkMembership()
        showReceipt()
        checkMorePurchase()
    }

    private fun <T> retryInput(inputValue: () -> T, validationInputValue: (T) -> Unit): T {
        while (true) {
            try {
                val input = inputValue()
                validationInputValue(input)
                return input
            } catch (e: IllegalArgumentException) {
                println(e.message)
            }
        }
    }

    private fun checkFreebie(productName: String): Boolean {
        while (true) {
            try {
                val addingFreebieState = ResponseState.from(inputView.readAddingFreebie(productName))
                return promotions.isAddingFreebie(addingFreebieState)
            } catch (e: IllegalArgumentException) {
                println(e.message)
            }
        }
    }

    private fun checkRegularPriceToPay(productName: String, regularPriceToPayCount: Int): Boolean {
        while (true) {
            try {
                val regularPriceToPayState =
                    ResponseState.from(inputView.readRegularPriceToPay(productName, regularPriceToPayCount))
                return promotions.isRegularPriceToPay(regularPriceToPayState)
            } catch (e: IllegalArgumentException) {
                println(e.message)
            }
        }
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
                println(e.message)
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