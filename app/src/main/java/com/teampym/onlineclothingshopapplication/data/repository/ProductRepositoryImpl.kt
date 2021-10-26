package com.teampym.onlineclothingshopapplication.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.facebook.internal.BoltsMeasurementEventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions
import com.teampym.onlineclothingshopapplication.data.models.Cart
import com.teampym.onlineclothingshopapplication.data.models.Inventory
import com.teampym.onlineclothingshopapplication.data.models.Product
import com.teampym.onlineclothingshopapplication.data.models.ProductImage
import com.teampym.onlineclothingshopapplication.presentation.client.products.ProductPagingSource
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class ProductRepositoryImpl @Inject constructor(
    private val db: FirebaseFirestore
) {

    // READ Operation
    private val productCollectionRef = db.collection("Products")

    fun getProductsPagingSource(queryProducts: Query) =
        Pager(
            PagingConfig(
                pageSize = 30
            )
        ) {
            ProductPagingSource(queryProducts)
        }

    // TODO("CRUD Operations for Product Collection")
    suspend fun getOneProductWithImagesAndInventories(productId: String): Product? {

        val productsQuery = productCollectionRef.document(productId).get().await()
        if(productsQuery != null) {
            // get all inventories
            val inventoryQuery = productCollectionRef.document(productId).collection("inventories").get().await()
            val inventoryList = mutableListOf<Inventory>()
            for(inventory in inventoryQuery) {
                inventoryList.add(
                    Inventory(
                        id = inventory.id,
                        productId = inventory["productId"].toString(),
                        size = inventory["size"].toString(),
                        stock = inventory["stock"].toString().toLong() - inventory["committed"].toString().toLong(),
                        committed = inventory["committed"].toString().toLong(),
                        sold = inventory["sold"].toString().toLong(),
                        returned = inventory["returned"].toString().toLong(),
                        restockLevel = inventory["restockLevel"].toString().toLong()
                    )
                )
            }

            // get all productImages
            val productImagesQuery = productCollectionRef.document(productId).collection("productImages").get().await()
            val productImageList = mutableListOf<ProductImage>()
            for(productImage in productImagesQuery) {
                productImageList.add(
                    ProductImage(
                        id = productImage.id,
                        productId = productImage["productId"].toString(),
                        imageUrl = productImage["imageUrl"].toString()
                    )
                )
            }

            return Product(
                id = "",
                categoryId = "",
                name = "",
                description = "",
                imageUrl = "",
                price = "0".toBigDecimal(),
                flag = "",
                inventories = inventoryList,
                productImages = productImageList
            )
        }
        return null
    }

    suspend fun createProduct(product: Product): Product? {
        val result = productCollectionRef.add(product).await()
        if (result != null)
            return product.copy(id = result.id)
        return null
    }

    suspend fun updateProduct(product: Product): Boolean {
        val productQuery = productCollectionRef.document(product.id).get().await()
        if (productQuery != null) {
            val productToUpdateMap = mapOf<String, Any>(
                "name" to product.name,
                "description" to product.description,
                "imageUrl" to product.imageUrl,
                "price" to product.price,
                "flag" to product.flag
            )

            val result = productCollectionRef.document(product.id)
                .set(productToUpdateMap, SetOptions.merge()).await()
            return result != null
        }
        return false
    }

    suspend fun deleteProduct(productId: String): Boolean {
        val result = productCollectionRef.document(productId).delete().await()
        return result != null
    }

    // TODO("Inventory Collection operation - Add Stock, Add New Inventory(size), Delete Inventory")
    suspend fun deleteInventory(productId: String, inventoryId: String): Boolean {
        val result = productCollectionRef
            .document(productId)
            .collection("inventories")
            .document(inventoryId)
            .delete().await()
        return result != null
    }

    // SHIPPING
    suspend fun deductStockToCommittedCount(cart: List<Cart>) {
        // TODO("Use Product Repository to update the deduct number of stock and add it to committed")
        for (item in cart) {
            val inventoryQuery =
                productCollectionRef.document(item.product.id).collection("inventories")
                    .document(item.selectedSizeFromInventory.id).get().await()
            if (inventoryQuery != null) {
                val stockNewCount = inventoryQuery["stock"].toString().toLong() - item.quantity
                val committedNewCount =
                    inventoryQuery["committed"].toString().toLong() + item.quantity
                val updateProductsInventoryMap = mapOf<String, Any>(
                    "stock" to stockNewCount,
                    "committed" to committedNewCount
                )

                productCollectionRef.document(item.product.id).collection("inventories")
                    .document(item.selectedSizeFromInventory.id)
                    .set(updateProductsInventoryMap, SetOptions.merge()).await()
            }
        }
    }

    // CANCELED
    suspend fun deductCommittedToStockCount(cart: List<Cart>) {
        for (item in cart) {
            val inventoryQuery =
                productCollectionRef.document(item.product.id).collection("inventories")
                    .document(item.selectedSizeFromInventory.id).get().await()
            if (inventoryQuery != null) {
                val committedNewCount =
                    inventoryQuery["committed"].toString().toLong() - item.quantity
                val stockNewCount = inventoryQuery["stock"].toString().toLong() + item.quantity
                val updateProductsInventoryMap = mapOf<String, Any>(
                    "stock" to stockNewCount,
                    "committed" to committedNewCount
                )

                productCollectionRef.document(item.product.id).collection("inventories")
                    .document(item.selectedSizeFromInventory.id)
                    .set(updateProductsInventoryMap, SetOptions.merge()).await()
            }
        }
    }

    // COMPLETED
    suspend fun deductCommittedToSoldCount(cart: List<Cart>) {
        for (item in cart) {
            val inventoryQuery =
                productCollectionRef.document(item.product.id).collection("inventories")
                    .document(item.selectedSizeFromInventory.id).get().await()
            if (inventoryQuery != null) {
                val committedNewCount =
                    inventoryQuery["committed"].toString().toLong() - item.quantity
                val soldNewCount = inventoryQuery["sold"].toString().toLong() + item.quantity
                val updateProductsInventoryMap = mapOf<String, Any>(
                    "committed" to committedNewCount,
                    "sold" to soldNewCount
                )

                productCollectionRef.document(item.product.id).collection("inventories")
                    .document(item.selectedSizeFromInventory.id)
                    .set(updateProductsInventoryMap, SetOptions.merge()).await()
            }
        }
    }

    // RETURNED
    suspend fun deductSoldToReturnedCount(cart: List<Cart>) {
        for (item in cart) {
            val inventoryQuery =
                productCollectionRef.document(item.product.id).collection("inventories")
                    .document(item.selectedSizeFromInventory.id).get().await()
            if (inventoryQuery != null) {
                val soldNewCount = inventoryQuery["sold"].toString().toLong() - item.quantity
                val returnedNewCount =
                    inventoryQuery["returned"].toString().toLong() + item.quantity
                val updateProductsInventoryMap = mapOf<String, Any>(
                    "sold" to soldNewCount,
                    "returned" to returnedNewCount
                )

                productCollectionRef.document(item.product.id).collection("inventories")
                    .document(item.selectedSizeFromInventory.id)
                    .set(updateProductsInventoryMap, SetOptions.merge()).await()
            }
        }
    }

}