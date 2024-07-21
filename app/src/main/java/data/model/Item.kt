package data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "items")
data class Item(
    @ColumnInfo(name = "content")
    var title: String,

    @ColumnInfo(name = "content_desc")
    var description: String,

    @ColumnInfo(name = "image")
    var photo: String?
) {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
}
