package com.solt.practiv.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.paging.InvalidatingPagingSourceFactory
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.solt.practiv.data.remote.api.DummyService
import com.solt.practiv.data.remote.entities.Recipe
import com.solt.practiv.domain.DummyAllPagingSource
import com.solt.practiv.domain.DummySearchPagingSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class RetrofitSearchViewModel( private val apiService:DummyService, ) :ViewModel() {
   val pagedListOfProducts : Flow<PagingData<Recipe>>
           get()= Pager(PagingConfig(10)){DummyAllPagingSource(apiService)}.flow.cachedIn(viewModelScope)






    //For each query we return a new Pager and in the activity it will be converted to a staeflow so that can we can get the current paging data of the search without storing it in the adapter
     fun query(search:String) = Pager(PagingConfig(10)){ DummySearchPagingSource(apiService,search) }.flow.cachedIn(viewModelScope)

}
class RetrofitViewModelFactory(val apiService:DummyService):ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if(modelClass.isAssignableFrom(RetrofitSearchViewModel::class.java)) RetrofitSearchViewModel(apiService) as T
        else super.create(modelClass)
    }
}