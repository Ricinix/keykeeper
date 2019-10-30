package com.example.keykeeper.model.room.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.CASCADE
import androidx.room.PrimaryKey

@Entity(
    tableName = "key_table",
    foreignKeys = [ForeignKey(
        entity = TitleData::class,
        parentColumns = ["name"],
        childColumns = ["category"],
        onDelete = CASCADE,
        onUpdate = CASCADE
    )]
)
data class KeyData (
    @PrimaryKey(autoGenerate = true) val id:Int = 0,
    @ColumnInfo(name = "name") val name:String,
    @ColumnInfo(name = "account") val account: String,
    @ColumnInfo(name = "password") val password: String,
    @ColumnInfo(name = "kind") val kind: String,
    @ColumnInfo(name = "category") val category: String
)

data class KeySimplify(
    val id: Int,
    var name: String,
    var account: String,
    var password: String,
    var kind: String
){
    fun toKeyData(category: String): KeyData{
        return KeyData(id, name, account, password, kind, category)
    }
}