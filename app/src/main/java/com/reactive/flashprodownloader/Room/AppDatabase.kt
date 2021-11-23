package com.reactive.flashprodownloader.Room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.reactive.flashprodownloader.model.FlashBookmark
import com.reactive.flashprodownloader.model.FlashHistory
import com.reactive.flashprodownloader.model.FlashLightDownload
import com.reactive.flashprodownloader.model.FlashWindow

@Database(entities = [FlashWindow::class,FlashBookmark::class,FlashHistory::class,
    FlashLightDownload::class],version = 1)
abstract class AppDatabase: RoomDatabase() {
    abstract fun windowDao(): FlashDao
}