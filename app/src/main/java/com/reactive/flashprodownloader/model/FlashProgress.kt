package com.reactive.flashprodownloader.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class FlashProgress(
    @PrimaryKey (autoGenerate = true)
    var id: Int,
    @ColumnInfo
    var videoId : Int,
    @ColumnInfo
    var progress: Int
)
