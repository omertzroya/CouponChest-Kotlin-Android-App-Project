package data.repository

import android.app.Application
import data.local_db.ItemDao
import data.local_db.ItemDataBase
import data.model.Item
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ItemRepository(application: Application) {

    private val itemDao: ItemDao? = ItemDataBase.getDatabase(application.applicationContext)?.itemsDao()

    suspend fun addItem(item: Item) {
        withContext(Dispatchers.IO) {
            itemDao?.addItem(item)
        }
    }

    suspend fun updateItem(item: Item) {
        withContext(Dispatchers.IO) {
            itemDao?.updateItem(item)
        }
    }

    suspend fun deleteItem(item: Item) {
        withContext(Dispatchers.IO) {
            itemDao?.deleteItem(item)
        }
    }

    suspend fun deleteAll() {
        withContext(Dispatchers.IO) {
            itemDao?.deleteAll()
        }
    }

    fun getItems() = itemDao?.getItems()
}
