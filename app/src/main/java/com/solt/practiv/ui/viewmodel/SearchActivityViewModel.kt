package com.solt.practiv.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.solt.practiv.data.local.database.dao.SearchDao
import com.solt.practiv.data.local.database.entities.SearchEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.cache
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch

class SearchActivityViewModel(val dao :SearchDao , listOfQueries:SharedFlow<String>): ViewModel() {
    //This is the full table this is our refrenc to update the recycler view

      var  currentListOfResultItems  = MutableStateFlow<List<SearchEntity>>(emptyList())


    init {
      viewModelScope.launch {
          //This will only be updated by the current full list of items in our database


              listOfQueries.collectLatest {

                  currentListOfResultItems.value = dao.query(it)

              }

      }
    }

    suspend fun addItem(item: SearchEntity){
        dao.insertItem(item)
    }
      fun getFullListOfItems() = dao.getAllItems()



}
class ViewModelFactory(val dao: SearchDao, val sharedFlow: SharedFlow<String>) :ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(SearchActivityViewModel::class.java)) SearchActivityViewModel(dao,sharedFlow) as T
        else super.create(modelClass)
    }
}