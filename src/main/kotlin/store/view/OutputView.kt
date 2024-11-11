package store.view

import store.products.Product
import store.products.ProductsManager.Companion.INITIAL_QUANTITY
import store.products.ProductsManager.Companion.NONE_PROMOTION
import store.receipt.PurchasedProduct
import java.text.DecimalFormat

class OutputView {

    fun printWelcomeMessage() {
        println("안녕하세요. W편의점입니다.")
        println("현재 보유하고 있는 상품입니다.\n")
    }

    fun printProducts(products: List<Product>) {
        products.forEach { product ->
            val promotion = if (product.promotion == NONE_PROMOTION) "" else product.promotion
            if (product.quantity == INITIAL_QUANTITY) {
                println("- ${product.name} ${THOUSAND_COMMA.format(product.price)}원 재고 없음 $promotion")
                return@forEach
            }
            println("- ${product.name} ${THOUSAND_COMMA.format(product.price)}원 ${product.quantity}개 $promotion")
        }
        println()
    }

    fun printErrorMessage(errorMessage: String?) {
        println(errorMessage)
    }

    fun printReceipt(
        purchasedProducts: List<PurchasedProduct>,
        freebies: List<Map<String, Int>>?,
        totalAmount: Int,
        promotionDiscount: Int,
        membershipDiscount: Int,
        amountDue: Int
    ) {
        println("===========W 편의점=============")
        println("상품명\t\t수량\t금액")
        purchasedProducts.forEach { product ->
            val formattedPrice = THOUSAND_COMMA.format(product.price * product.count)
            println("${product.name}\t\t${product.count}\t$formattedPrice")
        }
        if (freebies?.isNotEmpty() == true) {
            println("===========증\t정=============")
            freebies.forEach { freebie ->
                println("${freebie.keys.first()}\t\t${freebie.values.first()}")
            }
        }
        println("==============================")
        println(
            "총구매액\t\t${THOUSAND_COMMA.format(purchasedProducts.sumOf { it.count })}\t${
                THOUSAND_COMMA.format(
                    totalAmount
                )
            }"
        )
        println("행사할인\t\t\t-${THOUSAND_COMMA.format(promotionDiscount)}")
        println("멤버십할인\t\t\t-${THOUSAND_COMMA.format(membershipDiscount)}")
        println("내실돈\t\t\t${THOUSAND_COMMA.format(amountDue)}")
    }

    companion object {
        private val THOUSAND_COMMA = DecimalFormat("#,###")
    }
}