package com.solt.practiv.ui

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.Room
import com.solt.practiv.R
import com.solt.practiv.data.local.database.database.SearchDatabase
import com.solt.practiv.data.local.database.entities.SearchEntity
import com.solt.practiv.databinding.AddSearchItemDialogBinding
import com.solt.practiv.databinding.SearchActivityBinding
import com.solt.practiv.ui.viewmodel.SearchActivityViewModel
import com.solt.practiv.ui.viewmodel.ViewModelFactory
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class SearchableActivity: AppCompatActivity() {
    lateinit var binding: SearchActivityBinding
    val searchAdapter = SearchEntityAdapter()
    lateinit var viewModel: SearchActivityViewModel
    val queries = MutableSharedFlow<String>(3)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = SearchActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.mainToolbar)
        this.setDefaultKeyMode(DEFAULT_KEYS_SEARCH_LOCAL)
        handleIntent(intent)
        binding.searchDialog.setOnClickListener {
            onSearchRequested()
        }
        binding.searchDialog.setOnLongClickListener {
            searchAdapter.submitList(searchAdapter.fullList)
            true
        }
        binding.listView.apply {
            layoutManager = LinearLayoutManager(this@SearchableActivity)
            adapter =searchAdapter
        }
        val room = Room.databaseBuilder(this, SearchDatabase::class.java,"search.db").build()
        viewModel = ViewModelProvider(this,
            ViewModelFactory(room.searchDao(),queries.asSharedFlow())
        )[SearchActivityViewModel::class.java]
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
        val searchManager = this.getSystemService(Context.SEARCH_SERVICE) as SearchManager

        searchView.apply {
            this.setSearchableInfo(searchManager.getSearchableInfo(this@SearchableActivity.componentName))
            setIconifiedByDefault(false)
            isSubmitButtonEnabled = true
            this.setOnCloseListener {
                searchAdapter.submitList(searchAdapter.fullList)
                true
            }

        }

        return true
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
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent) // We set it because the activity is not restarted so a new intent is not passed
        handleIntent(intent)
    }


    fun handleIntent(intent: Intent){
        if(intent.action == Intent.ACTION_SEARCH){
            //Get the query
            val query = intent.getStringExtra(SearchManager.QUERY)
            queries.tryEmit(query?:"")
        }
    }
}
//Today we will looking at how Android requires on how to search
//Here we can do voice search and search suggestions from recent queries and app data
//There are two different ways of searching
/*
* 1 Search Dialog
* 2. Search Widget
* To handle search in Android we will need
* 1. a search configuration xml file ->Details on how to handle search , hint and voice search
* 2. a search activity  -> where all the search queries go to
* 3. a search interfave -> Search dialog or search view
* Lets create the search interface first
*We can use this activity as the both the main and search activity
*  Now lets us recieve the query
* Since we are using single top to do the results we use onNewIntent*/