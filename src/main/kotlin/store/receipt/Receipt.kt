package store.receipt

class Receipt {
    private val _purchasedProducts: MutableList<PurchasedProduct> = mutableListOf()
    val purchasedProducts: List<PurchasedProduct> get() = _purchasedProducts

    private val _promotions: MutableList<Map<String, Int>> = mutableListOf()
    val promotions: List<Map<String, Int>> get() = _promotions

    var membershipDiscount = ZERO
        private set

    fun addPurchasedProduct(product: PurchasedProduct) {
        _purchasedProducts.add(product)
    }

    fun addPromotionProduct(promotionProduct: Map<String, Int>) {
        _promotions.add(promotionProduct)
    }

    fun addMembershipDiscount(price: Int) {
        membershipDiscount += price
    }

    fun calculateTotalAmount(): Int {
        return _purchasedProducts.sumOf { it.price * it.count }
    }

    fun calculateFreebiesPrice(): Int {
        var totalFreebiePrice = ZERO
        promotions.forEach { promotion ->
            purchasedProducts.forEach { product ->
                totalFreebiePrice = addFreebiesPrice(promotion, product, totalFreebiePrice)
            }
        }

        return totalFreebiePrice
    }

    private fun addFreebiesPrice(
        promotion: Map<String, Int>,
        product: PurchasedProduct,
        totalFreebiePrice: Int
    ): Int {
        var totalPrice = totalFreebiePrice
        if (promotion.containsKey(product.name)) {
            totalPrice += product.price * promotion.values.first()
        }
        return totalPrice
    }

    fun calculateNotContainingFreebie(): Int {
        return _purchasedProducts.filter { product ->
            _promotions.none { promotion -> promotion.containsKey(product.name) }
        }.sumOf { it.price * it.count }
    }

    fun calculateAmountDue(): Int {
        return calculateTotalAmount() - calculateFreebiesPrice() - membershipDiscount
    }

    fun reset() {
        _purchasedProducts.clear()
        _promotions.clear()
        membershipDiscount = ZERO
    }

    companion object {
        private const val ZERO = 0
    }
}