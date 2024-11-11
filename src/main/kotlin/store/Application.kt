package store

import store.products.ProductsManager
import store.promotion.Promotions
import store.receipt.Receipt
import store.view.InputView
import store.view.OutputView

fun main() {
    val inputView = InputView()
    val outputView = OutputView()
    val productsManager = ProductsManager()
    val promotions = Promotions()
    val receipt = Receipt()
    val storeController = StoreController(inputView, outputView, productsManager, promotions, receipt)
    storeController.start()
}
