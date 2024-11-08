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

    fun validPossiblePurchase(productsToPurchase: List<Map<String, Int>>) {
        productsToPurchase.forEach { product ->
            val productName = product.keys.first()
            val quantityToPurchase = product.values.first()
            val matchingProducts = _products.filter { it[0] == productName }
            require(matchingProducts.isNotEmpty()) { "[ERROR] 존재하지 않는 상품입니다. 다시 입력해 주세요." }

            val purchasableQuantity = matchingProducts.sumOf { it[2].toIntOrNull() ?: 0 }
            require(purchasableQuantity >= quantityToPurchase) { "[ERROR] 재고 수량을 초과하여 구매할 수 없습니다. 다시 입력해 주세요." }
        }
    }

    fun findPromotion(productName: String): String? {
        return products.firstOrNull { product ->
            product[0] == productName
        }?.get(3)
    }
}