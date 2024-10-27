package com.solt.practiv.data.local.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
 data class SearchEntity
     (
    @PrimaryKey(true)
    val id :Long,
    val title:String,
      val description:String) {
}