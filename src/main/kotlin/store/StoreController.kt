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
import java.time.LocalDate

class StoreController(
    private val inputView: InputView,
    private val outputView: OutputView,
    private val productsManager: ProductsManager,
    private val promotions: Promotions,
) {
    private val receipt = Receipt()

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
                                product.values.first() - promotions.findInsufficientPromotionQuantity(product),
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

        checkMembership(receipt.calculateNotContainingFreebie())
        showReceipt()
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

    private fun checkMembership(price: Int) {
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
}