package store.products

import store.receipt.PurchasedProduct
import java.io.File

class ProductsManager {
    private val _products: MutableList<Product> = mutableListOf()
    val products: List<Product> get() = _products

    init {
        updateProducts()
    }

    private fun updateProducts() {
        val productsLine = readProductsFile().drop(1)
        productsLine.forEach { productLine ->
            val productData = productLine.split(",")
            val product = Product(
                name = productData[0],
                price = productData[1].toInt(),
                quantity = productData[2].toInt(),
                promotion = productData[3],
            )
            _products.add(product)
        }
        addOutOfProductNonePromotion()
    }

    private fun readProductsFile(): List<String> {
        val path = "src/main/resources/products.md"
        return File(path).useLines { it.toList() }
    }

    private fun addOutOfProductNonePromotion() {
        val sameProductCounts = _products.groupBy { it.name }.mapValues { it.value.size }
        val updateProducts = mutableListOf<Product>()

        products.forEach { product ->
            if (sameProductCounts.size == 1 && product.promotion != "null") {
                updateProducts.add(product)
                updateProducts.add(
                    Product(
                        product.name, product.price, 0, "null"
                    )
                )
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
            val matchingProducts = _products.filter { it.name == productName }
            require(matchingProducts.isNotEmpty()) { "[ERROR] 존재하지 않는 상품입니다. 다시 입력해 주세요." }

            val purchasableQuantity = matchingProducts.sumOf { it.quantity }
            require(purchasableQuantity >= quantityToPurchase) { "[ERROR] 재고 수량을 초과하여 구매할 수 없습니다. 다시 입력해 주세요." }
        }
    }

    fun findProductPromotion(productName: String): String? {
        return products.firstOrNull { it.name == productName }?.promotion?.takeIf { it.isNotEmpty() }
    }

    fun findPromotionStock(productName: String): Int {
        val promotionProduct = products.firstOrNull { it.name == productName && it.promotion != "null" }
            ?: throw IllegalArgumentException("[ERROR] 프로모션 재고가 없습니다.")
        return promotionProduct.quantity
    }

    fun findProductPrice(productName: String): Int {
        return products.first { it.name == productName }.price
    }

    fun updateLatestProduct(purchasedProduct: PurchasedProduct, isPromotionPeriod: Boolean) {
        if (isPromotionPeriod) {
            products.find { product -> product.name == purchasedProduct.name }?.quantity?.minus(purchasedProduct.count)
            return
        }
        products.find { product -> product.name == purchasedProduct.name && product.promotion == "null" }?.quantity?.minus(
            purchasedProduct.count
        )
    }
}