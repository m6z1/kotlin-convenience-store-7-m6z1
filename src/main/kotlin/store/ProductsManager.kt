package store

import java.io.File

class ProductsManager {
    private val _products: MutableList<List<String>> = emptyList<List<String>>().toMutableList()

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
}