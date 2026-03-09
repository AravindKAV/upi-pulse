package com.upipulse.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.upipulse.data.local.entity.MerchantEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MerchantDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(merchants: List<MerchantEntity>)

    @Query("SELECT * FROM merchants WHERE merchantId = :id")
    fun observeMerchant(id: String): Flow<MerchantEntity?>
}