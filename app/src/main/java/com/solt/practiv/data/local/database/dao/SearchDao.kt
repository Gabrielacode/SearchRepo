package com.solt.practiv.data.local.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.solt.practiv.data.local.database.entities.SearchEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SearchDao {
    @Query("SELECT * FROM SearchEntity ORDER BY title")
      fun getAllItems (): Flow<List<SearchEntity>>

     @Insert(onConflict = OnConflictStrategy.REPLACE)
      suspend fun insertItem(item:SearchEntity):Long

     @Query("SELECT * FROM SearchEntity WHERE title LIKE '%' || :item || '%' OR description LIKE  '%' || :item || '%'")
      suspend fun query(item:String):List<SearchEntity>

}