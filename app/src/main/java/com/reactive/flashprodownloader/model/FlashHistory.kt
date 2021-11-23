package com.reactive.flashprodownloader.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity
data class FlashHistory(
    @PrimaryKey (autoGenerate = true)
    var id: Int,
    @ColumnInfo
    var url: String,
    @ColumnInfo
    var title: String,
    @ColumnInfo
    var time: String
):Serializable
