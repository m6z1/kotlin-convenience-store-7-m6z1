package store.promotion

import java.time.LocalDate

data class Promotion(
    val name: String,
    val countOfBuy: Int,
    val countOfGet: Int,
    val startDate: LocalDate,
    val endDate: LocalDate,
)
