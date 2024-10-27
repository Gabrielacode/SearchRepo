package com.solt.practiv.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.paging.PagingData
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.solt.practiv.R
import com.solt.practiv.data.remote.api.BASE_URL
import com.solt.practiv.data.remote.api.DummyService
import com.solt.practiv.data.remote.entities.Recipe
import com.solt.practiv.databinding.ActivityMainBinding
import com.solt.practiv.databinding.ListItemBinding
import com.solt.practiv.ui.viewmodel.RetrofitSearchViewModel
import com.solt.practiv.ui.viewmodel.RetrofitViewModelFactory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.sample
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit

class RetrofitSearchActivity : AppCompatActivity() {
     lateinit var binding :ActivityMainBinding
     val retrofitAdapter = RetrofitSearchAdapter()
     val queriesStateFlow = MutableStateFlow("")
     lateinit var resustStateFlow :StateFlow<PagingData<Recipe>>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.mainToolbar)
        binding.listView.apply {
            layoutManager = LinearLayoutManager(this@RetrofitSearchActivity)
            adapter = retrofitAdapter
        }
        val JsonConverter = Json{ignoreUnknownKeys = true}.asConverterFactory("application/json".toMediaType())
        val retrofit = Retrofit.Builder().baseUrl(BASE_URL).addConverterFactory(JsonConverter).build()
        val viewModel = ViewModelProvider(this,RetrofitViewModelFactory(retrofit.create(DummyService::class.java)))[RetrofitSearchViewModel::class.java]
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
             viewModel.pagedListOfProducts.collectLatest { retrofitAdapter.submitData(it) }
            }
        }
        lifecycleScope.launch {
            queriesStateFlow.collectLatest {
                 viewModel.query(it).collectLatest {
                     retrofitAdapter.submitData(it)
                 }
            }
        }


    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_activity_menu,menu)
        val searchView =  menu?.findItem(R.id.search_button)?.actionView as SearchView
        searchView.apply {
            queryHint = "Search for Recipe"
            setOnQueryTextListener(object:SearchView.OnQueryTextListener{
                override fun onQueryTextSubmit(query: String?): Boolean {
                    queriesStateFlow.value=query?:""
                    return true
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    queriesStateFlow.value=newText?:""
                    return true
                }
            })
        }
        return true
    }

}
val RecipeDiffUtil = object: DiffUtil.ItemCallback<Recipe>() {
    override fun areItemsTheSame(oldItem: Recipe, newItem: Recipe): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Recipe, newItem: Recipe): Boolean {
       return oldItem == newItem
    }

}


class RetrofitSearchAdapter():PagingDataAdapter<Recipe,RetrofitSearchAdapter.RetrofitViewHolder>(RecipeDiffUtil){
    inner class RetrofitViewHolder(val binding:ListItemBinding):ViewHolder(binding.root){}

    override fun onBindViewHolder(holder: RetrofitViewHolder, position: Int) {
     holder.binding.apply {
         getItem(position).also {
             if (it !=null){
                this.title.text = it.name
                this.description.text =it.cuisine
             }
         }
     }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RetrofitViewHolder {
        val binding = ListItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return RetrofitViewHolder(binding)
    }
}