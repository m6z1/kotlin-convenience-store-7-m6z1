package store.view

import camp.nextstep.edu.missionutils.Console

class InputView {

    fun readProductsToPurchase(): List<Map<String, Int>> {
        println("구매하실 상품명과 수량을 입력해 주세요. (예: [사이다-2],[감자칩-1])")
        val inputLines = Console.readLine().split(COMMA_DELIMITER)
        inputLines.forEach { inputLine -> validateInputLine(inputLine) }

        return inputLines.map { inputLine ->
            val product = formatProductToPurchase(inputLine)
            val (name, quantity) = product.split(DASH_DELIMITER)
            validateProductToPurchase(quantity)
            mapOf(name to quantity.toInt())
        }
    }

    private fun validateInputLine(inputLine: String) {
        require(inputLine.first() == START_FORM_OF_PRODUCT_TO_PURCHASE && inputLine.last() == END_FORM_OF_PRODUCT_TO_PURCHASE) { "[ERROR] 올바르지 않은 형식으로 입력했습니다. 다시 입력해 주세요." }
        require(inputLine.isNotBlank()) { "[ERROR] 올바르지 않은 형식으로 입력했습니다. 다시 입력해 주세요." }
        require(inputLine.contains(BLANK).not()) { "[ERROR] 올바르지 않은 형식으로 입력했습니다. 다시 입력해 주세요." }
    }

    private fun formatProductToPurchase(inputLine: String): String {
        val product = inputLine
            .replace(START_FORM_OF_PRODUCT_TO_PURCHASE.toString(), EMPTY_VALUE)
            .replace(END_FORM_OF_PRODUCT_TO_PURCHASE.toString(), EMPTY_VALUE)
        return product
    }

    private fun validateProductToPurchase(quantity: String) {
        require(quantity.toIntOrNull() != null) { "[ERROR] 올바르지 않은 형식으로 입력했습니다. 다시 입력해 주세요." }
        require(quantity.toInt() > 0) { "[ERROR] 잘못된 입력입니다. 다시 입력해 주세요." }
    }

    fun readMembershipState(): String {
        println("멤버십 할인을 받으시겠습니까? (Y/N)")
        return Console.readLine()
    }

    fun readAddingFreebie(productName: String): String {
        println("현재 ${productName}은(는) 1개를 무료로 더 받을 수 있습니다. 추가하시겠습니까? (Y/N)")
        return Console.readLine()
    }

    fun readRegularPriceToPay(productName: String, regularPriceToPayCount: Int): String {
        println("현재 $productName ${regularPriceToPayCount}개는 프로모션 할인이 적용되지 않습니다. 그래도 구매하시겠습니까? (Y/N)")
        return Console.readLine()
    }

    fun readMorePurchase(): String {
        println("감사합니다. 구매하고 싶은 다른 상품이 있나요? (Y/N)")
        return Console.readLine()
    }

    companion object {
        private const val COMMA_DELIMITER = ","
        private const val DASH_DELIMITER = "-"
        private const val START_FORM_OF_PRODUCT_TO_PURCHASE = '['
        private const val END_FORM_OF_PRODUCT_TO_PURCHASE = ']'
        private const val EMPTY_VALUE = ""
        private const val BLANK = " "
    }
}