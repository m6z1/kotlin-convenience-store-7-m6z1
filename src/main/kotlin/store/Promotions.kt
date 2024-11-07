package store

import java.io.File
import java.time.LocalDate

class Promotions {
    private val promotions: MutableList<List<String>> = emptyList<List<String>>().toMutableList()

    init {
        updatePromotions()
    }

    private fun updatePromotions() {
        val productsLine = readProductsFile()
        val productsLineExceptTitle = productsLine.filter { it != productsLine[0] }
        productsLineExceptTitle.forEach { product ->
            promotions.add(product.split(","))
        }
    }

    private fun readProductsFile(): List<String> {
        val path = "src/main/resources/promotions.md"
        val productsLine = emptyList<String>().toMutableList()
        File(path).forEachLine { productsLine.add(it) }

        return productsLine
    }

    fun isPossiblePromotionDiscount(promotionName: String, today: LocalDate): Boolean {
        val promotion = promotions.firstOrNull { it[0] == promotionName } ?: return false

        val startDate = LocalDate.parse(promotion[3])
        val endDate = LocalDate.parse(promotion[4])
        return today in startDate..endDate
    }
}