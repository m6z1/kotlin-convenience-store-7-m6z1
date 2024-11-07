package store

class StoreController(
    private val inputView: InputView,
    private val outputView: OutputView,
    private val productsManager: ProductsManager,
) {

    fun welcome() {
        outputView.printWelcomeMessage()
        outputView.printProducts(productsManager.products)
    }
}