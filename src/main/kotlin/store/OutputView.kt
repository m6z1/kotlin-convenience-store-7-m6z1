package store

import java.text.DecimalFormat

class OutputView {

    fun printWelcomeMessage() {
        println("안녕하세요. W편의점입니다.")
        println("현재 보유하고 있는 상품입니다.\n")
    }

    fun printProducts(products: List<List<String>>) {
        products.forEach { product ->
            val productCount: String = if (product[2].toInt() == 0) "재고없음" else product[2].toInt().toString() + "개"
            println("- ${product[0]} ${THOUSAND_COMMA.format(product[1].toInt())}원 $productCount ${product[3]}")
        }
    }

    companion object {
        private val THOUSAND_COMMA = DecimalFormat("#,###")
    }
}