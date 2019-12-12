package com.example.keykeeper.model.room.data

import androidx.room.*
import androidx.room.ForeignKey.CASCADE

@Entity(
    tableName = "key_table",
    foreignKeys = [ForeignKey(
        entity = TitleData::class,
        parentColumns = ["title_name"],
        childColumns = ["category"],
        onDelete = CASCADE,
        onUpdate = CASCADE
    )],
    indices = [Index("category", name = "index_category")]
)
data class KeyData (
    @ColumnInfo(name = "name") var name:String,
    @ColumnInfo(name = "account") var account: String,
    @ColumnInfo(name = "password") var password: String,
    @ColumnInfo(name = "kind") var kind: String,
    @ColumnInfo(name = "category") var category: String,
    @PrimaryKey(autoGenerate = true) val id:Int = 0
)

data class KeySimplify(
    var name: String,
    var account: String,
    var password: String,
    var kind: String,
    val id: Int = 0
){
    fun toKeyData(category: String): KeyData{
        return KeyData(name, account, password, kind, category, id)
    }
}