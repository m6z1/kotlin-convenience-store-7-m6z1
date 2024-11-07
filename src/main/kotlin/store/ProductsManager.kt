package store

import java.io.File

class ProductsManager {
    private val _products: MutableList<List<String>> = emptyList<List<String>>().toMutableList()
    val products: List<List<String>> get() = _products

    init {
        updateProducts()
    }

    private fun updateProducts() {
        val productsLine = readProductsFile()
        val productsLineExceptTitle = productsLine.filter { it != productsLine[0] }
        productsLineExceptTitle.forEach { product ->
            _products.add(product.split(","))
        }
    }

    private fun readProductsFile(): List<String> {
        val path = "src/main/resources/products.md"
        val productsLine = emptyList<String>().toMutableList()
        File(path).forEachLine { productsLine.add(it) }

        return productsLine
    }

    fun isPossiblePay(productName: String, quantityToPurchase: Int): Boolean {
        val productsToPurchase = _products.filter { it[0] == productName }
        val purchasableQuantity = productsToPurchase.sumOf { it[2].toInt() }

        return purchasableQuantity >= quantityToPurchase
    }
}