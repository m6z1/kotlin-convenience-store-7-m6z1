package store.products

import store.fileReader.FileReader
import store.receipt.PurchasedProduct

class ProductsManager {
    private val _products: MutableList<Product> = mutableListOf()
    val products: List<Product> get() = _products

    private val fileReader = FileReader(PRODUCTS_PATH)

    init {
        updateProducts()
    }

    private fun updateProducts() {
        fileReader.readFile().drop(FILE_TITLE).forEach { productLine ->
            val productData = productLine.split(FILE_DELIMITER)
            val product = Product(
                name = productData[PRODUCT_NAME_INDEX],
                price = productData[PRODUCT_PRICE_INDEX].toInt(),
                quantity = productData[PRODUCT_QUANTITY_INDEX].toInt(),
                promotion = productData[PRODUCT_PROMOTION_INDEX],
            )
            _products.add(product)
        }
        checkNonePromotionsProduct()
    }

    private fun checkNonePromotionsProduct() {
        val sameProductCounts = _products.groupBy { it.name }.mapValues { it.value.size }
        val updateProducts = mutableListOf<Product>()

        products.forEach { product ->
            addNonePromotionProduct(sameProductCounts, product, updateProducts)
        }
        _products.clear()
        _products.addAll(updateProducts)
    }

    private fun addNonePromotionProduct(
        sameProductCounts: Map<String, Int>,
        product: Product,
        updateProducts: MutableList<Product>
    ) {
        if (sameProductCounts[product.name] == STANDARD_ADDING_NONE_PROMOTION_PRODUCT && product.promotion != NONE_PROMOTION) {
            updateProducts.add(product)
            updateProducts.add(
                Product(
                    product.name, product.price, INITIAL_QUANTITY, NONE_PROMOTION
                )
            )
        } else {
            updateProducts.add(product)
        }
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

    fun findProductPromotion(productName: String): String {
        return products.first { it.name == productName }.promotion
    }

    fun findPromotionStock(productName: String): Int {
        val promotionProduct = products.first { it.name == productName && it.promotion != NONE_PROMOTION }
        return promotionProduct.quantity
    }

    fun findProductPrice(productName: String): Int {
        return products.first { it.name == productName }.price
    }

    fun updateLatestProduct(purchasedProduct: PurchasedProduct, isPromotionPeriod: Boolean) {
        _products.forEachIndexed { index, product ->
            if (isPromotionPeriod && product.name == purchasedProduct.name && product.promotion != NONE_PROMOTION) {
                _products[index] = product.copy(quantity = product.quantity - purchasedProduct.count)
                return@forEachIndexed
            }

            if (!isPromotionPeriod && product.name == purchasedProduct.name && product.promotion == NONE_PROMOTION) {
                _products[index] = product.copy(quantity = product.quantity - purchasedProduct.count)
                return@forEachIndexed
            }
        }
    }

    companion object {
        private const val PRODUCTS_PATH = "src/main/resources/products.md"
        private const val FILE_TITLE = 1
        private const val FILE_DELIMITER = ","
        private const val PRODUCT_NAME_INDEX = 0
        private const val PRODUCT_PRICE_INDEX = 1
        private const val PRODUCT_QUANTITY_INDEX = 2
        private const val PRODUCT_PROMOTION_INDEX = 3
        private const val STANDARD_ADDING_NONE_PROMOTION_PRODUCT = 1
        const val INITIAL_QUANTITY = 0
        const val NONE_PROMOTION = "null"
    }
}