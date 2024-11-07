package store

class StoreController(
    private val inputView: InputView,
    private val outputView: OutputView,
    private val productsManager: ProductsManager,
) {

    fun start() {
        welcome()
        val productsToPurchase = inputView.readProductsToPurchase()
        require(productsToPurchase.all {
            productsManager.isPossiblePurchase(
                productName = it.keys.toString(),
                quantityToPurchase = it.size
            )
        }) { "[ERROR] 재고 수량을 초과하여 구매할 수 없습니다. 다시 입력해 주세요." }
    }

    private fun welcome() {
        outputView.printWelcomeMessage()
        outputView.printProducts(productsManager.products)
    }
}