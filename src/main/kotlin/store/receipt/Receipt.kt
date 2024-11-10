package store.receipt

class Receipt {
    private val _purchasedProducts: MutableList<PurchasedProduct> = mutableListOf()
    val purchasedProducts: List<PurchasedProduct> get() = _purchasedProducts
    private val _promotions: MutableList<Map<String, Int>> = mutableListOf()
    val promotions: List<Map<String, Int>> get() = _promotions
    var membershipDiscount = 0
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
        var totalFreebiePrice = 0

        promotions.forEach { promotion ->
            purchasedProducts.forEach { product ->
                if (promotion.containsKey(product.name)) {
                    totalFreebiePrice += product.price * promotion.values.first()
                }
            }
        }

        return totalFreebiePrice
    }

    fun calculateNotContainingFreebie(): Int {
        return _purchasedProducts.filter { product ->
            _promotions.none { promotion -> promotion.containsKey(product.name) }
        }.sumOf { it.price * it.count }
    }

    fun calculateAmountDue(): Int {
        return calculateTotalAmount() - calculateFreebiesPrice() - membershipDiscount
    }
}