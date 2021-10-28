package com.teampym.onlineclothingshopapplication.data.models

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize
import java.math.BigDecimal

@Entity(tableName = "table_cart")
@Parcelize
data class Cart(
    @PrimaryKey
    var id: String,
    var userId: String,
    var productId: String,
    var inventoryId: String,
    var subTotal: Double,
    var quantity: Long = 1,
    @Ignore var product: Product = Product(),
    @Ignore var sizeInv: Inventory = Inventory()
) : Parcelable {

    constructor(): this("", "", "", "", 0.0, 1)

    val calculatedTotalPrice: Double get() = quantity.toDouble() * product.price
    init {
        subTotal = calculatedTotalPrice
    }

    @Ignore val subTotalBig = subTotal.toBigDecimal()
}