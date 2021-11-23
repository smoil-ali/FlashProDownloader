package com.reactive.flashprodownloader.model

import android.webkit.WebView
import androidx.room.*

@Entity
data class FlashWindow(
    @PrimaryKey (autoGenerate = true)
    var id: Int,
    @ColumnInfo
    var url: String?,
    @ColumnInfo
    var title: String?,
    @ColumnInfo
    var path: String?,
    @ColumnInfo
    var selected: Boolean
)
