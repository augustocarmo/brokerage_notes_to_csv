package model

import java.util.*

data class BrokerageNoteEntry(
    val date: Date,
    val square: String,
    val buyOrSell: Char,
    val marketType: String,
    val share: String,
    val quantity: Int,
    val netPrice: Double,
    val buyOrSellPrice: Double,
    val dOrC: Char
)