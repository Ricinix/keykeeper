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
    @ColumnInfo(name = "name") val name:String,
    @ColumnInfo(name = "account") val account: String,
    @ColumnInfo(name = "password") val password: String,
    @ColumnInfo(name = "kind") val kind: String,
    @ColumnInfo(name = "category") val category: String,
    @PrimaryKey(autoGenerate = true) val id:Int = 0
)

data class KeySimplify(
    val id: Int,
    var name: String,
    var account: String,
    var password: String,
    var kind: String
){
    fun toKeyData(category: String): KeyData{
        return KeyData(name, account, password, kind, category, id)
    }
}