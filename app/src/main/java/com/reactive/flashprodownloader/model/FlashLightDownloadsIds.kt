package com.reactive.flashprodownloader.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


data class FlashLightDownloadsIds(
    val id: Int,
    val downloadId: Int,
    val subjectId: Int,
    var isPause: Boolean
)
