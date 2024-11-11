package store.view

import camp.nextstep.edu.missionutils.Console

class InputView {

    fun readProductsToPurchase(): List<Map<String, Int>> {
        val inputLines = Console.readLine().split(COMMA_DELIMITER)
        inputLines.forEach { inputLine -> validateInputLine(inputLine) }

        return inputLines.map { inputLine ->
            val product = inputLine.replace(START_FORM_OF_PRODUCT_TO_PURCHASE.toString(), EMPTY_VALUE)
                .replace(END_FORM_OF_PRODUCT_TO_PURCHASE.toString(), EMPTY_VALUE)
            val (name, quantity) = product.split(DASH_DELIMITER)

            mapOf(name to quantity.toInt())
        }
    }

    private fun validateInputLine(inputLine: String) {
        require(inputLine.first() == START_FORM_OF_PRODUCT_TO_PURCHASE && inputLine.last() == END_FORM_OF_PRODUCT_TO_PURCHASE) { "[ERROR] 올바르지 않은 형식으로 입력했습니다. 다시 입력해 주세요." }
        require(inputLine.isNotBlank()) { "[ERROR] 올바르지 않은 형식으로 입력했습니다. 다시 입력해 주세요." }
    }

    fun readMembershipState(): String {
        return Console.readLine()
    }

    fun readAddingFreebie(): String {
        return Console.readLine()
    }

    fun readRegularPriceToPay(): String {
        return Console.readLine()
    }

    fun readMorePurchase(): String {
        return Console.readLine()
    }

    companion object {
        private const val COMMA_DELIMITER = ","
        private const val DASH_DELIMITER = "-"
        private const val START_FORM_OF_PRODUCT_TO_PURCHASE = '['
        private const val END_FORM_OF_PRODUCT_TO_PURCHASE = ']'
        private const val EMPTY_VALUE = ""
    }
}