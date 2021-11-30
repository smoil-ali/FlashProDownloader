package com.reactive.flashprodownloader.Room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.reactive.flashprodownloader.model.*

@Database(entities = [FlashWindow::class,FlashBookmark::class,FlashHistory::class,
    FlashLightDownload::class,FlashProgress::class],version = 1)
abstract class AppDatabase: RoomDatabase() {
    abstract fun windowDao(): FlashDao
}