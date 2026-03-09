package com.upipulse.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.upipulse.data.local.dao.BudgetDao
import com.upipulse.data.local.dao.MerchantDao
import com.upipulse.data.local.dao.TransactionDao
import com.upipulse.data.local.entity.BudgetEntity
import com.upipulse.data.local.entity.MerchantEntity
import com.upipulse.data.local.entity.TransactionEntity

@Database(
    entities = [
        TransactionEntity::class,
        MerchantEntity::class,
        BudgetEntity::class
    ],
    version = 1,
    exportSchema = false // <- removes Room schema warning
)
@TypeConverters(Converters::class)
abstract class UpiPulseDatabase : RoomDatabase() {
    abstract fun transactionDao(): TransactionDao
    abstract fun merchantDao(): MerchantDao
    abstract fun budgetDao(): BudgetDao
}