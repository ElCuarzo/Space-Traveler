package com.example.spacetraveler.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface OfflineOperationDao {

    @Insert
    suspend fun insertOperation(operation: OfflineOperation)

    @Query("SELECT * FROM offline_operations")
    suspend fun getAllOperations(): List<OfflineOperation>

    @Query("DELETE FROM offline_operations WHERE id = :operationId")
    suspend fun deleteOperationById(operationId: Int)

}
