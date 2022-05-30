package model

import java.util.*

data class BrokerageNoteEntry(
    val date: Date,
    val square: String,
    val buyOrSell: String,
    val marketType: String,
    val share: String,
    val quantity: String,
    val netPrice: String,
    val buyOrSellPrice: String,
    val dOrC: String
)