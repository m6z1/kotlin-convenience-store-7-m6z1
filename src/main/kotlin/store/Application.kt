package store

fun main() {
    val inputView = InputView()
    val outputView = OutputView()
    val productsManager = ProductsManager()
    val promotions = Promotions()
    val storeController = StoreController(inputView, outputView, productsManager, promotions)
    storeController.start()
}
