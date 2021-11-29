package com.reactive.flashprodownloader.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity
data class FlashLightDownload(
    @PrimaryKey (autoGenerate = true)
    var id: Int,
    @ColumnInfo
    val url: String,
    @ColumnInfo
    val title: String,
    @ColumnInfo
    val path: String,
    @ColumnInfo
    val size: String,
    @ColumnInfo
    val bytes: Int,
    @ColumnInfo
    var selected: Boolean,
    @ColumnInfo
    val status: Boolean

): Serializable
