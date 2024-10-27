package com.solt.practiv.data.remote.api

import com.solt.practiv.data.remote.entities.DummyJson
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.Url

interface DummyService {
    @GET("recipes/")
      suspend fun getListOfRecipies(@Query("limit") limit:Int,@Query("skip") skip:Int):DummyJson
     @GET("recipes/search")
     suspend fun searchRecipies( @Query("q") query:String,@Query("limit") limit:Int,@Query("skip") skip:Int):DummyJson
}

const val BASE_URL ="https://dummyjson.com/"