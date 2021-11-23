package com.teampym.onlineclothingshopapplication.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import com.teampym.onlineclothingshopapplication.data.models.ProductImage
import com.teampym.onlineclothingshopapplication.data.util.PRODUCTS_COLLECTION
import com.teampym.onlineclothingshopapplication.data.util.PRODUCT_IMAGES_SUB_COLLECTION
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class ProductImageRepositoryImpl @Inject constructor(
    db: FirebaseFirestore
) {

    private val productCollectionRef = db.collection(PRODUCTS_COLLECTION)

    // TODO("ProductImages Collection operation - Add and Delete")

    suspend fun getAll(productId: String): List<ProductImage> {
        val productImagesQuery = productCollectionRef
            .document(productId)
            .collection(PRODUCT_IMAGES_SUB_COLLECTION)
            .get()
            .await()

        val productImageList = mutableListOf<ProductImage>()

        if (productImagesQuery != null) {
            for (document in productImagesQuery.documents) {
                val copy = document.toObject<ProductImage>()!!
                    .copy(id = document.id, productId = productId)
                productImageList.add(copy)
            }
        }
        return productImageList
    }

    suspend fun create(productImage: ProductImage): Boolean {
        var isCreated = true
        productCollectionRef
            .document(productImage.productId)
            .collection(PRODUCT_IMAGES_SUB_COLLECTION)
            .add(productImage)
            .addOnSuccessListener {
            }.addOnFailureListener {
                isCreated = false
                return@addOnFailureListener
            }
        return isCreated
    }

    suspend fun delete(productImage: ProductImage): Boolean {
        var isDeleted = true
        productCollectionRef
            .document(productImage.productId)
            .collection(PRODUCT_IMAGES_SUB_COLLECTION)
            .document(productImage.id)
            .delete()
            .addOnSuccessListener {
            }.addOnFailureListener {
                isDeleted = false
                return@addOnFailureListener
            }
        return isDeleted
    }
}
