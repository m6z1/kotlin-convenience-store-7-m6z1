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

    fun printProductsToPurchaseMessage() {
        println("구매하실 상품명과 수량을 입력해 주세요. (예: [사이다-2],[감자칩-1])")
    }

    fun printErrorMessage(errorMessage: String?) {
        println(errorMessage)
    }

    fun printMembershipMessage() {
        println("멤버십 할인을 받으시겠습니까? (Y/N)")
    }

    fun printAddingFreebieMessage(productName: String) {
        println("현재 ${productName}은(는) 1개를 무료로 더 받을 수 있습니다. 추가하시겠습니까? (Y/N)")
    }

    fun printRegularPriceToPayMessage(productName: String, regularPriceToPayCount: Int) {
        println("현재 $productName ${regularPriceToPayCount}개는 프로모션 할인이 적용되지 않습니다. 그래도 구매하시겠습니까? (Y/N)")
    }

    fun printMorePurchaseMessage() {
        println("감사합니다. 구매하고 싶은 다른 상품이 있나요? (Y/N)")
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