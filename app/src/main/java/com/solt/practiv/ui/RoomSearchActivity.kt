package com.solt.practiv.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.ViewGroup
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.solt.practiv.R
import com.solt.practiv.data.local.database.database.SearchDatabase
import com.solt.practiv.data.local.database.entities.SearchEntity
import com.solt.practiv.databinding.AddSearchItemDialogBinding
import com.solt.practiv.databinding.ListItemBinding
import com.solt.practiv.databinding.SearchRoomActivityBinding
import com.solt.practiv.ui.viewmodel.SearchActivityViewModel
import com.solt.practiv.ui.viewmodel.ViewModelFactory
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlin.random.Random

class RoomSearchActivity:AppCompatActivity(){
    lateinit var binding: SearchRoomActivityBinding
    val searchAdapter = SearchEntityAdapter()
    val queries = MutableSharedFlow<String>(3)

    lateinit var viewModel: SearchActivityViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = SearchRoomActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.listView.apply {
            adapter = searchAdapter
            layoutManager = LinearLayoutManager(this@RoomSearchActivity)

        }
        setSupportActionBar(binding.mainToolbar)
        val room = Room.databaseBuilder(this,SearchDatabase::class.java,"search.db").build()
         viewModel = ViewModelProvider(this,ViewModelFactory(room.searchDao(),queries.asSharedFlow()))[SearchActivityViewModel::class.java]

        //Now listen to the flows
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                //For full list of items
                launch {
                    viewModel.getFullListOfItems().collectLatest {
                        searchAdapter.fullList = it
                    }
                }
                launch {
                    viewModel.currentListOfResultItems.collectLatest {
                        searchAdapter.submitList(it)
                        Log.i("Sagt",it.toString())
                    }
                }
            }
        }

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.search_room_activity_menu,menu)
        val searchView = menu?.findItem(R.id.search_button)?.actionView as SearchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                queries.tryEmit("$query")
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                queries.tryEmit("$newText")
                return true
            }

        })



        return true

    }

    override fun onStart() {
        super.onStart()
        Log.i("Sagt","Activity Started")

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId){
            R.id.add_item->{
                val binding = AddSearchItemDialogBinding.inflate(layoutInflater)
                binding.apply {
                    addButton.setOnClickListener {
                        val title = titleEt.text.toString()
                        val desription = desriptionEt.text.toString()
                        lifecycleScope.launch {
                        viewModel.addItem(SearchEntity(0,title,desription))
                        }
                    }
                }
                AlertDialog.Builder(this).setView(binding.root).setCancelable(true).show()
                true
            }
            else->false
        }
    }

}
val  SearchDiffUtil = object: DiffUtil.ItemCallback<SearchEntity>(){
    override fun areItemsTheSame(oldItem: SearchEntity, newItem: SearchEntity): Boolean {
        return  oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: SearchEntity, newItem: SearchEntity): Boolean {
        return oldItem == newItem
    }

}
class SearchEntityAdapter(): ListAdapter<SearchEntity, SearchEntityAdapter.SearchViewHolder>(SearchDiffUtil)
     {
    var fullList:List<SearchEntity> = emptyList()
        set(value) {
            field = value
            submitList(value)
        }

    inner class SearchViewHolder(val binding: ListItemBinding): RecyclerView.ViewHolder(binding.root){

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchViewHolder {
        val binding = ListItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return SearchViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SearchViewHolder, position: Int) {
        val item = getItem(position)
        holder.binding.apply {
            title.text = item.title
            description.text = item.description
        }
    }


}
