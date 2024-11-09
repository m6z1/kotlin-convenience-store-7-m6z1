package store.view

import store.receipt.PurchasedProduct
import java.text.DecimalFormat

class OutputView {

    fun printWelcomeMessage() {
        println("안녕하세요. W편의점입니다.")
        println("현재 보유하고 있는 상품입니다.\n")
    }

    fun printProducts(products: List<List<String>>) {
        products.forEach { product ->
            if (product[2] == "재고 없음") {
                println("- ${product[0]} ${THOUSAND_COMMA.format(product[1].toInt())}원 재고 없음")
                return@forEach
            }
            println("- ${product[0]} ${THOUSAND_COMMA.format(product[1].toInt())}원 ${product[2].toInt()}개 ${product[3]}")
        }
        println()
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
            println("${product.name}\t\t${product.count}\t${product.price * product.count}")
        }
        if (freebies?.isNotEmpty() == true) {
            println("===========증\t정=============")
            freebies.forEach { freebie ->
                println("${freebie.keys.first()}\t\t${freebie.values.first()}")
            }
        }
        println("==============================")
        println("총구매액\t\t${purchasedProducts.sumOf { it.count }}\t$totalAmount")
        println("행사할인\t\t\t-${promotionDiscount}")
        println("멤버십할인\t\t\t-${membershipDiscount}")
        println("내실돈\t\t\t$amountDue")
    }

    companion object {
        private val THOUSAND_COMMA = DecimalFormat("#,###")
    }
}