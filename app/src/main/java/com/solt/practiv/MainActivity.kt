package com.solt.practiv

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.appcompat.widget.SearchView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.solt.practiv.databinding.ActivityMainBinding
import com.solt.practiv.databinding.ListItemBinding

class MainActivity : AppCompatActivity() {
    lateinit var binding:ActivityMainBinding
    val searchAdapter = SearchAdapter()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.mainToolbar)
        binding.listView.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = searchAdapter
            val listofData = listOf<SearchItem>(
                SearchItem("Macaroni","Delicious Macaroni"),
                SearchItem("Rice","Delicious Rice"),
                SearchItem("Pizza","Delicious Pizza"),
                SearchItem("Beans","Delicious Beans"),
                SearchItem("Spaghetti","Delicious Spaghetti") ,
                SearchItem("Indomie","Delicious Indomie"),
                SearchItem("Abacha","Delicious Delicious"),
                SearchItem("Soup","Delicious Soup"),
                SearchItem("Afang","Delicious Afang"),
                SearchItem("Ogoniio","Delicious Ogoniio"),
                SearchItem("Basdo","Delicious Basdo")
            )
            searchAdapter.setListOriginal(listofData)
        }



    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
      menuInflater.inflate(R.menu.main_activity_menu,menu)
        val searchView = menu?.getItem(0)?.actionView as SearchView
        searchView.setOnQueryTextListener(object :SearchView.OnQueryTextListener{
                    override fun onQueryTextSubmit(query: String?): Boolean {
                        searchAdapter.filter.filter(query)
                        return true
                    }

                    override fun onQueryTextChange(newText: String?): Boolean {
                        searchAdapter.filter.filter(newText)
                        return true
                    }

                })

        return true
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        return when(item.itemId){
//            R.id.search_button ->{
//                val searchView  = item.actionView as SearchView
//
//
//
  return false

    }
    }

data class SearchItem(val title:String,val description:String)
val  SearchDiffUtil = object:DiffUtil.ItemCallback<SearchItem>(){
    override fun areItemsTheSame(oldItem: SearchItem, newItem: SearchItem): Boolean {
       return oldItem.title == newItem.title
    }

    override fun areContentsTheSame(oldItem: SearchItem, newItem: SearchItem): Boolean {
       return  oldItem == newItem
    }

}
class SearchAdapter(): ListAdapter<SearchItem,SearchAdapter.SearchViewHolder>(SearchDiffUtil),Filterable{
    var originalListCopy :List<SearchItem> = emptyList()
    fun setListOriginal(list:List<SearchItem>){
        originalListCopy = list
        submitList(list)
    }


    inner class SearchViewHolder(val binding:ListItemBinding):ViewHolder(binding.root){

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

    override fun getFilter(): Filter {
        val filter = object :Filter(){
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                 val originalList = currentList
                return FilterResults().apply {
                    values =  if (constraint.toString().isBlank()) originalListCopy
                    else originalList.onEach { it.title.lowercase();it.description.lowercase()}.filter { it.title.contains(constraint.toString(),true)||it.description.contains(constraint.toString(),true) }
                }
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {

               val list:List<SearchItem> = results?.values as? List<SearchItem> ?: originalListCopy
                submitList(list)
            }

        }
        return filter

    }
}
//So today we will be learning  how to use a search view with
//A recycler view
// We are going to try it with room database lets see whether it works
//We might even try it with a paging source

//And using the searchable activity