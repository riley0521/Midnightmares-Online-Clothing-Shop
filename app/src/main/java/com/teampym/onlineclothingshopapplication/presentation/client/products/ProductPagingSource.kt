package com.teampym.onlineclothingshopapplication.presentation.client.products

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.bumptech.glide.load.HttpException
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import com.teampym.onlineclothingshopapplication.data.models.Product
import kotlinx.coroutines.tasks.await
import java.io.IOException

private const val DEFAULT_PAGE_INDEX = 1

class ProductPagingSource(
    private val queryProducts: Query
) : PagingSource<QuerySnapshot, Product>() {

    val productCollectionRef = FirebaseFirestore.getInstance().collection("Products")

    override fun getRefreshKey(state: PagingState<QuerySnapshot, Product>): QuerySnapshot? {
        TODO("Not yet implemented")
    }

    override suspend fun load(params: LoadParams<QuerySnapshot>): LoadResult<QuerySnapshot, Product> {

        return try {

            val currentPage = params.key ?: queryProducts
                .get()
                .await()

            var nextPage: QuerySnapshot? = null

            if (currentPage.size() > 30) {
                val lastVisibleItem = currentPage!!.documents[currentPage.size() - 1]
                nextPage = queryProducts.startAfter(lastVisibleItem).get().await()
            }

            val productList = mutableListOf<Product>()
            for (product in currentPage.documents) {

                val obj = product.toObject(Product::class.java)
                obj?.let {
                    productList.add(it)
                }

            }

            LoadResult.Page(
                data = productList,
                prevKey = null,
                nextKey = nextPage
            )
        } catch (exception: IOException) {
            return LoadResult.Error(exception)
        } catch (exception: HttpException) {
            return LoadResult.Error(exception)
        }
    }
}