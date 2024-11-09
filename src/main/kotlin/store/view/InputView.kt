package store.view

import camp.nextstep.edu.missionutils.Console

class InputView {

    fun readProductsToPurchase(): List<Map<String, Int>> {
        println("구매하실 상품명과 수량을 입력해 주세요. (예: [사이다-2],[감자칩-1])")
        val inputLines = Console.readLine().split(",")
        return inputLines.map { inputLine ->
            val product = inputLine.replace("[", "").replace("]", "")
            val (name, quantity) = product.split("-")

            mapOf(name to quantity.toInt())
        }
    }

    fun readMembershipState(): String {
        println("멤버십 할인을 받으시겠습니까? (Y/N)")
        return Console.readLine()
    }

    fun readAddingFreebie(productName: String): String {
        println("현재 ${productName}은(는) 1개를 무료로 더 받을 수 있습니다. 추가하시겠습니까? (Y/N)")
        return Console.readLine()
    }
}