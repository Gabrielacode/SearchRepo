package com.solt.practiv.domain

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.solt.practiv.data.remote.api.DummyService
import com.solt.practiv.data.remote.entities.Recipe
import okio.IOException
import retrofit2.HttpException

class DummyAllPagingSource(val apiService:DummyService):PagingSource<Int,Recipe>() {
    override fun getRefreshKey(state: PagingState<Int, Recipe>): Int? {
     return ((state.anchorPosition?:0)- (state.config.pageSize/2)).coerceAtLeast(0)
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Recipe> {
        val pageKey = params.key?:1
        val pageSize = params.loadSize

        return try {
          val results = apiService.getListOfRecipies(pageSize,pageKey)
            if (results.recipes.isEmpty()){
                LoadResult.Page(results.recipes,if(pageKey>1)pageKey-pageSize else null ,null)
            }else{
                LoadResult.Page(results.recipes,if(pageKey>1)pageKey-pageSize else null , pageKey+pageSize)
            }
        }catch (e:HttpException){
            LoadResult.Error(e)
        }catch (e:IOException){
            LoadResult.Error(e)
        }

    }
}
class DummySearchPagingSource(val apiService:DummyService,  var query:String):PagingSource<Int,Recipe>() {


    override fun getRefreshKey(state: PagingState<Int, Recipe>): Int? {
        return ((state.anchorPosition?:0)- (state.config.pageSize/2)).coerceAtLeast(0)
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Recipe> {
        val pageKey = params.key?:1
        val pageSize = params.loadSize

        return try {
            val results =   apiService.searchRecipies(query,pageSize,pageKey)
            Log.i("Sagt",results.recipes.toString())
            if (results.recipes.isEmpty()){
                LoadResult.Page(results.recipes,if(pageKey>1)pageKey-pageSize else null ,null)
            }else{
                LoadResult.Page(results.recipes,if(pageKey>1)pageKey-pageSize else null , pageKey+pageSize)
            }
        }catch (e:HttpException){
            LoadResult.Error(e)
        }catch (e:IOException){
            LoadResult.Error(e)
        }

    }
}