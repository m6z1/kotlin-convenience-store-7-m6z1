package store

import java.io.File

class ProductsManager {
    private val _products: MutableList<MutableList<String>> = mutableListOf()
    val products: List<List<String>> get() = _products

    init {
        updateProducts()
    }

    private fun updateProducts() {
        val productsLine = readProductsFile()
        val productsLineExceptTitle = productsLine.filter { it != productsLine[0] }
        productsLineExceptTitle.forEach { product ->
            _products.add(product.split(",").toMutableList())
        }

        formatNullToBlank()
        addOutOfProductNonePromotion()
    }

    private fun readProductsFile(): List<String> {
        val path = "src/main/resources/products.md"
        val productsLine = emptyList<String>().toMutableList()
        File(path).forEachLine { productsLine.add(it) }

        return productsLine
    }

    private fun formatNullToBlank() {
        _products.forEach { product ->
            if (product[3] == "null") {
                product[3] = ""
            }
        }
    }

    private fun addOutOfProductNonePromotion() {
        val sameProductCounts = _products.groupBy { it[0] }.mapValues { it.value.size }
        val updateProducts = mutableListOf<MutableList<String>>()

        for (product in _products) {
            val productName = product[0]
            if (sameProductCounts[productName] == 1 && product[3] != "") {
                updateProducts.add(product)
                updateProducts.add(mutableListOf(product[0], product[1], "재고 없음"))
            } else {
                updateProducts.add(product)
            }
        }

        _products.clear()
        _products.addAll(updateProducts)
    }

    fun isPossiblePurchase(productName: String, quantityToPurchase: Int): Boolean {
        val productsToPurchase = _products.filter { it[0] == productName }
        val purchasableQuantity = productsToPurchase.sumOf { it[2].toInt() }

        return purchasableQuantity >= quantityToPurchase
    }
}