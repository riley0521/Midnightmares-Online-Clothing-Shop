package com.teampym.onlineclothingshopapplication.data.di

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.teampym.onlineclothingshopapplication.data.room.*
import com.teampym.onlineclothingshopapplication.data.repository.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideDbInstance() =
        Firebase.firestore

    @Provides
    @Singleton
    fun provideCategoryRepository(db: FirebaseFirestore) =
        CategoryRepositoryImpl(db)

    @Provides
    @Singleton
    fun provideAccountRepository(
        db: FirebaseFirestore,
        deliveryInformationRepository: DeliveryInformationRepositoryImpl,
        notificationTokenRepository: NotificationTokenRepositoryImpl,
        wishItemRepository: WishItemRepositoryImpl,
        userInformationDao: UserInformationDao
    ) = AccountRepositoryImpl(
        db,
        deliveryInformationRepository,
        notificationTokenRepository,
        wishItemRepository,
        userInformationDao
    )

    @Provides
    @Singleton
    fun provideDeliveryInformationRepository(
        db: FirebaseFirestore,
        deliveryInformationDao: DeliveryInformationDao
    ) = DeliveryInformationRepositoryImpl(
        db,
        deliveryInformationDao
    )

    @Provides
    @Singleton
    fun provideNotificationTokenRepository(
        db: FirebaseFirestore,
        notificationTokenDao: NotificationTokenDao,
        preferencesManager: PreferencesManager
    ) = NotificationTokenRepositoryImpl(
        db,
        notificationTokenDao,
        preferencesManager
    )

    @Provides
    @Singleton
    fun provideWishListRepository(
        db: FirebaseFirestore
    ) = WishItemRepositoryImpl(db)

    @Provides
    @Singleton
    fun provideCartRepository(db: FirebaseFirestore, cartDao: CartDao, productDao: ProductDao, inventoryDao: InventoryDao) =
        CartRepositoryImpl(db, cartDao, productDao, inventoryDao)

    @Provides
    @Singleton
    fun provideProductRepository(
        db: FirebaseFirestore,
        productImageRepository: ProductImageRepositoryImpl,
        productInventoryRepository: ProductInventoryRepositoryImpl,
        reviewRepository: ReviewRepositoryImpl,
        accountRepository: AccountRepositoryImpl
    ) = ProductRepositoryImpl(
        db,
        productImageRepository,
        productInventoryRepository,
        reviewRepository,
        accountRepository
    )

    @Provides
    @Singleton
    fun provideProductImageRepository(
        db: FirebaseFirestore
    ) = ProductImageRepositoryImpl(db)

    @Provides
    @Singleton
    fun provideProductInventoryRepository(
        db: FirebaseFirestore
    ) = ProductInventoryRepositoryImpl(db)

    @Provides
    @Singleton
    fun provideReviewRepository(
        db: FirebaseFirestore
    ) = ReviewRepositoryImpl(db)

    @Provides
    @Singleton
    fun provideOrderRepository(
        db: FirebaseFirestore,
        orderDetailRepository: OrderDetailRepositoryImpl,
        productRepository: ProductRepositoryImpl
    ) = OrderRepositoryImpl(db, orderDetailRepository, productRepository)

    @Provides
    @Singleton
    fun provideOrderDetailRepository(
        db: FirebaseFirestore
    ) = OrderDetailRepositoryImpl(
        db
    )

}