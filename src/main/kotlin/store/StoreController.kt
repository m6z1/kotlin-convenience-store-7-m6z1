package store

class StoreController(
    private val inputView: InputView,
    private val outputView: OutputView,
    private val productsManager: ProductsManager,
) {

    fun start() {
        welcome()
        val productsToPurchase = inputView.readProductsToPurchase()
        productsManager.validPossiblePurchase(productsToPurchase)
    }

    private fun welcome() {
        outputView.printWelcomeMessage()
        outputView.printProducts(productsManager.products)
    }
}