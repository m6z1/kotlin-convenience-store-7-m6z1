package store.receipt

class Receipt {
    private val purchasedProducts: MutableList<PurchasedProduct> = mutableListOf()
    private var membershipDiscount = 0

    fun addPurchasedProduct(product: PurchasedProduct) {
        purchasedProducts.add(product)
    }

    fun addMembershipDiscount(price: Int) {
        membershipDiscount += price
    }
}