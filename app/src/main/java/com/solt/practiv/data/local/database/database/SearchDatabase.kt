package com.solt.practiv.data.local.database.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.solt.practiv.data.local.database.dao.SearchDao
import com.solt.practiv.data.local.database.entities.SearchEntity

@Database(entities = [SearchEntity::class], version = 1)
 abstract class SearchDatabase  :RoomDatabase(){
  abstract fun searchDao():SearchDao
}