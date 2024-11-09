package store

import store.products.ProductsManager
import store.promotion.Promotions
import store.view.InputView
import store.view.OutputView

fun main() {
    val inputView = InputView()
    val outputView = OutputView()
    val productsManager = ProductsManager()
    val promotions = Promotions()
    val storeController = StoreController(inputView, outputView, productsManager, promotions)
    storeController.start()
}
