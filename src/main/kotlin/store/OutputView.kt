package store

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
    }

    companion object {
        private val THOUSAND_COMMA = DecimalFormat("#,###")
    }
}