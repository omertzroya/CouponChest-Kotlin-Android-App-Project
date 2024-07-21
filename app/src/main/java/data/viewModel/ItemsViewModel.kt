package data.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import data.model.Item
import data.repository.ItemRepository
import kotlinx.coroutines.launch

class ItemsViewModel (application: Application) : AndroidViewModel(application) {

    private val repository = ItemRepository(application)

    val items : LiveData<List<Item>>? = repository.getItems()


    private val _selectedItem = MutableLiveData<Item>()
    val selectedItem: LiveData<Item> get() = _selectedItem

    fun setItem(item:Item){
        _selectedItem.value = item
    }
    fun addItem(item:Item){
        viewModelScope.launch {
            repository.addItem(item)
        }
    }

    fun deleteItem(item: Item)
    {
        viewModelScope.launch {
            repository.deleteItem(item)
        }
    }
    fun updateItem(item: Item) {
        viewModelScope.launch {
            repository.updateItem(item)
        }
    }
    fun deleteAll(){
        viewModelScope.launch{
            repository.deleteAll()
        }
    }
}