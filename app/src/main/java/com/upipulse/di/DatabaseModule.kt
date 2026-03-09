package com.upipulse.di

import android.content.Context
import androidx.room.Room
import com.upipulse.data.local.dao.BudgetDao
import com.upipulse.data.local.dao.TransactionDao
import com.upipulse.data.local.db.UpiPulseDatabase
import com.upipulse.data.repository.TransactionRepository
import com.upipulse.data.repository.TransactionRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    @Singleton
    abstract fun bindTransactionRepository(
        impl: TransactionRepositoryImpl
    ): TransactionRepository
}

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): UpiPulseDatabase =
        Room.databaseBuilder(context, UpiPulseDatabase::class.java, "upi_pulse.db")
            .fallbackToDestructiveMigration()
            .build()

    @Provides
    fun provideTransactionDao(database: UpiPulseDatabase): TransactionDao = database.transactionDao()

    @Provides
    fun provideBudgetDao(database: UpiPulseDatabase): BudgetDao = database.budgetDao()
}